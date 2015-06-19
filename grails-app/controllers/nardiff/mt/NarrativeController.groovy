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

   // static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE", demographics: "POST"]

    ResourceLoader resourceLoader

    def nardiffService



    @Transactional
    def complete() {
        render "OK"
    }



    def preview() {

    }

    def storyImage() {

        Narrative parent = Narrative.get(params.narrative).parent_narrative
        response.setContentType("image/png")


        def text = "We encountered an internal problem; please notify the experimenter of the following code and return this hit: "
        if (!parent) {
            log.error("Parent does not exist")
            text += "[NO PARENT]"
        } else {
            NarrativeData data = parent.data
            if (!data) {
                text+="[NO DATA]"
            } else {
                if (!data.text) {
                    data.fresh = true
                    data.save([flush:true,failOnError: true])
                    text = parent.root_narrative.text
                } else {
                    text = data.text
                }
            }
        }

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
        Turker t = Turker.findByMturk_id(params.workerid)
        if (!t) {
            t = new Turker()
            t.mturk_id = params.workerid
        }
        t.age = params.age as Integer
        t.gender = params.gender
        t.education = params.education as Integer
        t.save([flush: true, failOnError: true])

        response.status = 200
        render "Ok demographics"
    }



    @Transactional
    def submitStage() {
        println "Received $params"
        NarrativeData d = NarrativeData.findByNarrativeId(params.narrativeId)

        if (!d) {
            log.error("Could not get narrative data!")
            render "Not good"
        } else {

            int nstage = Integer.parseInt(params.stage)
            //including this condition to avoid the hassle of
            //making sure the {@link NardiffService#finalizeMethod} isn't writing to the same
            //row in the db
            if (nstage < 7) {
                d.stage = nstage
                d.save([flush:true,failOnError: true])
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
            log.error("Could not get narrative to save")
            render "Not good"
        } else {
            nardiffService.finalizeNarrative(n, params)
            nardiffService.updateExpansion(n)
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

            if (!n) {
                log.error("Could not retrieve new narrative for ${params.workerId}")
                render(view: "error.gsp")
                return
            }

            Turker turker = Turker.findByMturk_id(params.workerId)
            boolean shouldAsk = !(turker && turker.age && turker.education && turker.gender)
            NarrativeData data = n.data
            if (data.stage > 0 && data.stage < 7 && data.stage!=4) {
                data.stage--
                data.save(flush: true)
            }


            render(view: 'start', model: [narrative: n, askForDemographics: shouldAsk])
        }

    }


}
