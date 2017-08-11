package edu.msu.mi.gwurk

import com.amazonaws.services.mturk.AmazonMTurkClient
import groovy.util.logging.Log4j

@Log4j
class MultiHitTask extends Task {

    static constraints = {
    }


    MultiHitTask(String name, Map props) {
        super(name, props)
    }

    @Override
    def start(AmazonMTurkClient service, TaskRun runner) {

        kickoffHits(service,runner)
    }


    @Override
    def update(AmazonMTurkClient service, TaskRun runner) {

        mturkAwsFacadeService.refresh(service, runner)
        log.info "All assignments ${runner.allHits*.assignments}"
        runner.allHits*.assignments.flatten().each { AssignmentView av ->
            if (!av) {
                log.info("Why is there a null entity?")
            } else {
                if (!av.processed) {
                    if (!runner.taskProperties.requireApproval || av.assignmentStatus == AssignmentView.Status.APPROVED) {
                        mturkTaskService.onAssignment(runner,av)
                        av.processed = true
                        av.save()
                    }
                }
            }
        }

        ([]+runner.activeHits.hits).each { HitView v ->
           if (v.hitStatus in [HitView.Status.REVIEWABLE, HitView.Status.FINISHED]) {
               mturkTaskService.onHit(runner,v)
               runner.removeActive(v)
           }
        }

        if (runner.hasAllAssignments() || runner.taskStatus == TaskRun.Status.ABORTED) {
            runner.activeHits.hits.each {
                mturkTaskService.onHit(runner, it)

            }
            runner.taskStatus = TaskRun.Status.COMPLETE
        } else if (runner.hasPendingAssignments()) {
            runner.taskStatus = TaskRun.Status.NEEDS_INPUT
        } else if (runner.activeHits.hits.size() == 0 && (runner.allHits*.assignments*.size()).sum() < runner.taskProperties.maxAssignments) {
            log.info runner.activeHits
            kickoffHits(service,runner)
        }
        save()


    }

    def kickoffHits(AmazonMTurkClient svc, TaskRun runner) {
        int remaining = Math.ceil(runner.taskProperties.maxAssignments / runner.taskProperties.assignmentsPerHit) - runner.allHits.size()

        int toLaunch = Math.min(remaining,runner.taskProperties.batchSize)

        (1..toLaunch).each {
            mturkAwsFacadeService.launchOneHit(svc, runner)
        }

    }
}
