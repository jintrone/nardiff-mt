package nardiff.mt

import edu.msu.mi.gwurk.Task
import edu.msu.mi.gwurk.TaskRun
import org.apache.commons.lang3.StringUtils
import org.springframework.core.io.ResourceLoader

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class NarrativeController implements org.springframework.context.ResourceLoaderAware {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE", demographics: "POST"]

    ResourceLoader resourceLoader

    def nardiffService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Narrative.list(params), model: [narrativeInstanceCount: Narrative.count()]
    }

    def show(Narrative narrativeInstance) {
        respond narrativeInstance
    }

    def create() {
        respond new Narrative(params)
    }

    @Transactional
    def complete() {
        render "OK"
    }


    def preview() {

    }

    def storyImage() {

        Narrative parent = Narrative.get(params.narrative).parent_narrative
        response.setContentType("image/png")
        println "${parent.text}"
        String text = parent?.text?:"Our apologies for the inconvenience, but we encountered an internal problem; please notify the experimenter and return this hit."

        def outputStream = response.getOutputStream()
        try {
            ImageIO.write(Text2PNG.getImage(text), "png", outputStream)

        } catch (IOException e) {
            log.debug('Canceled download?', e)
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close()
                } catch (IOException e) {
                    log.debug('Exception on close', e)
                }
            }
        }

    }

    @Transactional
    def demographics() {
        println "$params"
        Turker t = Turker.findByMturk_id(params.workerid)
        if (!t) {
            t = new Turker()
            t.mturk_id = params.workerid
        }
        t.age = params.age as Integer
        t.gender = params.gender
        t.education = params.education as Integer
        t.save()

        response.status = 200
        render "OK"
    }

    @Transactional
    def submitStage() {
        println "Received $params"
        Narrative n = Narrative.findByAssignmentId(params.assignmentId)

        if (!n) {
            println("Could not get narrative!")
            render "Not good"
        } else {

            int nstage = Integer.parseInt(params.stage)
            //including this condition to avoid the hassle of
            //making sure the {@link NardiffService#finalizeMethod} isn't writing to the same
            //row in the db
            if (nstage < 7) {
                n.stage = nstage
                n.save([flush: true])
            }

            render "Ok"
        }
        response.status = 200
    }


    @Transactional
    def submitNarrative() {
        println "Received $params"
        Narrative n = Narrative.get(params.narrativeId)
        if (!n) {
            println("Could not get narrative!")
            render "Not good"
        } else {
            nardiffService.finalizeNarrative(n, params)
            render "Ok"
        }
        response.status = 200
    }

    @Transactional
    def turkerTask() {

        if (!params.workerId) {
            render(view: "preview.gsp")
        } else {

            Narrative n = nardiffService.openNarrative(params.workerId, params.assignmentId)
            Turker turker = Turker.findByMturk_id(params.workerId)
            boolean shouldAsk = !(turker && turker.age && turker.education && turker.gender)

            if (n.stage > 0 && n.stage < 7 && n.stage!=4) {
                n.stage--
                n.save(flush: true)
            }

            render(view: 'start', model: [narrative: n, askForDemographics: shouldAsk])
        }

    }


}
