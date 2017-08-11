package edu.msu.mi.gwurk

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.retry.RetryPolicy
import com.amazonaws.services.mturk.AmazonMTurkClient
import groovy.util.logging.Log4j

@Log4j
class WorkflowRun implements BeatListener {

    static constraints = {
    }


    static hasMany = [currentTasks: TaskRun, allTasks: TaskRun, taskProperties: TaskProperties]
    static mappedBy = [taskProperties: "none", currentTasks: "activeWorkflowRun", allTasks: "workflowRun"]


    static enum Status {
        WAITING,  RUNNING, ABORTED, DONE
    }

    static mapping = {
        real column: "is_real_hit"
    }

    static transients = ['retriableErrors']

    def mturkTaskService
    def mturkAwsFacadeService
    def mturkMonitorService

    // RequesterService requesterService
    //Set<TaskRun> currentTasks = [] as Set
    //Set<TaskRun> allTasks = [] as Set
    Status currentStatus
    boolean real

    int retryAttempts = 10
    long retryDelayMillis = 1000
    int iteration = 0
    int maxIterations = 0

    TaskProperties globalProperties
    Map taskProperties = [:]
    Map<String,String> userProperties = [:]

    Workflow workflow
    Credentials credentials


    Set<String> retriableErrors = ["Server.ServiceUnavailable"] as Set<String>

    WorkflowRun(Workflow w, Credentials credentials, boolean real, Map props) {
        workflow = w
        this.real = real
        this.credentials = credentials
        globalProperties = w.taskProperties

        props.each { k, v ->
            if (v instanceof Map) {
                if (k == "global") {
                    globalProperties = globalProperties.copyFrom(new TaskProperties(v))

                } else {
                    taskProperties.put(k, new TaskProperties(v).save())
                }
            }
        }
        globalProperties.save()
        currentStatus = Status.WAITING

    }

    def abort() {
        log.warn("Abort is not yet fully implemented - this will fail in an unspecified manner")
        if (currentStatus == Status.DONE) {
            log.info("Cannot abort a complete workflow")
        } else {

            if (currentStatus == Status.WAITING) {
                log.info("Aborting a Waiting workflow - are you sure you wanted to do this?")
                currentStatus = Status.DONE
                save()
            } else {
                for (TaskRun r:currentTasks) {
                    r.status = TaskRun.Status.ABORTED
                }
            }

        }

    }




    AmazonMTurkClient getRequesterService() {
        mturkAwsFacadeService.getRequesterService(credentials,real)
    }

    TaskRun addTask(Task t, boolean current, TaskRun... previous) {
        log.info("Creating a new TaskRun")
        TaskProperties p =  getTaskProperties(t)

        p.save()
        def tr = new TaskRun(t, p)
        tr.workflowRun = this
        tr.save(flush:true,failOnError: true)
        log.info("About to save task run")
        addToAllTasks(tr)
        if (current) {
            log.info("Add ${tr} to current")    
            addToCurrentTasks(tr)
        }
        if (previous) previous.each {
            tr.addToPreviousTaskRuns(it)
        }


        log.info("About to save workflow run")
        try {
            save(failOnError: true)
        } catch (Throwable th) {
           th.printStackTrace()
            print this.errors.allErrors
        }
        
        log.info("Done...")
        log.info("Task run is ${tr}")
        tr
    }

    def run(times) {
        if (currentStatus != Status.WAITING) throw new MturkStateException("Can't reuse a workflow object; please use 'copy' if you would like to run with existing parameters")
        currentStatus = Status.RUNNING
        maxIterations = times
        kickoff()

        save(failOnError: true)

    }

    def kickoff() {
        workflow.startingTasks.each { task ->
            addTask(task,true)
        }
    }

    TaskProperties getTaskProperties(Task task) {
        TaskProperties taskP = taskProperties[task.name] ? workflow.allTasks[task.name].taskProperties.copyFrom(taskProperties[task.name]) : workflow.allTasks[task.name].taskProperties
        globalProperties.copyFrom(taskP)

    }

    void setUserProperty(String key, String val) {
        userProperties[key] = val
        save()
    }

    String getUserProperty(String key) {
        userProperties[key]
    }


    @Override
    def beat(def Object beater, long timestamp) {
        def next = []
        if (currentStatus == Status.RUNNING) {
            mturkMonitorService.safeBeat(this,currentTasks) { currTaskRun ->
               
                if (currTaskRun.taskStatus == TaskRun.Status.COMPLETE) {
                    log.info("Removing TaskRun:${currTaskRun.task.name}")
                    removeFromCurrentTasks(currTaskRun)
                    if (currTaskRun.task.next) {
                        currTaskRun.task.next.each {
                            if (mturkTaskService.advanceToTask(currTaskRun,it)) {
                                addTask(it,true,currTaskRun)
                            } else {
                                log.info("Could not advance to ${it.name}; fails conditional")
                            }

                        }

                    }
                }

            }

        } else if (currentStatus == Status.ABORTED) {
            currentTasks.each { TaskRun tr ->
                if (tr.taskStatus in TaskRun.Status.RUN_STATES) {
                    //TODO
                    log.info("Abort is not yet implemented")
                }

            }

        }


        save()
        log.info("Current tasks after save are now $currentTasks")
        if (currentTasks.isEmpty() && currentStatus!=Status.ABORTED) {
            ++iteration
            mturkTaskService.onWorkflow(this)
            if (iteration >= maxIterations) {
                currentStatus = Status.DONE
            } else {
                kickoff()
            }
        }
        save()

    }


}
