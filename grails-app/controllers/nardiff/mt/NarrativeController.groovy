package nardiff.mt


import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class NarrativeController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

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
