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
        NardiffStuff n = new NardiffStuff();
        n.doInsert(params);
    }


    def preview() {

    }

    def storyImage() {

        Narrative parent = Narrative.get(params.narrative)
        response.setContentType("image/png")
        OutputStream os = response.getOutputStream()
        ImageIO.write(Text2PNG.getImage(parent.text),"png",os)
        os.close()

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
        t.save()

        response.status = 200
        render "OK"
    }

    @Transactional
    def turkerTask() {

        println "$params"
        if (!params.workerId) {
            render(view: "preview.gsp")
        } else {

            Task t = TaskRun.get(params.task).task

            Narrative parent = NardiffStuff.getNarrativeToExpand(t.taskProperties.parameter as Long)
            Turker turker = Turker.findByMturk_id(params.workerId)
            boolean shouldAsk = !(turker && turker.age && turker.education && turker.gender)
            println "Should I ask? $shouldAsk"
            render(view: 'start', model: [narrative: parent, askForDemographics:shouldAsk])
        }

    }



    @Transactional
    def save(Narrative narrativeInstance) {
        if (narrativeInstance == null) {
            notFound()
            return
        }

        if (narrativeInstance.hasErrors()) {
            respond narrativeInstance.errors, view: 'create'
            return
        }

        narrativeInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'narrative.label', default: 'Narrative'), narrativeInstance.id])
                redirect narrativeInstance
            }
            '*' { respond narrativeInstance, [status: CREATED] }
        }
    }

    def edit(Narrative narrativeInstance) {
        respond narrativeInstance
    }

    @Transactional
    def update(Narrative narrativeInstance) {
        if (narrativeInstance == null) {
            notFound()
            return
        }

        if (narrativeInstance.hasErrors()) {
            respond narrativeInstance.errors, view: 'edit'
            return
        }

        narrativeInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Narrative.label', default: 'Narrative'), narrativeInstance.id])
                redirect narrativeInstance
            }
            '*' { respond narrativeInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Narrative narrativeInstance) {

        if (narrativeInstance == null) {
            notFound()
            return
        }

        narrativeInstance.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Narrative.label', default: 'Narrative'), narrativeInstance.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'narrative.label', default: 'Narrative'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }

}
