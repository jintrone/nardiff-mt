package edu.msu.mi.gwurk

import com.amazonaws.mturk.requester.Comparator
import com.amazonaws.mturk.requester.HIT
import com.amazonaws.mturk.requester.QualificationRequirement
import com.amazonaws.mturk.requester.SearchQualificationTypesResult
import com.amazonaws.mturk.service.axis.RequesterService
import grails.transaction.Transactional
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import static com.amazonaws.mturk.requester.Comparator.*

@Log4j
@Transactional
class MturkAwsFacadeService {

    def grailsApplication
    LinkGenerator grailsLinkGenerator


    HitView recycle(RequesterService requesterService, HitView hitView) {
        expire(requesterService,hitView)
        launchHit(requesterService,hitView.taskRun)
    }

    def RequesterService getRequesterService(TaskRun run) {
        if (run.activeWorkflowRun.real) {

        } else {

        }
    }


    def expire(RequesterService requesterService, HitView hitView) {
        hitView.expire(requesterService)
    }

    def refresh(RequesterService requesterService, TaskRun taskRun) {
        taskRun.activeHits.hits*.update(requesterService)
    }



    HitView launchOneHit(RequesterService requesterService, TaskRun taskRun) {


        TaskProperties p = taskRun.taskProperties.clone() as TaskProperties
        p.maxAssignments = 1
        taskRun.attach()
        HitView result = new HitView(taskRun,launchHit(requesterService,p,taskRun.id))
        result.save()
        taskRun.addActive(result)
        result
    }


    HitView launchHit(RequesterService requesterService, TaskRun taskRun) {

        taskRun.attach()
        HitView result = new HitView(taskRun,launchHit(requesterService,taskRun.taskProperties,taskRun.id))
        result.save()
        taskRun.addActive(result)
        result

    }

    HIT launchHit(RequesterService requesterService, TaskProperties props, Long taskRunId) {
        //taskRun.attach()
        MturkHitProperties mprops = getProperties(props)

        QualificationRequirement qualReq = new QualificationRequirement();
        qualReq.setQualificationTypeId(RequesterService.LOCALE_QUALIFICATION_TYPE_ID);
        qualReq.setComparator(EqualTo);
        com.amazonaws.mturk.requester.Locale country = new com.amazonaws.mturk.requester.Locale();
        country.setCountry("US");
        qualReq.setLocaleValue(country);

        QualificationRequirement qualReq1 = new QualificationRequirement();
        qualReq1 = new QualificationRequirement();
        qualReq1.setQualificationTypeId(RequesterService.APPROVAL_RATE_QUALIFICATION_TYPE_ID)
        qualReq1.setComparator(com.amazonaws.mturk.requester.Comparator.GreaterThanOrEqualTo);
        qualReq1.setIntegerValue(98)


        if (props.qualificationString) {
            String[] str = props.qualificationString.split()
            String qualName = str.length==1?str[0]:str[1]
            try {

                SearchQualificationTypesResult result = requesterService.searchQualificationTypes(qualName, false, true, null, null, null, null)
                if (result.numResults != 1) {
                    println("Could not identify a unique qualification type, creating!")
                    requesterService.createQualificationType()
                } else {
                    //svc.assignQualification(result.getQualificationType(0).qualificationTypeId, workerid, 0, true)
                    //println "Would assign ${result.getQualificationType(0).qualificationTypeId} to $workerid"
                }
            } catch (
                    Exception e
                    ) {
                e.printStackTrace()
            }

            if (str.length > 1) {
                if (str[0] == "not") {

                }
            }
        }

        log.info("Launch hit with : "+props.maxAssignments+" assignments")
        String url = "https://${grailsApplication.config.gwurk.hostname}:${grailsApplication.config.gwurk.port}${grailsLinkGenerator.link(action:"external",controller: "workflow", params:[task:taskRunId])}"
        log.info("Would link: ${url}")

//        log.info("Not really launching")

        requesterService.createHIT (
                null, // hitTypeId
                mprops.getTitle("No title"),
                mprops.getDescription("No description"),
                mprops.getKeywords(null), // keywords
                getExternalQuestion(url, props.height),
                mprops.getRewardAmount(0),
                mprops.getAssignmentDuration(60 * 5),
                mprops.getAutoApprovalDelay(60 * 30),
                props.lifetime,
                mprops.getMaxAssignments(1),
                "", // requesterAnnotation
                [qualReq,qualReq1] as QualificationRequirement[], // qualificationRequirements
                (String[])["Minimal", "HITDetail", "HITQuestion", "HITAssignmentSummary"] as String[], // responseGroup
                null, // uniqueRequestToken
                null, // assignmentReviewPolicy
                null); // hitReviewPolicy



    }


    static MturkHitProperties getProperties(TaskProperties properties) {
        def props = new MturkHitProperties()
        props.title = properties.title
        props.description = properties.description
        props.autoApprovalDelay = properties.autoApprove?0:24*60*60
        props.keywords = properties.keywords
        props.maxAssignments = properties.maxAssignments
        props.assignmentDuration = properties.assignmentDuration
        props.rewardAmount = properties.rewardAmount
        props.lifetime = properties.lifetime as String

        props
    }

    static String getExternalQuestion(String url,int frameHeight) {
        String question = String.format("<ExternalQuestion xmlns=\"http://mechanicalturk.amazonaws.com/AWSMechanicalTurkDataSchemas/2006-07-14/ExternalQuestion.xsd\">" +
                "<ExternalURL>%s</ExternalURL>" +
                "<FrameHeight>%d</FrameHeight>" +
                "</ExternalQuestion>", url,frameHeight);
        log.info("Attempt to launch external hit: "+question);
        return question;
    }

    BufferedOutputStream getExternalFile() {

    }


}
