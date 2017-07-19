package nardiff.mt

import edu.msu.mi.gwurk.TaskRun
import grails.transaction.Transactional
import org.springframework.core.io.ResourceLoader

import javax.imageio.ImageIO

@Transactional(readOnly = true)
class NarrativeController implements org.springframework.context.ResourceLoaderAware {

    // static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE", demographics: "POST"]

    ResourceLoader resourceLoader

    def nardiffService



    def complete() {
        render "OK"
    }


    def preview() {

    }

    @Transactional
    def storyImage() {
        Narrative mine = Narrative.get(params.narrative)
        Narrative parent = mine.parent_narrative
        response.setContentType("image/png")


        def text = "We encountered an internal problem; please notify the experimenter of the following code and return this hit: "
        if (!parent) {
            log.error("Parent does not exist")
            text += "[NO PARENT]"
        } else {
            NarrativeData data = parent.data
            if (!data) {
                text += "[NO DATA]"
            } else {
                if (!data.text) {
                    log.error("Parent has no data!!!")
                    NarrativeData mydata = mine.data
                    mydata.fresh = true
                    mydata.save([flush: true, failOnError: false])
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
        t.hispanic = params.hispanic
        t.race = params.race

        t.education = params.education as Integer
        t.student = params.student
        t.save([flush: true, failOnError: true])

        response.status = 200
        render "Ok demographics"
    }

    @Transactional
    def survey() {
        Survey t = Survey.findByMturk_idAndNarrativeId(params.workerid,params.narrativeId)
        if (!t) {
            t = new Survey()
            t.mturk_id = params.workerid
        }

        params.each { k,v->
            if (k!="mturk_id" && t.hasProperty(k)) {
                t[k]=v as Integer
            }

        }

        t.save([flush: true, failOnError: true])

        response.status = 200
        render "Ok survey"
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
                d.save([flush: true, failOnError: true])
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

            render "Ok"
        }
        response.status = 200
    }

    @Transactional
    def turkerTask() {
        println(params)

        if (!params.workerId) {
            render(view: "preview.gsp", model: [storycount: NarrativeSeed.count])

        } else {

            TaskRun tr = TaskRun.get(params.taskRun)
            log.info(tr.toString())
            //String qstring = tr.taskProperties.qualificationString
            //RequesterService rs = mturkAwsFacadeService.getRequesterService(tr)

            //QualificationType qt = rs.searchQualificationTypes(qstring, false, true, null, null, null, null).qualificationType[0]

            //rs.assignQualification(qt.qualificationTypeId, params.workerId, null, false)

            Narrative n = nardiffService.openNarrative(params.workerId, params.assignmentId)

            if (!n) {
                log.error("Could not retrieve new narrative for ${params.workerId}")
                render(view: "notavailable")
                return
            } else {

                Turker turker = Turker.findByMturk_id(params.workerId)
                boolean shouldAsk = !(turker && turker.age && turker.education && turker.gender)
                NarrativeData data = n.data
                if (data.stage > 0 && data.stage < 8 && data.stage != 4) {
                    data.stage--
                    data.save(flush: true)
                }


                render(view: 'start', model: [narrative: n, askForDemographics: shouldAsk])
            }


        }


    }


}
