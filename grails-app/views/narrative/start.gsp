<%@ page import="groovy.json.JsonSlurper; nardiff.mt.NarrativeRequest; nardiff.mt.NardiffStuff" contentType="text/html;charset=UTF-8" %>
<html ng-app="nardiff">
<head>
    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.13/angular.js"></script>
    <script type="text/javascript" src="https://code.jquery.com/jquery-1.11.2.min.js"></script>
    <script type="text/javascript" src="/nardiff-mt/js/nardiff.js"></script>

    <title></title>

    <style>
    #mainPanel {
        margin-left: auto;
        margin-right: auto;
        width: 500px;

        font-size: 125%;
    }

    img.center {
        display: block;
        margin-left: auto;
        margin-right: auto;
    }

    textarea {
        width: 100%;
        height: 200px;
    }

    .button {
        font-size: 125%;
    }

        .grayish {
            color: #555555;
        }
    </style>
</head>

<body>
<div ng-controller="WorkflowController as wf" id="mainPanel">

    <%
        NarrativeRequest nr = params.get("narrativeRequest")
        boolean askForDemographics = params.get("askForDemographics")
        int beginStage = askForDemographics?2:3;



    %>
    <timer></timer>
    <div>
        <em class="grayish">This HIT is under development; please <a href="mailto:jintrone@msu.edu" target="_blank">email</a> if you encounter problems.</em>
    </div>

    <div ng-show="stage === 1">

        <p><b>Instructions.</b>  When you click the 'begin' button, you will
        ${askForDemographics?"be asked for some basic demographic information and then ":"be "}
        given 2 minutes to read a very short story. Afterward, you will be asked to
        retell as much of the story as possible in your own words for the
        next person.</p>
        <p>
        Please retell the story as best you can and do not copy/paste. Any assignment not meeting these criteria will be <b>rejected</b>.
        </p>

        <p>
            <button class="button" ng-click="stage = <%=beginStage%>; wf.request_id = <%=
                nr.id
            %>; ">Begin</button>
        </p>
    </div>


    <div ng-show="stage === 2">
        <p>To start with, we need a small amount of demographic information.</p>

        <form ng-submit="wf.demographics.workerid='${workerId}';wf.submitDemographics(wf.demographics)" class="gwurkignore">
            <p>Year of Birth:

                <g:select id="age" name="age" ng-model="wf.demographics.age" from="${((1905..1998) as List).reverse()}" noSelection="['':'-Choose your birth year-']"/>

            </p>


            <!--   <input type="number" min="18" max="110" name="age" ng-model="wf.age" required="Please enter a whole number 18 or greater"></p> -->

            <p>Sex:

                <input ng-click="wf.demographics.gender = 'M'" type="radio" name="gender"> Male,
                <input ng-click="wf.demographics.gender = 'F'" type="radio" name="gender"> Female,
                <input ng-click="wf.demographics.gender = 'O'" type="radio" name="gender"> Other</p>

            <p>Education:</p>

            <input ng-click="wf.demographics.education = '1'" type="radio" name="edu" value="1">Less than High School<br/>
            <input ng-click="wf.demographics.education = '2'" type="radio" name="edu" value="2">High School<br/>
            <input ng-click="wf.demographics.education = '3'" type="radio" name="edu" value="3">Some College<br/>
            <input ng-click="wf.demographics.education = '4'" type="radio" name="edu" value="4">Associate Degree<br/>
            <input ng-click="wf.demographics.education = '5'" type="radio" name="edu" value="5">Bachelors Degree<br/>
            <input ng-click="wf.demographics.education = '6'" type="radio" name="edu" value="6">Masters Degree<br/>
            <input ng-click="wf.demographics.education = '7'" type="radio" name="edu" value="7">Graduate/Professional Degree<br/>


            <p><button type="submit" class="button">Continue</button></p>
        </form>


    </div>


    <div ng-show="stage === 3">
        <p><b>Instructions.</b>  Please spend up to <em>2 minutes</em> reading the story below.
        Afterward, you will be asked to retell as much of the story as possible in your own
        words for the next person. Do not copy/paste; any assignment that is copied / pasted
        will be rejected.
        </p>

        <p><button class="button" ng-click="stage = 4; wf.startTimer();">Continue to Story</button></p>

    </div>

    <div ng-show="stage === 4" id="img-div">


        <p id="toremove"><img src="/nardiff-mt/narrative/storyImage?narrativeRequestId=${nr.id}"/>

        <p>Time Remaining: {{ timeRemaining + " seconds" }}</p>

        <p><button class="button" ng-click="wf.st(wf); stage = 5">Skip Remaining Time</button></p>
    </div>

    <div ng-show="stage === 5">
        <p>
        <%
         JsonSlurper slurper = new JsonSlurper()
         def distractor = slurper.parse(nr.parent_narrative.distractorTask as char[])

        %>
            ${distractor.probe}
        </p>
        <p>

           ${distractor.question}
        </p>

        <form>
            <g:each in="${distractor.answers}" var="answer" status="idx">
            <input ng-click="wf.distractorAnswer = '${idx}'; wf.dt(wf); stage = 6" type="radio"
                   name="dt">${answer}<br/>
            </g:each>
        </form>

    </div>

    <div ng-show="stage === 6">
        In the text box below, please retell the story for the next person.
        <form>
            <textarea ng-model="wf.story" name="story"></textarea><br>

            <button class="button" ng-click="wf.rt(wf); stage = 7">Continue</button>
        </form>
    </div>

    <div ng-show="stage === 7">
        <p>Thank you for your participation in this study.  Clicking the button below will submit
        your answers and complete this HIT.</p>

        <form action="complete" method="POST">

            <input type="hidden" name="request_id" value="{{ wf.request_id }}">
            <input type="hidden" name="distractorAnswer" value="{{ wf.distractorAnswer }}">
            <input type="hidden" name="story" value="{{ wf.story }}">

            <input type="hidden" name="storyTime" value="{{ wf.storyTime }}">
            <input type="hidden" name="distractorTime" value="{{ wf.distractorTime }}">
            <input type="hidden" name="retellTime" value="{{ wf.retellTime }}">


            <g:submitButton name="Complete HIT" class="button">Complete HIT</g:submitButton>
        </form>

    </div>
</div>
</body>
</html>
