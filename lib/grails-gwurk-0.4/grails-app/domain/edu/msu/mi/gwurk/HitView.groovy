package edu.msu.mi.gwurk

import com.amazonaws.services.mturk.AmazonMTurkClient
import com.amazonaws.services.mturk.model.GetHITRequest
import com.amazonaws.services.mturk.model.HIT
import com.amazonaws.services.mturk.model.HITStatus
import com.amazonaws.services.mturk.model.ListAssignmentsForHITRequest
import com.amazonaws.services.mturk.model.UpdateExpirationForHITRequest
import groovy.util.logging.Log4j

@Log4j
class HitView {

    static constraints = {

    }

    static belongsTo = [taskRun:TaskRun]
    static hasMany = [assignments:AssignmentView]

    static enum Status {
        AVAILABLE, UNAVAILABLE, REVIEWABLE, FINISHED
    }



    String hitId
    String hitGroup
    Date creationTime
    Status hitStatus

    HitView(TaskRun run, HIT hit) {
        hitId = hit?.HITId
        hitGroup = hit?.HITGroupId
        creationTime = hit?.creationTime?:new Date()
        hitStatus = Status.AVAILABLE
        taskRun = run
        save()
    }

    long getAge() {
        System.currentTimeMillis() - creationTime.time
    }



    def update(AmazonMTurkClient requesterService) {

        HIT h = hitId?requesterService.getHIT(new GetHITRequest().withHITId(hitId)).HIT:null
        if (h) {
            switch (HITStatus.fromValue(h.getHITStatus())) {

                case HITStatus.Assignable:
                    hitStatus = Status.AVAILABLE
                    break
                case HITStatus.Unassignable:
                    hitStatus = Status.UNAVAILABLE
                    break
                case HITStatus.Reviewable:
                    hitStatus = Status.REVIEWABLE
                    break
                case HITStatus.Disposed:
                    hitStatus = Status.FINISHED
            }
            save()
            def known = assignments*.assignmentId as Set

            def awsAssts = requesterService.listAssignmentsForHIT(new ListAssignmentsForHITRequest().withHITId(hitId))
            log.info "Retrieved assignments from service: ${awsAssts.getNumResults()}"
            awsAssts.assignments.findAll { it && !known.contains(it.assignmentId) }.each {
                log.info("Adding assignment $it")
                addToAssignments(new AssignmentView(it))
            }
            assignments.each {
                if (it.assignmentStatus == AssignmentView.Status.SUBMITTED) it.update(requesterService)
            }
            save()
        } else {
            log.warn("Could not identify hit : ${hitId}")
        }

    }

    AssignmentView[] getSubmittedAssignments() {
        return assignments.findAll {it.assignmentStatus == AssignmentView.Status.SUBMITTED}
    }

    AssignmentView[] getReviewedAssignments() {
        return assignments.findAll {it.assignmentStatus != AssignmentView.Status.SUBMITTED}
    }

    AssignmentView[] getRejectedAssignments() {
        return assignments.findAll {it.assignmentStatus != AssignmentView.Status.REJECTED}
    }

    AssignmentView[] getApprovedAssignments() {
        return assignments.findAll {it.assignmentStatus != AssignmentView.Status.APPROVED}
    }

    def expire(AmazonMTurkClient requesterService) {
        requesterService.updateExpirationForHIT(new UpdateExpirationForHITRequest().withHITId(hitId).withExpireAt(new Date()))
        update(requesterService)
        save()
    }

}
