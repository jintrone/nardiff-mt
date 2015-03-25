import edu.msu.mi.gwurk.GwurkEvent
import edu.msu.mi.gwurk.SingleHitTask
import edu.msu.mi.gwurk.Task
import edu.msu.mi.gwurk.TaskRun
import edu.msu.mi.gwurk.Workflow
import groovy.util.logging.Log4j

//This is test application
@Log4j
class BootStrap {

    def mturkTaskService

    def init = { servletContext ->

        Workflow w = new Workflow("Narrative Diffusion", "An experiment similar to a game of telephone", [
                rewardAmount: 0.03f,
                relaunchInterval: 1000 * 60 * 60,
                autoApprove: true,
                lifetime: 60 * 60 * 10,
                assignmentDuration: 60,
                keywords: "research",
                maxAssignments: 1,
                height: 1000,
                requireApproval: true
        ])

        Task one = new SingleHitTask("Narrative Task #1", [
                controller: "narrative",
                action: "start",
                title: "Please follow the instructions below.",
                description: "This research is to help understand certain aspects of how information spreads.  No sensitive information is collected, and identities will be erased after data collection is complete.",
        ]).save()

        Task two = new SingleHitTask("Narrative Task #2", [
                controller: "narrative",
                action: "start",
                title: "Please follow the instructions below.",
                description: "This research is to help understand certain aspects of how information spreads.  No sensitive information is collected, and identities will be erased after data collection is complete.",
        ]).save()


//        Task three = new SingleHitTask("InterestsTask2", [
//                controller: "interests",
//                action: "create",
//                title: "(<= 20) Enter some information about yourself",
//                description: "This a survey to understand the turker workforce a little better. No identifying information will be captured, and the data is for research purposes only",
//        ]).save()

        //Adds the next task to be executed in the workflow
        one.addNextTask(two)

//        one.addNextTask(three)

        //Remember to save!
        one.save()

        //Sets the tasks to begin with.  If this method is not set, no tasks will be executed when the workflow is launched!
        w.initStartingTasks(one)

        //Remember to save!
        w.save()


        //an optional conditional to determine whether a task should be executed next. The closure takes one parameter
        //(the previously executed task run) and should return a boolean .
        //
        //If the closure returns true on evaluation , the path will be followed. You probably want to make sure only one
        //path may be followed out of a task
        //
//        mturkTaskService.installConditional(one,two)  {  TaskRun from  ->
//            (from.getUserProperty("age") as Integer) > 20
//        }


//        mturkTaskService.installConditional(one,three)  {  TaskRun from ->
//            (from.getUserProperty("age") as Integer) <= 20
//        }


        mturkTaskService.installTask(one) { type, GwurkEvent evt ->
            switch (type) {

                case GwurkEvent.Type.TASK_STARTING:
                    log.info("Task1 starting")
                    break

                case GwurkEvent.Type.HIT_COMPLETE:
                    log.info("Hit complete!")
                    break
                case GwurkEvent.Type.ASSIGNMENT_COMPLETE:
                    log.info("Assignment complete!")
                    log.info(evt.assignmentView.answer)

                  //  def d = new Demographics(evt.assignmentView.answer).save()

                    //User properties must be strings (for maximal generality)
                    evt.taskRun.setUserProperty("age","${d.age}")
                    break

                case GwurkEvent.Type.TASK_COMPLETE:
                    log.info("Task1 complete!")
                    break
            }

        }

        mturkTaskService.installTask(two) { type, GwurkEvent evt ->
            switch (type) {

                case GwurkEvent.Type.TASK_STARTING:
                    log.info("Task2 starting")
                    log.info("Previous turker indicated an age of ${evt.taskRun.previousTaskRuns[0].getUserProperty("age")} which should be > 20")
                    break

                case GwurkEvent.Type.HIT_COMPLETE:
                    log.info("Hit complete!")
                    break

                case GwurkEvent.Type.ASSIGNMENT_COMPLETE:
                    log.info("Assignment complete!")
                    println evt.assignmentView.answer
                 //   new Interests(evt.assignmentView.answer).save()
                    break

                case GwurkEvent.Type.TASK_COMPLETE:
                    log.info("Task2 complete!")
                    break
            }

        }

//        mturkTaskService.installTask(three) { type, GwurkEvent evt ->
//            switch (type) {
//
//                case GwurkEvent.Type.TASK_STARTING:
//                    log.info("Task3 starting")
//                    log.info("Previous turker indicated an age of ${evt.taskRun.previousTaskRuns[0].getUserProperty("age")} which should be <= 20")
//                    break
//
//                case GwurkEvent.Type.HIT_COMPLETE:
//                    log.info("Hit complete!")
//                    break
//
//                case GwurkEvent.Type.ASSIGNMENT_COMPLETE:
//                    log.info("Assignment complete!")
//                    println evt.assignmentView.answer
//           //         new Interests(evt.assignmentView.answer).save()
//                    break
//
//                case GwurkEvent.Type.TASK_COMPLETE:
//                    log.info("Task3 complete!")
//                    break
//            }
//
//        }




        mturkTaskService.installWorkflow(w) { a, b ->
            log.info("Workflow complete!")
        }

    }
    def destroy = {
    }
}
