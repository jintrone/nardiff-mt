package edu.msu.mi.gwurk

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.mturk.AmazonMTurk
import com.amazonaws.services.mturk.AmazonMTurkClient
import com.amazonaws.services.mturk.AmazonMTurkClientBuilder
import com.amazonaws.services.mturk.model.Comparator
import com.amazonaws.services.mturk.model.CreateHITRequest
import com.amazonaws.services.mturk.model.CreateHITResult
import com.amazonaws.services.mturk.model.HIT
import com.amazonaws.services.mturk.model.ListQualificationTypesRequest
import com.amazonaws.services.mturk.model.ListQualificationTypesResult
import com.amazonaws.services.mturk.model.Locale
import com.amazonaws.services.mturk.model.QualificationRequirement
import com.amazonaws.services.mturk.model.QualificationType
import grails.transaction.Transactional
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.web.mapping.LinkGenerator



@Log4j
@Transactional
class MturkAwsFacadeService {


    private static final String SANDBOX_ENDPOINT = "mturk-requester-sandbox.us-east-1.amazonaws.com";
    private static final String PRODUCTION_ENDPOINT = "mturk-requester.us-east-1.amazonaws.com";
    private static final String SIGNING_REGION = "us-east-1";


    //QUALIFICATION IDS
    private static final String WorkerNumberHitsApproved =  "00000000000000000040"
    private static final String WorkerPercentAssignmentsApproved = "000000000000000000L0"
    private static final String WorkerLocale = "00000000000000000071"




    def grailsApplication
    LinkGenerator grailsLinkGenerator


    HitView recycle(AmazonMTurkClient requesterService, HitView hitView) {
        expire(requesterService,hitView)
        launchHit(requesterService,hitView.taskRun)
    }

    def AmazonMTurkClient getRequesterService(Credentials cred, boolean real) {
        AmazonMTurkClientBuilder builder = AmazonMTurkClientBuilder.standard()
        builder.setCredentials(new AWSCredentialsProvider() {
            @Override
            AWSCredentials getCredentials() {
                new AWSCredentials() {
                    @Override
                    String getAWSAccessKeyId() {
                        cred.awsId
                    }

                    @Override
                    String getAWSSecretKey() {
                        cred.awsSecret
                    }
                }
            }

            @Override
            void refresh() {

            }
        })
        if (real) {
            builder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(PRODUCTION_ENDPOINT, SIGNING_REGION))
            log.info("Generating production endpoint")
        } else {
            log.info("Generating sandbox endpoint")
            builder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(SANDBOX_ENDPOINT, SIGNING_REGION))

        }
        builder.build()
    }


    def expire(AmazonMTurkClient requesterService, HitView hitView) {
        hitView.expire(requesterService)
    }

    def refresh(AmazonMTurkClient requesterService, TaskRun taskRun) {
        taskRun.activeHits.hits*.update(requesterService)
    }



    HitView launchOneHit(AmazonMTurkClient requesterService, TaskRun taskRun) {


        TaskProperties p = taskRun.taskProperties.clone() as TaskProperties
        p.maxAssignments = 1
        taskRun.attach()
        HitView result = new HitView(taskRun,launchHit(requesterService,p,taskRun.id))
        result.save()
        taskRun.addActive(result)
        result
    }


    HitView launchHit(AmazonMTurkClient requesterService, TaskRun taskRun) {

        taskRun.attach()
        HitView result = new HitView(taskRun,launchHit(requesterService,taskRun.taskProperties,taskRun.id))
        result.save()
        taskRun.addActive(result)
        result

    }

    HIT launchHit(AmazonMTurkClient requesterService, TaskProperties props, Long taskRunId) {
        //taskRun.attach()
        MturkHitProperties mprops = getProperties(props)
        List<QualificationRequirement> qrs = []

//        QualificationRequirement qr1 = new QualificationRequirement()
//        //seems like it should be somewhere in the library, huh?
//        //thanks amazon!!! :-P
//        qr1.setComparator(Comparator.GreaterThan)
//        //Worker_​NumberHITsApproved
//        qr1.setQualificationTypeId(WorkerNumberHitsApproved)
//        qr1.setIntegerValues([100])
//        qrs<<qr1



        QualificationRequirement qr2 = new QualificationRequirement();
        //Worker_​PercentAssignmentsApproved
        qr2.setQualificationTypeId(WorkerPercentAssignmentsApproved)
        qr2.setComparator(Comparator.GreaterThanOrEqualTo)
        qr2.setIntegerValues([98])
        qrs<<qr2

        QualificationRequirement qr3 = new QualificationRequirement();
        //Worker_Locale 
        qr3.setQualificationTypeId(WorkerLocale)
        qr3.setComparator(Comparator.EqualTo)
        Locale l = new Locale()
        l.setCountry("US")
        qr3.setLocaleValues([l])
        qrs<<qr3

        if (props.qualificationString) {
            String[] str = props.qualificationString.split()
            String qualName = str.length == 1 ? str[0] : str[1]
            QualificationType result = null
            try {
                ListQualificationTypesRequest request = new ListQualificationTypesRequest()
                request = request.withQuery(qualName).withMustBeRequestable(false).withMustBeOwnedByCaller(true)
                ListQualificationTypesResult lresult = requesterService.listQualificationTypes(request)
                if (lresult.numResults != 1) {
                    log.info("Could not identify a unique qualification type, bailing!")
                    //requesterService.createQualificationType()
                } else {

                    result = lresult.getQualificationTypes()[0]
                    log.info("Found qual type ${result}")
                    //svc.assignQualification(result.getQualificationType(0).qualificationTypeId, workerid, 0, true)
                    //println "Would assign ${result.getQualificationType(0).qualificationTypeId} to $workerid"
                }
            } catch (Exception e) {
                e.printStackTrace()
            }

            if (result) {
                QualificationRequirement qr4 = new QualificationRequirement()
                qr4.setQualificationTypeId(result.qualificationTypeId)
                if (str.length == 2 && str[0] == "not") {
                    qr4.setComparator(Comparator.DoesNotExist)
                } else {
                    qr4.setComparator(Comparator.Exists)
                }
                qrs << qr4
            }
        }


        log.info("Launch hit with : "+props.maxAssignments+" assignments")
        String url = "https://${grailsApplication.config.gwurk.hostname}:${grailsApplication.config.gwurk.port}${grailsLinkGenerator.link(action:"external",controller: "workflow", params:[task:taskRunId])}"
        log.info("Would link: ${url}")

//        log.info("Not really launching")


        CreateHITRequest request = new CreateHITRequest()
        request.setAssignmentDurationInSeconds(mprops.getAssignmentDuration(60 * 5))
        request.setAutoApprovalDelayInSeconds(mprops.getAutoApprovalDelay(60 * 30))
        request.setReward(String.valueOf(mprops.getRewardAmount(0)))
        request.setTitle(mprops.getTitle("No title"))
        request.setKeywords(mprops.getKeywords(null))
        request.setLifetimeInSeconds(props.lifetime)
        request.setMaxAssignments(mprops.getMaxAssignments(1))
        request.setQualificationRequirements(qrs)
        request.setQuestion(getExternalQuestion(url, props.height))
        request.setDescription(mprops.getDescription("No description"))


        CreateHITResult r = requesterService.createHIT (request)

//                null, // hitTypeId
//                mprops.getTitle("No title"),
//                mprops.getDescription("No description"),
//                mprops.getKeywords(null), // keywords
//                getExternalQuestion(url, props.height),
//                mprops.getRewardAmount(0),
//                mprops.getAssignmentDuration(60 * 5),
//                mprops.getAutoApprovalDelay(60 * 30),
//                props.lifetime,
//                mprops.getMaxAssignments(1),
//                "", // requesterAnnotation
//                [qualReq,qualReq1] as QualificationRequirement[], // qualificationRequirements
//                (String[])["Minimal", "HITDetail", "HITQuestion", "HITAssignmentSummary"] as String[], // responseGroup
//                null, // uniqueRequestToken
//                null, // assignmentReviewPolicy
//                null); // hitReviewPolicy

        r.HIT



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
        props.lifetime = properties.lifetime

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
