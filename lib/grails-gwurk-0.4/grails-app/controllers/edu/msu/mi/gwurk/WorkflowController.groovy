package edu.msu.mi.gwurk

import groovy.util.logging.Log4j

@Log4j
class WorkflowController {



    def mturkMonitorService

    def index() {}

    def launchWorkflow() {
        if (params.workflow) {
            flash.workflow = Workflow.get(params.workflow)

        } else {
            render(view:"index")
        }

    }

    def doLaunch() {
        Workflow w = flash.workflow
        log.info("Workflow: ${w}")
        log.info("Type: ${params.type}")
        log.info("Type: ${params.iterations}")
        log.info("Credentials: ${Credentials.get(params.credentials as long)}")
        log.info("${params.props as Map}")




        WorkflowRun run = mturkMonitorService.launch(w,params.type=="real",params.iterations as int,Credentials.get(params.credentials as long), params.props as Map)
        log.info("About to launch")
        mturkMonitorService.actualLaunch(run, params.iterations as int)
        
        redirect(action:"index",controller:"workflowRun")
        //log.info("about to leave")
    }




    def external() {
        TaskRun run = TaskRun.get(params.task as long)
        //render "hi"
        def result = [taskrun: params.task, workerId: params.workerId, action: run.taskProperties.action,controller: run.taskProperties.controller, submiturl: run.submitUrl, assignmentId: params.assignmentId]
        print result
        result

    }


}
