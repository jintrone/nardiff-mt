package nardiff.mt

import grails.transaction.Transactional


@Transactional
class NardiffService {

    List branching = [2, 2, 2]

    def setBranching(List branching) {
        this.branching = new ArrayList(branching)
    }

    def int getBranchingFactor(int level) {
        level < branching.size() ? branching[level] : 1
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
        Collections.shuffle(available)
        List<Narrative> expandable = available.sum { NarrativeSeed seed ->
            Narrative.findAllByRoot_narrativeAndExpanding(seed, true)
        }
        if (expandable) {
            expandable.sort { l, r ->
                def children = [l, r].collect {
                    def data = new HashSet(it.children ?: Collections.emptySet())
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

        return expandable ? expandable.first() : null
    }

    /**
     * Delays update until a pessimistic lock can be obtained.  Not generally a good idea
     *
     * @param object
     * @param update
     * @param maxAttempts
     */
    def slowUpdate(Object object, Closure update) {
        int maxAttempts = 20
        int failure = 0
        while (failure > -1 && failure < maxAttempts) {
            try {
                object = object.lock(object.id)
                failure = -1
                update(object)
                object.save([flush: true, failOnError: true])

            } catch (Exception ex) {
                Thread.sleep(100)
                object.refresh()
                failure++
            }
        }
        if (failure>=maxAttempts) {
            log.error("Error updating object ${object}")
        }

    }


    def Narrative openNarrative(String workerId, String assignmentId) {
        pruneNarratives()
        Narrative n = Narrative.findByAssignmentId(assignmentId)
        if (n) {
            n
        } else {
            Narrative parent = findNarrativeToExpandForWorker(workerId)
            if (parent) {
                Narrative narr = new Narrative(parent, assignmentId, workerId).save([flush: true, failOnError: true])
                NarrativeSeed seed = parent.root_narrative
                slowUpdate(seed, { NarrativeSeed s -> s.addToNarratives(narr) })
                slowUpdate(parent, { Narrative p -> p.addToChildren(narr) })
                narr
            } else {
                null
            }
        }
    }

    def finalizeNarrative(Narrative n, Map data) {
        NarrativeData d = n.data
        d.text = data.text
        d.distractor_answer = data.distractorAnswer
        d.time_distrator = Integer.parseInt(data.timeDistractor)
        d.time_reading = Integer.parseInt(data.timeReading)
        d.time_writing = Integer.parseInt(data.timeWriting)
        d.stage = 7
        d.save([flush:true,failOnError: true])

        slowUpdate(n) {
            it.closed = new Date()
            it.too_simple = isAnswerTooSimple(d.text, it.parent_narrative.data.text, it.depth)
        }
    }


     def updateExpansion(Narrative n) {
         List<Narrative> siblings = Narrative.findAllByParent_narrativeAndClosedNotIsNotNull(n.parent_narrative)
        if (siblings.size() >= getBranchingFactor(n.parent_narrative.depth)) {
            n.parent_narrative.expanding = false
            siblings.each {Narrative s->
                slowUpdate(s) {
                    it.expanding = true
                }
            }

        }
        n
    }


    boolean isAnswerTooSimple(String test, String parent, int depth) {
        (!test || test.length() <= 120 || test == parent)
    }

}
