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






    static String[] initialStories = [
            "Once there was a little boy who lived in a hot country.  " +
                    "One day his mother told him to take some cake to his grandmother.  " +
                    "She warned him to hold it carefully so it wouldnt break into " +
                    "crumbs.  The little boy put the cake in a leaf under his arm " +
                    "and carried it to his grandmother's.  \n" +
                    "When he got there the cake had crumbled into tiny pieces. " +
                    "His grandmother told him he was a silly boy and that he " +
                    "should have carried the cake on top of his head so it " +
                    "wouldn't break.  Then she gave him a pat of butter to " +
                    "take back to his mother's house.\n" +
                    "The little boy wanted to be very careful with the butter, " +
                    "so he put it on top of his head and carried it home.  " +
                    "The sun was shining hard and when he got home the butter " +
                    "had all melted.  \n" +
                    "His mother told him that he was a silly boy and that " +
                    "he should have put the butter in a leaf so that it would " +
                    "have gotten home safe and sound.",

            "Once there was a woman who needed a tiger’s whisker.  She " +
                    "was afraid of tigers, but she needed a whisker to make a " +
                    "medicine for her husband who had gotten very sick.  She thought " +
                    "and thought about how to get a tiger’s whisker.    She decided " +
                    "to use a trick.\n" +
                    "She know that tigers loved food and music.  She thought that if " +
                    "she brought food to a lonely tiger and played soft music, the " +
                    "tiger would be nice to her and she could get the whisker.\n" +
                    "She went to a tiger’s cave where a lonely tiger lived.  She " +
                    "put a bowl of food in front of the opening of the cave.  Then " +
                    "she sang soft music.  The tiger came out and ate the food.  " +
                    "He then walked over to the lady and thanked her for the " +
                    "delicious food and lovely music.  \n" +
                    "The lady then cut off one of his whiskers and ran down the" +
                    " hill very quickly.  The tiger felt lonely and sad again.",

            "There was a fox and a bear who were friends.  One day they " +
                    "decided to catch a chicken for supper.  They decided to go " +
                    "together, because neither one wanted to be left alone and " +
                    "they both liked fried chicken.\n" +
                    "They waited until night time.  Then they ran very quickly " +
                    "to a nearby farm where they knew chickens lived.  The bear, " +
                    "who felt very lazy climbed up on the roof to watch.  The fox " +
                    "then opened the door of the henhouse very carefully.  He " +
                    "grabbed a chicken and killed it.\n" +
                    "As he was carrying it out the henhouse the weight of the " +
                    "bear on the roof caused the roof to crack.  The fox heard " +
                    "the noise and was frightened but it was too late to run out.  " +
                    "The root and the bear fell in, killing five of the chickens. \n" +
                    "The fix and the bear were trapped in the broken henhouse.  " +
                    "Soon the farmer came out to see what was the matter.",

            "Judy is going to have a birthday party.  She is ten years " +
                    "old.  She wants a hammer and a saw for presents.  Then she " +
                    "could make a coat rack and fix her doll house.  She asked " +
                    "her father to get them for her.  \n" +
                    "Her father did not want to get them for her.  He did not " +
                    "think that girls should play with a hammer and a saw.  But " +
                    "he wanted to get her something.  So he bought her a " +
                    "beautiful new dress.  Judy liked the dress, but she still " +
                    "wanted the hammer and the saw.\n" +
                    "Later, she told her grandmother about her wish.  Her " +
                    "grandmother knew that Judy really wanted a hammer and a " +
                    "saw.  She decided to get them for her, because when Judy " +
                    "grows up and becomes a woman she will have to fix things " +
                    "when they break.\n" +
                    "Then her grandmother went out that very day and bought " +
                    "the tools for Judy.  She gave them to Judy that night.  " +
                    "Judy was very happy.  Now she could build things with " +
                    "her hammer and saw.",

            "A few weeks ago, health care professionals saw an " +
                    "alarming spike in the number of people seeking medical " +
                    "care after being infected with measles.  Measles is a " +
                    "highly contagious disease that the World Health " +
                    "Organization estimates used to cause ~2.6 million " +
                    "deaths per year globally.\n" +
                    "In the United States, the development and widespread" +
                    " use of vaccines has virtually eradicated the disease, " +
                    "but measles is periodically re-introduced when people " +
                    "travel to parts of the world where it is still prevalent.\n" +
                    "Widespread vaccination protects even those who have " +
                    "not been vaccinated themselves, because people who " +
                    "have been vaccinated cannot infect others.  This “herd " +
                    "immunity” reduces the risk that unvaccinated people " +
                    "will be exposed to the measles virus in the first place.\n" +
                    "Herd immunity is useful, because some people (e.g., " +
                    "very young children) cannot be vaccinated.  " +
                    "Unfortunately, vaccination rates have dropped in " +
                    "some parts of the U.S., allowing the disease to " +
                    "spread there and increasing the risk of a more " +
                    "widespread outbreak."
    ]

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

        Text2PNG.writeImageFile(narrative.text, narrative.id, imagePath);

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
                    rewardAmount      : 0.03f,
                    relaunchInterval  : 1000 * 60 * 60,
                    autoApprove       : true,
                    lifetime          : 60 * 60 * 10,
                    assignmentDuration: 600,
                    keywords          : "research",
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
                        action     : "start",
                        title      : "Please follow the instructions below.",
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
                            n.doInsert(evt.assignmentView.answer);

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
