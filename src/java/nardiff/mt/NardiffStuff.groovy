package nardiff.mt

import org.apache.commons.logging.LogFactory

/**
 * Ugh, yes, it's mostly static stuff in a single class, but whatever.
 *
 * Created by kkoning on 2/24/15.
 */
class NardiffStuff {


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



}
