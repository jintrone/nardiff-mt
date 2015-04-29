package nardiff.mt

import edu.msu.mi.gwurk.Task
import org.apache.commons.lang3.StringUtils
import org.springframework.core.io.ResourceLoader

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class NarrativeController implements org.springframework.context.ResourceLoaderAware {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

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

        // XXX DO NOT MODIFY!  This is copied code from Bootstrap.groovy, which is where the
        // real recording code lives.
        System.out.println(params)
        NarrativeRequest nr = NarrativeRequest.findById(Long.parseLong((String) params.get("request_id")));
        nr.when_completed = new Date();
        nr.save();

        Turker worker = nr.getAssigned_to();
        String age = params.get("age");
        if (age != null) {
            if (!age.equals("")) {
                worker.age = Integer.parseInt(age);
                worker.gender = params.get("gender");
                worker.education = Integer.parseInt((String) params.get("education"));
                worker.save();
            }
        }

        Narrative parentNarrative = nr.getParent_narrative();

        Narrative thisNarrative = new Narrative();
        thisNarrative.parent_narrative = parentNarrative;
        thisNarrative.root_narrative_id = parentNarrative.root_narrative_id;
        thisNarrative.completed_by = worker;
        thisNarrative.text = params.get("story");
        thisNarrative.distractor_answer = params.get("distractorAnswer");
        thisNarrative.time_distrator = Integer.parseInt(params.get("distractorTime"));
        thisNarrative.time_writing = Integer.parseInt(params.get("retellTime"));
        thisNarrative.too_simple = false;
        if (((String)params.get("tooSimple")).matches("^[T|t]") )
            thisNarrative.too_simple = true;

        thisNarrative.save();

        // Only insert more child requests if it wasn't too simple.
        if (thisNarrative.too_simple)
            return;

        // TODO Decide whether or not to accept this task and create new child tasks.
        String parentText = parentNarrative.text;
        String childText = thisNarrative.text;

        // if it's a perfect match, stop
        if (parentText.equalsIgnoreCase(childText))
            return;

        // if it's just REALLY short, also stop.
        if (childText.length() < 120)
            return;

        // Else, try calculating levenstein distance.
        // find the larger of the two strings
        int largerSize = parentText.length();
        if (childText.length() > largerSize)
            largerSize = childText.length();

        // looking for a 10% edit distance
        int maxDistToCheck = largerSize * 0.10;

        // if distance is above max distance, contains -1
        int rawDistance = StringUtils.getLevenshteinDistance(childText,parentText,maxDistToCheck);

        // we only want to continue if it's above the max (10% distance)
        if (rawDistance >= 0)
            return;


        // Assuming we are creating child tasks.
        def branchingLevels = [1:5, 2:5, 3:5, 4:1, 5:1, 6:1, 7:1, 8:1, 9:1, 10:0];

        int tasksToAdd = branchingLevels.get(nr.depth);
        int newNarrativeDepth = nr.depth+1;
        int newPriority = nr.priority+1;
        if (nr.priority > 50)
            newPriority = nr.priority-1;

        for (int i = 0; i < tasksToAdd; i++) {
            NarrativeRequest newRequest = new NarrativeRequest();
            newRequest.priority = newPriority;
            newRequest.depth = newNarrativeDepth;
            newRequest.root_narrative = nr.root_narrative;
            newRequest.parent_narrative = thisNarrative;
            newRequest.save();
        }

        //  def d = new Demographics(evt.assignmentView.answer).save()

        //User properties must be strings (for maximal generality)
        // evt.taskRun.setUserProperty("age", "${d.age}")





    }

    def preview() {

    }

    def start() {
        //  [taskrun: params.task, workerId: params.workerId, action: run.taskProperties.action,controller: run.taskProperties.controller, submiturl: run.submitUrl, assignmentId: params.assignmentId]

        Integer taskID;
        String assignmentID;
        String workerID;
        String rootNarrativeIDString;

        try {
            taskID = Integer.parseInt(params.get("task"))
        } catch (Exception e) {
            log.info("start() hit with no taskID, rendering preview");
            render(view: "preview.gsp");
            return;
        }

        assignmentID = params.get("assignmentId");
        if (assignmentID == null) {
            log.info("start() hit without assignmentID = \"" + assignmentID + "\", rendering preview");
            render(view: "preview.gsp");
            return;
        }

        workerID = params.get("workerId");
        if (workerID == null) {
            log.info("start() hit without workerID, rendering preview");
            render(view: "preview.gsp");
            return;
        }

        // Now that we have a taskID, assignmentID, and workerID, we can make
        // the assignment and have the user start working on it.
        log.info("start() called with assignmentID = \"" + assignmentID + "\"")

        Long rootNarrativeID = null;
        try {
            rootNarrativeID = Long.parseLong(params.get("rootNarrativeId"));
        } catch (Exception e) {
            rootNarrativeID = null;
        }

        Task t = null;
        if (rootNarrativeID == null) {
            t = Task.get(taskID);
            rootNarrativeID = Long.parseLong((t.getProperties().get("taskProperties")).getAt("parameter"));
        }

        NarrativeRequest nr = NardiffStuff.findRequest(rootNarrativeID);
        boolean askForDemographics = NardiffStuff.assignRequestToTurker(nr, workerID, assignmentID);
        params.setProperty("askForDemographics", askForDemographics);
        params.setProperty("narrativeRequest", nr);
        params.setProperty("task", t);

        // Generate the image
        nr.attach();
        Narrative parentNarrative = nr.getParent_narrative();

        String narrativeImagePath = servletContext.getRealPath("/images/narratives/");

        Text2PNG.writeImageFile(parentNarrative.text,parentNarrative.id, narrativeImagePath);


//        NarrativeRequest nr = NardiffStuff.findRequest(params.get("narrative_id"));
//        boolean askForDemographics = NardiffStuff.assignRequestToTurker(workerId);
//        int beginStage = 2;
//        if (askForDemographics == false)
//            beginStage = 3;

        log.info(params);
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
