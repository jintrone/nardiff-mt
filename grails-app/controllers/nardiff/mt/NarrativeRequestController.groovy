package nardiff.mt


import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class NarrativeRequestController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond NarrativeRequest.list(params), model: [narrativeRequestInstanceCount: NarrativeRequest.count()]
    }

    def show(NarrativeRequest narrativeRequestInstance) {
        respond narrativeRequestInstance
    }

    def create() {
        respond new NarrativeRequest(params)
    }

    @Transactional
    def save(NarrativeRequest narrativeRequestInstance) {
        if (narrativeRequestInstance == null) {
            notFound()
            return
        }

        if (narrativeRequestInstance.hasErrors()) {
            respond narrativeRequestInstance.errors, view: 'create'
            return
        }

        narrativeRequestInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'narrativeRequest.label', default: 'NarrativeRequest'), narrativeRequestInstance.id])
                redirect narrativeRequestInstance
            }
            '*' { respond narrativeRequestInstance, [status: CREATED] }
        }
    }

    def edit(NarrativeRequest narrativeRequestInstance) {
        respond narrativeRequestInstance
    }

    @Transactional
    def update(NarrativeRequest narrativeRequestInstance) {
        if (narrativeRequestInstance == null) {
            notFound()
            return
        }

        if (narrativeRequestInstance.hasErrors()) {
            respond narrativeRequestInstance.errors, view: 'edit'
            return
        }

        narrativeRequestInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'NarrativeRequest.label', default: 'NarrativeRequest'), narrativeRequestInstance.id])
                redirect narrativeRequestInstance
            }
            '*' { respond narrativeRequestInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(NarrativeRequest narrativeRequestInstance) {

        if (narrativeRequestInstance == null) {
            notFound()
            return
        }

        narrativeRequestInstance.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'NarrativeRequest.label', default: 'NarrativeRequest'), narrativeRequestInstance.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'narrativeRequest.label', default: 'NarrativeRequest'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
