package nardiff.mt

import grails.util.GrailsWebUtil
import spock.lang.Specification

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future


/**
 * Created by josh on 6/18/15.
 */
class CompleteWorkflowSpec extends Specification {


    def nardiffService


    def "test a single worker"() {

        setup:
        ExecutorService executor = Executors.newFixedThreadPool(8)
        nardiffService.setBranching([2,2,2])

        def tasks = (1..40).collect { int worker->
            { ->  (1..5).each { int task->
                    testWorkerOnce("a${worker}","b${worker}_${task}")
                } } as Callable<String>
        }

        when:
        executor.invokeAll(tasks).each {
            println "Worker ${it.get()} complete"
        }


        then:
        Turker.list().size() == 40
        Narrative.count() == 205
        Narrative.list().each {
            if (it.parent_narrative) {
                it.data.text.contains("${it.root_narrative.text}")
            }
        }

    }


    public NarrativeController getController() {

        def nc = new NarrativeController()
        GrailsWebUtil.bindMockWebRequest()
        nc.nardiffService = nardiffService
        nc
    }

    public void setStage(int stage, long narrativeId) {
        def nc = controller
        nc.params.narrativeId = narrativeId
        nc.params.stage = "${stage}"
        nc.submitStage()
    }

    public void testWorkerOnce(String workerId, String assignmentId) {
        Random rand = new Random()
        def nc = controller
        nc.params.workerId = workerId
        nc.params.assignmentId = assignmentId
        nc.turkerTask()

        Narrative n = nc.modelAndView.model.narrative
        boolean ask = nc.modelAndView.model.askForDemographics

        nc = controller
        nc.params.narrative = n.id
        nc.storyImage()

        if (ask) {
            setStage(2, n.id)
            Thread.sleep(200l+rand.nextInt(500))
            nc = controller
            nc.params.workerid = workerId
            nc.params.age = "1987"
            nc.params.gender = "M"
            nc.params.education = "3"
            nc.demographics()
        }

        setStage(3, n.id)
        Thread.sleep(200l)
        setStage(4, n.id)
        Thread.sleep(200l+rand.nextInt(500))

        setStage(5, n.id)
        Thread.sleep(200l+rand.nextInt(500))
        setStage(6, n.id)
        Thread.sleep(200l+rand.nextInt(1500))
        setStage(7, n.id)
        nc = controller
        nc.params.narrativeId = n.id
        nc.params.text ="${workerId}${n.root_narrative.text}"
        nc.params.distractorAnswer = "4"
        nc.params.timeDistractor = "50"
        nc.params.timeReading = "50"
        nc.params.timeWriting = "50"
        nc.submitNarrative()

        nc = controller
        nc.params.narrative = n.id
        nc.params.workerId = workerId
        nc.params.assignmentId = assignmentId

        nc.complete()


    }


}