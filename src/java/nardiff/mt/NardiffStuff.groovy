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

    static List branching = [2, 2, 2]


    static int getDesiredBranchingFactor(int depth) {
        //depth > branching.size() ? 1 : branching[depth]
        1
    }

    static Narrative getNarrativeToExpand(long l) {
        int maxdepth = Narrative.executeQuery("select max(depth) from Narrative where root_narrative.id = ?", [l]).first()

        println("Narrative root ${l} and maxdepth ${maxdepth}")

        List<Narrative> narratives = Narrative.where {
            depth == maxdepth && root_narrative.id == l
        }.list()

        println "Found ${narratives*.id}"

        if (narratives.first().depth == 0) {
            return narratives.first()
        }



        Map<Narrative, List<Narrative>> families = [:]
        narratives.each { Narrative n ->
            if (!families[(n.parent_narrative)]) {
                families[(n.parent_narrative)] = []
            }
            if (!n.too_simple) {
                families[(n.parent_narrative)] << n
            }
        }

        //logic here as follows:
        //-- accumulate all parents and their valid children
        //-- if any don't have the desired number, go ahead and add a child to that one
        //-- otherwise, see if there are any uncles (or aunts) without children, and return one of those
        //-- otherwise, we're complete, so add a new level
        families.find { k, v -> v.size() < getDesiredBranchingFactor(k.depth) }?.key ?: {
            List<Narrative> uncles = Narrative.findAllByDepthAndRoot_narrative(families.keySet().first().depth, Narrative.get(l)) - families.keySet()
            if (uncles) {
                uncles.first()
            } else {
                //level complete!
                narratives.first()
            }
        }()

    }

    static boolean isAnswerTooSimple(String test, String parent, int depth) {
        (!test || test.length() <= 120 || test == parent) ?: {
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


}
