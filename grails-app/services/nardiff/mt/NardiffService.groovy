package nardiff.mt

import edu.msu.mi.gwurk.AssignmentView
import grails.transaction.Transactional

@Transactional
class NardiffService {

    List branching = [2,2,2]

    def setBranching(List branching) {
        this.branching = new ArrayList(branching)
    }

    def int getBranchingFactor(int level) {
        level<branching.size()?branching[level]:1
    }

    def pruneNarratives() {
        Date firstAcceptableTime = new Date(System.currentTimeMillis() - 1000 * 60 * 10)
        Narrative.executeUpdate("update Narrative set abandoned = true where abandoned=false and closed is NULL and opened < ?", [firstAcceptableTime])
    }


    /**
     * each level must be complete before expanding the narrative at that level (see {@link NardiffService#finalizeNarrative})
     * seek to add nodes to parents with fewer children than branching factor first
     * otherwise, just pick the parents with the least number of children
     **/
    def Narrative findNarrativeToExpandForWorker(String workerid) {
        List<NarrativeSeed> available = NarrativeSeed.list() - Narrative.findAllByWorkerId(workerid)*.root_narrative
        List<Narrative> expandable = available.sum { NarrativeSeed seed ->
            Narrative.findAllByRoot_narrativeAndExpanding(seed, true)
        }
        if (expandable) {
            expandable.sort { l, r ->
                def children = [l, r].collect {
                    def data = new HashSet(it.children?:Collections.emptySet())
                    data.removeAll {
                        it.abandoned
                    }
                    data
                }
                int lbf = getBranchingFactor(l.depth), rbf = getBranchingFactor(r.depth)
                if (children[0].size() < lbf == children[1].size() < rbf) {
                    children[0].size() <=> children[1].size()
                } else (children[1].size() < rbf) ? 1 : -1

            }
        }

        return expandable?expandable.first():null
    }

    def Narrative openNarrative(String workerId, String assignmentId) {
        pruneNarratives()
        Narrative n = Narrative.findByAssignmentId(assignmentId)
        if (n) {
            n
        } else {
            Narrative parent = findNarrativeToExpandForWorker(workerId)
            parent ? new Narrative(parent, assignmentId, workerId).save([flush: true, failOnError: true]) : null
        }
    }

    def finalizeNarrative(Narrative n, Map data) {
        n.closed = new Date()
        n.text = data.text
        n.distractor_answer = data.distractorAnswer
        n.time_distrator = Integer.parseInt(data.timeDistractor)
        n.time_reading = Integer.parseInt(data.timeReading)
        n.time_writing = Integer.parseInt(data.timeWriting)
        n.too_simple = isAnswerTooSimple(n.text,n.parent_narrative.text,n.depth)
        n.stage = 7
        n.save(flush: true, failOnError: true)

        List<Narrative> siblings = Narrative.findAllByParent_narrativeAndClosedNotIsNotNull(n.parent_narrative)
        if (siblings.size()>=getBranchingFactor(n.parent_narrative.depth)) {
            n.parent_narrative.expanding = false
            siblings.each {
                it.expanding = true
                it.save([flush:true,failOnError:true])
            }

        }
        n
    }



    boolean isAnswerTooSimple(String test, String parent, int depth) {
        (!test || test.length() <= 120 || test == parent)
    }

}
