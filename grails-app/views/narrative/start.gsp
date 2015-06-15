<%@ page import="groovy.json.JsonSlurper; nardiff.mt.NardiffStuff" contentType="text/html;charset=UTF-8" %>
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
<div ng-controller="WorkflowController as wf" ng-init="askForDemographics = ${askForDemographics};wf.advance()" id="mainPanel">

    <timer></timer>

    <div>
        <em class="grayish">This HIT is under development; please <a href="mailto:jintrone@msu.edu"
                                                                     target="_blank">email</a> if you encounter problems.
        </em>
    </div>




    <div ng-show="stage === 2">
        <p>To start with, we need a small amount of demographic information.  This will be the only time we ask for this information.</p>

        <form ng-submit="demographics.workerid='${workerId}';wf.submitDemographics(demographics);wf.advance()"
              class="gwurkignore">
            <p>Year of Birth:

            <g:select id="age" name="age" ng-model="demographics.age" from="${((1905..1998) as List).reverse()}"
                      noSelection="['': '-Choose your birth year-']"/>

            </p>


            <!--   <input type="number" min="18" max="110" name="age" ng-model="wf.age" required="Please enter a whole number 18 or greater"></p> -->

            <p>Sex:

                <input ng-click="demographics.gender = 'M'" type="radio" name="gender"> Male,
                <input ng-click="demographics.gender = 'F'" type="radio" name="gender"> Female,
                <input ng-click="demographics.gender = 'O'" type="radio" name="gender"> Other</p>

            <p>Education:</p>

            <input ng-click="demographics.education = '1'" type="radio" name="edu" value="1">Less than High School<br/>
            <input ng-click="demographics.education = '2'" type="radio" name="edu" value="2">High School<br/>
            <input ng-click="demographics.education = '3'" type="radio" name="edu" value="3">Some College<br/>
            <input ng-click="demographics.education = '4'" type="radio" name="edu" value="4">Associate Degree<br/>
            <input ng-click="demographics.education = '5'" type="radio" name="edu" value="5">Bachelors Degree<br/>
            <input ng-click="demographics.education = '6'" type="radio" name="edu" value="6">Masters Degree<br/>
            <input ng-click="demographics.education = '7'" type="radio" name="edu"
                   value="7">Graduate/Professional Degree<br/>


            <p><button type="submit" class="button">Continue</button></p>
        </form>

    </div>


    <div ng-show="stage === 3">
        <p><b>Instructions.</b>  Please read the story on the following page. You will have up to <em>1 minute</em>.
        Afterward, you will be asked to
        retell as much of the story as possible in your own words for the
        next person.</p>

        <p>
            Please retell the story as best you can and do not copy/paste. Any assignment not meeting these criteria will be <b>rejected</b>.
        </p>


        <p><button class="button" ng-click="wf.advance()">Continue to Story</button></p>

    </div>

    <div ng-show="stage === 4" id="img-div">

        <p id="toremove"><img src="/nardiff-mt/narrative/storyImage?narrative=${narrative.id as Long}"/>

        <p>Time Remaining: {{(60-elapsedTime) + " seconds" }}</p>

        <p><button class="button" ng-click="wf.advance()">Skip Remaining Time</button></p>
    </div>

    <div ng-show="stage === 5">
        <p>
            <%
                JsonSlurper slurper = new JsonSlurper()
                def distractor = slurper.parse(narrative.root_narrative.distractorTask as char[])

            %>
            ${distractor.probe}
        </p>

        <p>

            ${distractor.question}
        </p>

        <form>
            <g:each in="${distractor.answers}" var="answer" status="idx">
                <input ng-click="distractorAnswer = '${idx}'; wf.advance()" type="radio"
                       name="dt">${answer}<br/>
            </g:each>
        </form>

    </div>

    <div ng-show="stage === 6">
        In the text box below, please retell the story for the next person.
        <form>
            <textarea ng-model="story" name="story"></textarea><br>

            <button class="button" ng-click="wf.advance()">Continue</button>
        </form>
    </div>

    <div ng-show="stage === 7">
        <p>Thank you for your participation in this study.  Clicking the button below will submit
        your answers and complete this HIT.</p>

        <form action="complete" method="POST">

            <input type="hidden" name="parent" value="${narrative.id}">
            <input type="hidden" name="distractorAnswer" value="{{ distractorAnswer }}">
            <input type="hidden" name="story" value="{{ story }}">

            <input type="hidden" name="storyTime" value="{{ storyTime }}">
            <input type="hidden" name="distractorTime" value="{{ distractorTime }}">
            <input type="hidden" name="retellTime" value="{{ retellTime }}">


            <g:submitButton name="Complete HIT" class="button">Complete HIT</g:submitButton>
        </form>

    </div>
</div>
</body>
</html>
