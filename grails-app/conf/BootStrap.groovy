import edu.msu.mi.gwurk.GwurkEvent
import edu.msu.mi.gwurk.SingleHitTask
import edu.msu.mi.gwurk.Task
import edu.msu.mi.gwurk.Workflow
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import nardiff.mt.NardiffStuff
import nardiff.mt.Narrative
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.support.WebApplicationContextUtils

import javax.servlet.ServletContext
//This is test application
@Log4j
class BootStrap {

    def mturkTaskService

    def grailsApplication



    public static Narrative insertStory(def storydata) {

        Narrative narrative = new Narrative();
        narrative.text = storydata.story;
        narrative.distractorTask = new JsonBuilder(storydata.distractor).toPrettyString()
        narrative.too_simple = false;

        Narrative.withTransaction { tx ->
            narrative.save flush: true, failOnError: true
            narrative.root_narrative = narrative;
            narrative.save flush: true, failOnError: true
        }

        //Text2PNG.writeImageFile(narrative.text, narrative.id, imagePath);



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

                Narrative narrative = insertStory(storydata);




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




    }
    def destroy = {
    }
}
