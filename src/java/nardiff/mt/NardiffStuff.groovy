package nardiff.mt

import edu.msu.mi.gwurk.AssignmentView
import grails.transaction.Transactional
import org.apache.commons.lang3.StringUtils
import org.apache.commons.logging.LogFactory

/**
 * Ugh, yes, it's mostly static stuff in a single class, but whatever.
 *
 * Created by kkoning on 2/24/15.
 */
class NardiffStuff {

    static List branching = [2,2,2]


    public static NarrativeRequest findRequest(Long root_story_id) {
        NarrativeRequest.withTransaction { tx ->
            Date now = new Date();
            Date cutoff = new Date(now.getTime() - (10 * 60 * 1000L)) // 10 minutes ago
            List expiredRequests = NarrativeRequest.findAll("from NarrativeRequest nr where nr.when_assigned < :t", [t: cutoff]);
            for (Object o : expiredRequests) {
                NarrativeRequest expiredRequest = (NarrativeRequest) o;
                LogFactory.getLog(this).info("NarrativeRequest " + expiredRequest.id + " was assigned to " +
                    expiredRequest.assigned_to.mturk_id + ", but not completed after 10 minutes, so made available again.");
                expiredRequest.assigned_to = null;
                expiredRequest.when_assigned = null;
                expiredRequest.save();
            }

            NarrativeRequest.find("from NarrativeRequest nr where nr.root_narrative.id = :rid and assigned_to is null order by priority", [rid: root_story_id]);
        }

    }

    public static boolean assignRequestToTurker(NarrativeRequest nr, String turker_id, String assignmentID) {
        boolean collectTurkerData = true;
        NarrativeRequest.withTransaction { tx ->
            Turker turker = Turker.findByMturk_id(turker_id);
            if (turker == null) {
                turker = new Turker();
                turker.mturk_id = turker_id;
            } else {
                if ( (turker.age != null) & (turker.education != null) & (turker.gender != null) )
                    collectTurkerData = false;
            }

            nr.attach();
            nr.assigned_to = turker;
            nr.when_assigned = new Date();
            nr.assignment_id = assignmentID;

            turker.save flush: true, failOnError: true;

            nr.save flush: true, failOnError: true;




        }

        return collectTurkerData;
    }

    static int getDesiredBranchingFactor(int depth) {
       depth>branching.size()?1:branching[depth]
    }

    static Narrative getNarrativeToExpand(long l) {
        List narratives  = Narrative.where {
            depth == max(depth) && id==l && (too_simple==false)
        }.list()
        if (narratives.first().depth == 0) {
            return narratives.first()
        }

        Map<Narrative,List<Narrative>> families = [:]
        narratives.each{Narrative n->
            families[(n.parent_narrative)] = (families[(n.parent_narrative)]?:[])+n
        }

        //logic here as follows:
        //-- accumulate all parents and their valid children
        //-- if any don't have the desired number, go ahead and add a child
        //-- otherwise, see if there are any uncles (or aunts) without children, and return one of those
        //-- otherwise, just pick the first parent and keep adding children to the poor sap
        families.find {k,v -> v.size() < getDesiredBranchingFactor(k.depth)}?.key?:{
            List<Narrative> uncles = Narrative.findAllByDepth(families.keySet().first().depth) - families.keySet()
            if (uncles) {
                uncles.first()
            } else {
                families.keySet().first()
            }
        }()

    }

    static boolean isAnswerTooSimple(String test, String parent, int depth) {
       (!test || test.length()<=120 || test==parent)?:{
           //OLD CODE - probably not worth including, but keeping around just in case

//           int largerSize = parentText.length();
//           if (childText.length() > largerSize)
//               largerSize = childText.length();
//
//           // looking for a 10% edit distance
//           int maxDistToCheck = largerSize * 0.10;
//
//           // if distance is above max distance, contains -1
//           int rawDistance = StringUtils.getLevenshteinDistance(childText, parentText, maxDistToCheck);
//
//           // we only want to continue if it's above the max (10% distance)
//           rawDistance >= 0
           false
       }()
    }

    void doInsert(AssignmentView view) {
        Narrative.withTransaction { tx ->
            System.out.println("Processing a completed assignment, params = " + params.toString());
            Narrative parent = Narrative.get(Long.parseLong(view.answer.parent))
            Narrative child = new Narrative(parent, view.assignmentId)
            child.text = view.answer.story
            child.distractor_answer = view.answer.distractorAnswer
            child.time_distrator = view.answer.distractorTime as int
            child.time_writing = view.answer.storyTime as int
            child.time_reading = view.answer.retellTime as int
            child.too_simple = isAnswerTooSimple(child.text, parent.text, child.depth)
            child.save()
        }

    }

    void oldInsert(Map params) {
        NarrativeRequest.withTransaction { tx ->


            System.out.println("Processing a completed assignment, params = " + params.toString());
            NarrativeRequest nr = NarrativeRequest.findById(Long.parseLong((String) params.get("request_id")));
            nr.when_completed = new Date();
            nr.save();

            Turker worker = nr.getAssigned_to();
            String age = params.get("age");
            if (age != null) {
                if (!age.equals("")) {
                    worker.age = Integer.parseInt(age);
                    worker.gender = params.get("gender");
                    worker.education = Integer.parseInt((String) params.get("education"));
                    worker.save();
                }
            }

            Narrative parentNarrative = nr.getParent_narrative();

            Narrative thisNarrative = new Narrative();
            thisNarrative.parent_narrative = parentNarrative;
            thisNarrative.root_narrative_id = parentNarrative.root_narrative_id;
            thisNarrative.completed_by = worker;
            thisNarrative.text = params.get("story");
            thisNarrative.distractorTask = parentNarrative.distractorTask
            thisNarrative.distractor_answer = params.get("distractorAnswer");
            try {
                thisNarrative.time_distrator = Integer.parseInt(params.get("distractorTime"));
            } catch (Exception e) {
                System.out.println("Error processing distractor times for story: " + e);
            }
            try {
                thisNarrative.time_writing = Integer.parseInt(params.get("retellTime"));
            } catch (Exception e) {
                System.out.println("Error processing retelling times for story: " + e);
            }
            try {
                thisNarrative.time_reading = Integer.parseInt(params.get("storyTime"));
            } catch (Exception e) {
                System.out.println("Error processing reading times for story: " + e);
            }
            thisNarrative.too_simple = false;


            thisNarrative.save();

            System.out.println("Finished processing answer, saved constructed narrative = " + thisNarrative);


            // Only insert more child requests if it wasn't too simple.
            if (thisNarrative.too_simple)
                return;

            // TODO Decide whether or not to accept this task and create new child tasks.
            String parentText = parentNarrative.text;
            String childText = thisNarrative.text;

            // if it's a perfect match, stop
            if (parentText.equalsIgnoreCase(childText))
                return;

            // if it's just REALLY short, also stop.
            if (!childText || childText.length() < 120)
                return;

            // Else, try calculating levenstein distance.
            // find the larger of the two strings
            int largerSize = parentText.length();
            if (childText.length() > largerSize)
                largerSize = childText.length();

            // looking for a 10% edit distance
            int maxDistToCheck = largerSize * 0.10;

            // if distance is above max distance, contains -1
            int rawDistance = StringUtils.getLevenshteinDistance(childText, parentText, maxDistToCheck);

            // we only want to continue if it's above the max (10% distance)
            if (rawDistance >= 0)
                return;

            // Assuming we are creating child tasks.
            def branchingLevels = [1: 5, 2: 5, 3: 5, 4: 1, 5: 1, 6: 1, 7: 1, 8: 0, 9: 0, 10: 0];

            int tasksToAdd = branchingLevels.get(nr.depth);
            int newNarrativeDepth = nr.depth + 1;
            int newPriority = nr.priority + 1;
            if (nr.priority > 50)
                newPriority = nr.priority - 1;

            for (int i = 0; i < tasksToAdd; i++) {
                NarrativeRequest newRequest = new NarrativeRequest();
                newRequest.priority = newPriority;
                newRequest.depth = newNarrativeDepth;
                newRequest.root_narrative = nr.root_narrative;

                //this is a problem
                newRequest.parent_narrative = thisNarrative;
                newRequest.save();
            }
        }


    }



}
