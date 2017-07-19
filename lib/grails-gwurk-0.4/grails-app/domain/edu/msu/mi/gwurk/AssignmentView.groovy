package edu.msu.mi.gwurk

import com.amazonaws.services.mturk.AmazonMTurkClient
import com.amazonaws.services.mturk.model.ApproveAssignmentRequest
import com.amazonaws.services.mturk.model.Assignment
import com.amazonaws.services.mturk.model.AssignmentStatus
import com.amazonaws.services.mturk.model.GetAssignmentRequest
import com.amazonaws.services.mturk.model.GetFileUploadURLRequest
import com.amazonaws.services.mturk.model.ServiceException


class AssignmentView {

    static constraints = {
        approvalTime nullable: true
        rejectTime nullable: true
        requestorFeedback nullable: true
        data nullable: true


    }

    static mapping = {
        rawAnswer column: "raw_answer", sqlType: "text" //, length: 8192
        data sqlType: "blob"

    }
    static enum Status {
        SUBMITTED, APPROVED, REJECTED
    }


    static belongsTo = [hit: HitView]

    boolean processed
    Status assignmentStatus
    String assignmentId
    String rawAnswer
    Date submitTime
    Date acceptTime
    String workerId
    boolean hasData = false
    byte[] data


    Date approvalTime
    Date rejectTime
    String requestorFeedback


    public AssignmentView(Assignment a) {
        this.assignmentStatus = Status.SUBMITTED
        this.processed = false
        this.assignmentId = a.assignmentId
        this.workerId = a.workerId
        this.acceptTime = a.acceptTime
        this.submitTime = a.submitTime
        this.hit = HitView.find { hitId == "${a.getHITId()}" }
        this.rawAnswer = a.answer
        hasData = getFileKey()!=null
        log.info("Has data? ${hasData}")
       save()
    }

    def update(AmazonMTurkClient service) {
        def a = service.getAssignment(new GetAssignmentRequest().withAssignmentId(assignmentId)).assignment
        switch (AssignmentStatus.fromValue(a.assignmentStatus)) {
            case AssignmentStatus.Submitted:
                assignmentStatus = Status.SUBMITTED
                if (hit.taskRun.taskProperties.autoApprove) {
                    try {
                        service.approveAssignment(new ApproveAssignmentRequest().withAssignmentId(assignmentId).
                                withRequesterFeedback("Thanks for your help!"))
                        this.approvalTime = new Date()
                        update(service)
                    } catch (ServiceException ex) {
                        log.warn("Service exception while approving hit: ${ex}")
                    }
                }
                break

            case AssignmentStatus.Approved:
                assignmentStatus = Status.APPROVED
                break

            case AssignmentStatus.Rejected:
                assignmentStatus = Status.REJECTED
                break
        }
        if (assignmentStatus == Status.APPROVED) {
            this.approvalTime = a.approvalTime ?: this.approvalTime

        } else if (assignmentStatus == Status.REJECTED) {
            this.rejectTime = a.rejectionTime ?: this.rejectTime
        }

        this.requestorFeedback = a?.requesterFeedback

        if (hasData && !data) {
            log.info("Retrieve data from service")
            retrieveFile(service)
        }

        save()
        if (hasErrors()) {
            log.warn(errors)
        }

    }

    public Map getAnswer() {
        return extractAnswers(rawAnswer)
    }

    public static Map extractAnswers(String answer) {
        def answers = new XmlSlurper().parseText(answer)
        answers.Answer.collectEntries { a ->
                if (a.FreeText) {
                    [a.QuestionIdentifier, a.freeText]
                } else if (a.SelectionIdentifier) {
                    [a.QuestionIdentifier, a.SelectionIdentifier]
                } else if (a.OtherSelectionText) {
                    [a.QuestionIdentifier, a.OtherSelectionText]
                }  else if (a.UploadedFileSizeInBytes) {
                    [a.QuestionIdentifier, [size: a.UploadedFileSizeInBytes, key: a.UploadedFileKey]]
                } else {
                    [ : ]
                }
        }

    }

    private Map.Entry getFileKey() {
        getAnswer().find { k, v ->
            return (v.hasProperty("size") && v.hasProperty("key"))
        }
    }

    private retrieveFile(AmazonMTurkClient service) {
        Map.Entry ent = getFileKey()
        log.info("Retrieving file data ${ent.value.size}")
        String s = service.getFileUploadURL(new GetFileUploadURLRequest().withAssignmentId(assignmentId).withQuestionIdentifier(ent.key))
        data = new byte[ent.value.size as int];
        DataInputStream dataIs = new DataInputStream(new URL(s).openConnection().inputStream)
        dataIs.readFully(data);
        dataIs.close()

    }


}
