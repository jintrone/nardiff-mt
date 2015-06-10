import edu.msu.mi.gwurk.GwurkEvent
import edu.msu.mi.gwurk.SingleHitTask
import edu.msu.mi.gwurk.Task
import edu.msu.mi.gwurk.TaskRun
import edu.msu.mi.gwurk.Workflow
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import nardiff.mt.NardiffStuff
import nardiff.mt.Narrative
import nardiff.mt.NarrativeRequest
import nardiff.mt.Text2PNG
import nardiff.mt.Turker
import org.apache.commons.lang3.StringUtils
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.support.WebApplicationContextUtils

import javax.servlet.ServletContext

//This is test application
@Log4j
class BootStrap {

    def mturkTaskService

    def grailsApplication



    public static Narrative insertStory(def storydata, int numInitialRequests, int numLPRequests, String imagePath) {

        Narrative narrative = new Narrative();
        narrative.text = storydata.story;
        narrative.distractorTask = new JsonBuilder(storydata.distractor).toPrettyString()
        narrative.too_simple = false;

        Narrative.withTransaction { tx ->
            narrative.save flush: true, failOnError: true
            narrative.root_narrative_id = narrative.id;
            narrative.save flush: true, failOnError: true
        }

        //Text2PNG.writeImageFile(narrative.text, narrative.id, imagePath);

        for (int i = 0; i < numInitialRequests; i++) {
            NarrativeRequest nr = new NarrativeRequest();
            nr.parent_narrative = narrative;
            nr.root_narrative = narrative;
            nr.priority = 1;
            nr.depth = 1;

            NarrativeRequest.withTransaction { tx ->
                nr.save flush: true, failOnError: true
            }
        }

        for (int i = 0; i < numLPRequests; i++) {
            NarrativeRequest nr = new NarrativeRequest();
            nr.parent_narrative = narrative;
            nr.root_narrative = narrative;
            nr.priority = 100;
            nr.depth = 1;

            NarrativeRequest.withTransaction { tx ->
                nr.save flush: true, failOnError: true
            }
        }

        return narrative;
    }


    def init = { servletContext ->

        def experimentData = new JsonSlurper().parse(new File(servletContext.getRealPath("/data/experiment2.json")))


        //println "$experimentData"

        if (Narrative.count() < 3) {

            Workflow w = new Workflow("Narrative Diffusion", "An experiment similar to a game of telephone", [
                    rewardAmount      : 0.58f,
                    relaunchInterval  : 1000 * 60 * 60,
                    autoApprove       : true,
                    lifetime          : 60 * 60 * 10,
                    assignmentDuration: 600,
                    keywords          : "research, memory",
                    maxAssignments    : 10,
                    height            : 1000,
                    requireApproval   : true
            ])

           List tasks = experimentData.collect { storydata ->

                WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
                ServletContext sc = context.getServletContext();
                String imagePath = sc.getRealPath("/images/narratives");

                Narrative narrative = insertStory(storydata, 5, 10, imagePath);




                Task t = new SingleHitTask("Narrative Task #" + narrative.id, [
                        parameter  : narrative.id.toString(),
                        controller : "narrative",
                        action     : "turkerTask",
                        title      : "Read a story and try to tell it to someone else.",
                        description: "This research is to help understand certain aspects of how information spreads.  No sensitive information is collected, and identities will be erased after data collection is complete.",
                ]).save()





                mturkTaskService.installTask(t) { type, GwurkEvent evt ->
                    switch (type) {

                        case GwurkEvent.Type.TASK_STARTING:
                            log.info("Task #" + narrative.id + "starting")
                            break

                        case GwurkEvent.Type.HIT_COMPLETE:
                            log.info("Hit complete!")
                            break
                        case GwurkEvent.Type.ASSIGNMENT_COMPLETE:
                            log.info("Assignment complete!")
                            log.info("Answer = " + evt.assignmentView.answer);

                            NardiffStuff n = new NardiffStuff();
                            n.doInsert(evt.assignmentView);

                            break;

                        case GwurkEvent.Type.TASK_COMPLETE:
                            log.info("Task1 complete!")
                            break
                    }

                }
               t
            }
            w.initStartingTasks(tasks as Task[])

            //Remember to save!
            w.save()

            mturkTaskService.installWorkflow(w) { a, b ->
                log.info("Workflow complete!")
            }

        }

        log.info("Bootstrap Complete")

//        Task one = new SingleHitTask("Narrative Task #1", [
//                narrative_id: 1,
//                controller  : "narrative",
//                action      : "start",
//                title       : "Please follow the instructions below.",
//                description : "This research is to help understand certain aspects of how information spreads.  No sensitive information is collected, and identities will be erased after data collection is complete.",
//        ]).save()
//
//        Task two = new SingleHitTask("Narrative Task #2", [
//                narrative_id: 2,
//                controller  : "narrative",
//                action      : "start",
//                title       : "Please follow the instructions below.",
//                description : "This research is to help understand certain aspects of how information spreads.  No sensitive information is collected, and identities will be erased after data collection is complete.",
//        ]).save()

//        Task three = new SingleHitTask("InterestsTask2", [
//                controller: "interests",
//                action: "create",
//                title: "(<= 20) Enter some information about yourself",
//                description: "This a survey to understand the turker workforce a little better. No identifying information will be captured, and the data is for research purposes only",
//        ]).save()

        //Adds the next task to be executed in the workflow
//        one.addNextTask(two)

//        one.addNextTask(three)

        //Remember to save!
//        one.save()

        //Sets the tasks to begin with.  If this method is not set, no tasks will be executed when the workflow is launched!
//        w.initStartingTasks(firstTask);

        //Remember to save!
//        w.save()

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

//        mturkTaskService.installTask(one) { type, GwurkEvent evt ->
//            switch (type) {
//
//                case GwurkEvent.Type.TASK_STARTING:
//                    log.info("Task1 starting")
//                    break
//
//                case GwurkEvent.Type.HIT_COMPLETE:
//                    log.info("Hit complete!")
//                    break
//                case GwurkEvent.Type.ASSIGNMENT_COMPLETE:
//                    log.info("Assignment complete!")
//                    log.info(evt.assignmentView.answer)
//
//                    //  def d = new Demographics(evt.assignmentView.answer).save()
//
//                    //User properties must be strings (for maximal generality)
//                    evt.taskRun.setUserProperty("age", "${d.age}")
//                    break
//
//                case GwurkEvent.Type.TASK_COMPLETE:
//                    log.info("Task1 complete!")
//                    break
//            }
//
//        }
//
//        mturkTaskService.installTask(two) { type, GwurkEvent evt ->
//            switch (type) {
//
//                case GwurkEvent.Type.TASK_STARTING:
//                    log.info("Task2 starting")
//                    log.info("Previous turker indicated an age of ${evt.taskRun.previousTaskRuns[0].getUserProperty("age")} which should be > 20")
//                    break
//
//                case GwurkEvent.Type.HIT_COMPLETE:
//                    log.info("Hit complete!")
//                    break
//
//                case GwurkEvent.Type.ASSIGNMENT_COMPLETE:
//                    log.info("Assignment complete!")
//                    println evt.assignmentView.answer
//                    //   new Interests(evt.assignmentView.answer).save()
//                    break
//
//                case GwurkEvent.Type.TASK_COMPLETE:
//                    log.info("Task2 complete!")
//                    break
//            }
//
//        }

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


    }
    def destroy = {
    }
}
