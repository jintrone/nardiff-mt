package edu.msu.mi.gwurk


import com.amazonaws.services.mturk.AmazonMTurkClient
import groovy.util.logging.Log4j

@Log4j
class TaskRun implements BeatListener{

    static constraints = {

        allHits nullable: true
        activeHits nullable: true
        previousTaskRuns nullable: true
        activeWorkflowRun nullable: true

    }

    //Note workflowRun and activeWorkflowRun will never point to two different workflows; activeWorkflowRun is only included
    //to speed up access to active taskruns in a running workflow
    static belongsTo = [workflowRun:WorkflowRun]
    static hasMany = [allHits:HitView,previousTaskRuns:TaskRun]
    static mappedBy = [allHits:"taskRun"]


    WorkflowRun activeWorkflowRun
    Task task
    Status taskStatus
    ActiveHits activeHits
    TaskProperties taskProperties
    Map<String,String> userProperties = [:]


    public TaskRun(Task task, TaskProperties props) {
        this.task = task
        this.taskProperties = props
        this.taskStatus = Status.WAITING
        this.activeHits = new ActiveHits()
        log.info "Newly created: ${System.identityHashCode(this)} id: $id"
    }

    def mturkAwsFacadeService
    def mturkTaskService

    /**
     * Checks if there are enough assignments to complete this task
     * @return
     */
    boolean hasAllAssignments() {
        log.debug("Searching for all assignments")
        log.debug("Looking for  ${taskProperties.maxAssignments} assignments")
        log.debug("Approval required by task? "+ taskProperties.requireApproval)
        def assignments = allHits*.assignments.flatten().findAll {
            log.debug("Examining $it; has status ${it.assignmentStatus}")

            taskProperties.requireApproval?it.assignmentStatus == AssignmentView.Status.APPROVED:true
        }
        log.debug("Found completed: ${assignments}")


        assignments.size()>= taskProperties.maxAssignments
    }

    void setUserProperty(String key, String val) {
       userProperties[key] = val
        save()
    }

    String getUserProperty(String key) {
        userProperties[key]
    }

    /**
     * Checks if there are pending assignments
     * @return
     */
    boolean hasPendingAssignments() {
        allHits*.assignments.findAll {
            it.assignmentStatus == AssignmentView.Status.SUBMITTED
        }.size() > 0
    }

    def afterLoad() {
        log.debug "After load: This identity: ${System.identityHashCode(this)} id: $id"
    }

    def afterInsert() {
        log.debug "After insert: This identity: ${System.identityHashCode(this)} id: $id"
    }


    @Override
    def beat(def beater, long timestamp) {
        AmazonMTurkClient service = (beater as WorkflowRun).requesterService
        if (taskStatus == Status.WAITING) {
            task.mturkTaskService.onTaskStarting(this)
            taskStatus = Status.RUNNING
            log.debug "BEAT: This identity: ${System.identityHashCode(this)} id: $id"
            task.start(service,this)
        } else if (taskStatus in Status.RUN_STATES) {
            log.debug("Updating on status $taskStatus in ${task.name}")
            task.update(service,this)
        }

        if (taskStatus == Status.COMPLETE) {
            ([]+activeHits.hits).each { HitView h->
                mturkAwsFacadeService.expire(service,h)
                activeHits.removeFromHits(h)

            }

        }

        //TODO handle abort?

        //TODO reincorporate recycle

//        if (taskStatus in Status.RUN_STATES) {
//            ([]+activeHits.hits).each { HitView h->
//                if (h.age > taskProperties.relaunchInterval) {
//                    addActive(mturkAwsFacadeService.recycle(service,h))
//                    mturkAwsFacadeService.expire(service, h)
//                }
//            }
//        }
        save()

    }

    def addActive(HitView hitView) {
        log.debug "AddActive: This identity: ${System.identityHashCode(this)} id: $id"
        activeHits.addToHits(hitView)
        addToAllHits(hitView)
        activeHits.save()
        save()
    }

    def removeActive(HitView hitView) {
        log.debug "RemoveActive: This identity: ${System.identityHashCode(this)} id: $id"
        activeHits.removeFromHits(hitView)
        save()
    }



    def setStatus(Status s) {
        this.taskStatus = s
        if (taskStatus == Status.COMPLETE) {
            mturkTaskService.onTaskComplete(this)
        }
    }

    def String getSubmitUrl() {
         workflowRun.real? "https://www.mturk.com/mturk/externalSubmit" : "https://workersandbox.mturk.com/mturk/externalSubmit"
    }

    /**
     * Created by josh on 2/21/14.
     */
    public static enum Status {

        WAITING,RUNNING,ABORTED,NEEDS_INPUT,COMPLETE

        static Status[] RUN_STATES = [RUNNING,NEEDS_INPUT]

    }
}
