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
        width: 750px;

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

    ul.likert {
        list-style-type: none;
        margin: 0;
        padding: 0;
        overflow: hidden;
    }

    ul.likert li {
        text-align: center;
        margin-left: 20px;
        margin-right: 20px;
        float: left;
    }

    div.minor-section {
        margin: 10px;
        padding: 10px;
        background: #b4c6d3;
    }

    </style>

</head>

<body>
<div ng-controller="WorkflowController as wf" ng-init="stage=${narrative.data.stage};narrative=${narrative.id};askForDemographics = ${askForDemographics};assignmentId='${params.assignmentId}';wf.advance()" id="mainPanel">

    <timer></timer>

    <div>
        <em class="grayish">This HIT is under development; please <a href="mailto:jintrone@msu.edu"
                                                                     target="_blank">email</a> if you encounter problems.
        </em>
    </div>




    <div ng-show="stage === 2">
        <p>To start with, we need a small amount of demographic information.  This will be the only time we ask for this information.</p>

        <form ng-submit="demographics.workerid='${params.workerId}';wf.submitDemographics();wf.advance()"
              class="gwurkignore">
            <p>Year of Birth:

            <g:select id="age" name="age" ng-model="demographics.age" from="${((1905..1998) as List).reverse()}"
                      noSelection="['': '-Choose your birth year-']"/>

            </p>


            <!--   <input type="number" min="18" max="110" name="age" ng-model="wf.age" required="Please enter a whole number 18 or greater"></p> -->

            <p>What is your gender?
                <ul>
                <li><input ng-click="demographics.gender = 'M'" type="radio" name="gender" required> Male</li>
                <li><input ng-click="demographics.gender = 'F'" type="radio" name="gender" required> Female</li>
                <li><input ng-click="demographics.gender = 'O'" type="radio" name="gender" required> Other,</li>
                <li><input ng-click="demographics.gender = 'N'" type="radio" name="gender" required> Prefer not to answer</li>
                </ul>
            </p>
            <p>Are you of Hispanic, Latino/a, or Spanish origin?
                <ul>
                <li><input ng-click="demographics.hispanic = 'Yes'" type="radio" name="hispanic" required> Yes,</li>
                <li><input ng-click="demographics.hispanic = 'No'" type="radio" name="hispanic" required> No</li>
                </ul>
            </p>
            <p>What is your race? One or more categories may be selected. Mark all that apply.
                <ul>
                <li><input ng-click="demographics.race = 'Black'" type="checkbox" name="race"> Black or African American,</li>
                <li><input ng-click="demographics.race = 'AInd'" type="checkbox" name="race"> American Indian or Alaska Native,</li>
                <li><input ng-click="demographics.race = 'Chin'" type="checkbox" name="race"> Chinese,</li>
                <li><input ng-click="demographics.race = 'Fili'" type="checkbox" name="race"> Filipino,</li>
                <li><input ng-click="demographics.race = 'Japa'" type="checkbox" name="race"> Japanese,</li>
                <li><input ng-click="demographics.race = 'Kore'" type="checkbox" name="race"> Korean,</li>
                <li><input ng-click="demographics.race = 'Viet'" type="checkbox" name="race"> Vietnamese,</li>
                <li><input ng-click="demographics.race = 'OAsian'" type="checkbox" name="race"> Other Asian,</li>
                <li><input ng-click="demographics.race = 'NatHaw'" type="checkbox" name="race"> Native Hawaiian,</li>
                <li><input ng-click="demographics.race = 'Samo'" type="checkbox" name="race"> Samoan,</li>
                <li><input ng-click="demographics.race = 'OPacI'" type="checkbox" name="race"> Other Pacific Islander</li>
                <li>Other:<input ng-click="demographics.otherrace = 'OPacI'" type="text" name="otherrace"/></li>
            </ul>
            </p>


            <p>Education:
            <ul>
            <li><input ng-click="demographics.education = '1'" type="radio" name="edu" value="1" required>Less than 8 years<br/></li>
            <li><input ng-click="demographics.education = '2'" type="radio" name="edu" value="2" required>8 to 11 years<br/></li>
            <li><input ng-click="demographics.education = '3'" type="radio" name="edu" value="3" required>12 years or completed high school / GED<br/></li>
            <li><input ng-click="demographics.education = '4'" type="radio" name="edu" value="4" required>Post high school training other than college<br/></li>
            <li><input ng-click="demographics.education = '5'" type="radio" name="edu" value="5" required>Some college<br/></li>
            <li><input ng-click="demographics.education = '6'" type="radio" name="edu" value="6" required>College graduate<br/></li>
            <li><input ng-click="demographics.education = '7'" type="radio" name="edu" value="7" required>Postgraduate<br/></li>
            </ul>
            </p>

            <p>Are you currently enrolled in an institution of higher learning?
                <ul>
                <li><input ng-click="demographics.student = 'Yes'" type="radio" name="student" required> Yes,</li>
                <li><input ng-click="demographics.student = 'No'" type="radio" name="student" required> No</li>
        </ul>
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
        <h3>
            ${narrative.root_narrative.title}
        </h3>
        <p id="toremove"><img src="/nardiff-mt/narrative/storyImage?narrative=${narrative.id as Long}"/>

        <p>Time Remaining: {{(60-elapsedTime) + " seconds" }}</p>

        <p><button class="button" ng-click="wf.advance()">Skip Remaining Time</button></p>
    </div>
    <div ng-show="stage === 5">

        <h2>Please answer the following questions about how engaging the story you just read was.</h2>
        <%
            JsonSlurper slurper = new JsonSlurper()
            def actions = slurper.parse(narrative.root_narrative.survey as char[])

        %>
        <form ng-submit="survey.workerid='${params.workerId}';survey.narrativeId='${narrative.root_narrative.id}';wf.submitSurvey();wf.advance()"
              class="gwurkignore">
            <div class="minor-section">
                <p>
                    I could picture myself in the scene of the events described in the narrative.
                </p>
                <ul class="likert">
                    <li><input ng-click="survey.tx1 = '1'" type="radio" name="tx1" value="1" required><br/>Not at all</li>
                    <li><input ng-click="survey.tx1 = '2'" type="radio" name="tx1" value="2" required></li>
                    <li><input ng-click="survey.tx1 = '3'" type="radio" name="tx1" value="3" required></li>
                    <li><input ng-click="survey.tx1 = '4'" type="radio" name="tx1" value="4" required></li>
                    <li><input ng-click="survey.tx1 = '5'" type="radio" name="tx1" value="5" required></li>
                    <li><input ng-click="survey.tx1 = '6'" type="radio" name="tx1" value="6" required></li>
                    <li><input ng-click="survey.tx1 = '7'" type="radio" name="tx1" value="7" required><br/>Very much</li>
                </ul>
            </div>
            <div class="minor-section">
                <p>
                    I was mentally involved in the narrative while reading it.
                </p>
                <ul class="likert">
                    <li><input ng-click="survey.tx2 = '1'" type="radio" name="tx2" value="1" required><br/>Not at all</li>
                    <li><input ng-click="survey.tx2 = '2'" type="radio" name="tx2" value="2" required></li>
                    <li><input ng-click="survey.tx2 = '3'" type="radio" name="tx2" value="3" required></li>
                    <li><input ng-click="survey.tx2 = '4'" type="radio" name="tx2" value="4" required></li>
                    <li><input ng-click="survey.tx2 = '5'" type="radio" name="tx2" value="5" required></li>
                    <li><input ng-click="survey.tx2 = '6'" type="radio" name="tx2" value="6" required></li>
                    <li><input ng-click="survey.tx2 = '7'" type="radio" name="tx2" value="7" required><br/>Very much</li>
                </ul>
            </div>

            <div class="minor-section">
                <p>
                    I wanted to learn how the narrative ended.
                </p>
                <ul class="likert">
                    <li><input ng-click="survey.tx3 = '1'" type="radio" name="tx3" value="1" required><br/>Not at all</li>
                    <li><input ng-click="survey.tx3 = '2'" type="radio" name="tx3" value="2" required></li>
                    <li><input ng-click="survey.tx3 = '3'" type="radio" name="tx3" value="3" required></li>
                    <li><input ng-click="survey.tx3 = '4'" type="radio" name="tx3" value="4" required></li>
                    <li><input ng-click="survey.tx3 = '5'" type="radio" name="tx3" value="5" required></li>
                    <li><input ng-click="survey.tx3 = '6'" type="radio" name="tx3" value="6" required></li>
                    <li><input ng-click="survey.tx3 = '7'" type="radio" name="tx3" value="7" required><br/>Very much</li>
                </ul>
            </div>

        <div class="minor-section">
            <p>
                Please select Very much for this question.
            </p>
            <ul class="likert">
                <li><input ng-click="survey.txc = '1'" type="radio" name="txc" value="1" required><br/>Not at all</li>
                <li><input ng-click="survey.txc = '2'" type="radio" name="txc" value="2" required></li>
                <li><input ng-click="survey.txc = '3'" type="radio" name="txc" value="3" required></li>
                <li><input ng-click="survey.txc = '4'" type="radio" name="txc" value="4" required></li>
                <li><input ng-click="survey.txc = '5'" type="radio" name="txc" value="5" required></li>
                <li><input ng-click="survey.txc = '6'" type="radio" name="txc" value="6" required></li>
                <li><input ng-click="survey.txc = '7'" type="radio" name="txc" value="7" required><br/>Very much</li>
            </ul>
        </div>

            <div class="minor-section">
                <p>
                    The narrative affected me emotionally.
                </p>
                <ul class="likert">
                    <li><input ng-click="survey.tx4 = '1'" type="radio" name="tx4" value="1" required><br/>Not at all</li>
                    <li><input ng-click="survey.tx4 = '2'" type="radio" name="tx4" value="2" required></li>
                    <li><input ng-click="survey.tx4 = '3'" type="radio" name="tx4" value="3" required></li>
                    <li><input ng-click="survey.tx4 = '4'" type="radio" name="tx4" value="4" required></li>
                    <li><input ng-click="survey.tx4 = '5'" type="radio" name="tx4" value="5" required></li>
                    <li><input ng-click="survey.tx4 = '6'" type="radio" name="tx4" value="6" required></li>
                    <li><input ng-click="survey.tx4 = '7'" type="radio" name="tx4" value="7" required><br/>Very much</li>
                </ul>
            </div>

            <div class="minor-section">
                <p>
                    While reading the narrative I had a vivid image of the events in the story.
                </p>
                <ul class="likert">
                    <li><input ng-click="survey.tx5 = '1'" type="radio" name="tx5" value="1" required><br/>Not at all</li>
                    <li><input ng-click="survey.tx5 = '2'" type="radio" name="tx5" value="2" required></li>
                    <li><input ng-click="survey.tx5 = '3'" type="radio" name="tx5" value="3" required></li>
                    <li><input ng-click="survey.tx5 = '4'" type="radio" name="tx5" value="4" required></li>
                    <li><input ng-click="survey.tx5 = '5'" type="radio" name="tx5" value="5" required></li>
                    <li><input ng-click="survey.tx5 = '6'" type="radio" name="tx5" value="6" required></li>
                    <li><input ng-click="survey.tx5 = '7'" type="radio" name="tx5" value="7" required><br/>Very much</li>
                </ul>
            </div>

            <h2>Please indicate how much you agree with the following statements about the information in the story.</h2>


            <div class="minor-section">
                <p>
                    The information in the message I just read was credible.
                </p>
                <ul class="likert">
                    <li><input ng-click="survey.if1 = '1'" type="radio" name="if1" value="1" required><br/>Strongly agree</li>
                    <li><input ng-click="survey.if1 = '2'" type="radio" name="if1" value="2" required></li>
                    <li><input ng-click="survey.if1 = '3'" type="radio" name="if1" value="3" required><br/>Neither agree nor disagree
                    </li>
                    <li><input ng-click="survey.if1 = '4'" type="radio" name="if1" value="4" required></li>
                    <li><input ng-click="survey.if1 = '5'" type="radio" name="if1" value="5" required><br/>Strongly disagree</li>
                </ul>
            </div>
            <div class="minor-section">
                <p>
                    The information in the message I just read was believable.
                </p>
                <ul class="likert">
                    <li><input ng-click="survey.if2 = '1'" type="radio" name="if2" value="1" required><br/>Strongly disagree</li>
                    <li><input ng-click="survey.if2 = '2'" type="radio" name="if2" value="2" required></li>
                    <li><input ng-click="survey.if2 = '3'" type="radio" name="if2" value="3" required><br/>Neither agree nor disagree
                    </li>
                    <li><input ng-click="survey.if2 = '4'" type="radio" name="if2" value="4" required></li>
                    <li><input ng-click="survey.if2 = '5'" type="radio" name="if2" value="5" required><br/>Strongly agree</li>
                </ul>
            </div>
            <div class="minor-section">
                <p>
                    The information in the message I just read was new.
                </p>
                <ul class="likert">
                    <li><input ng-click="survey.if3 = '1'" type="radio" name="if3" value="1" required><br/>Strongly disagree</li>
                    <li><input ng-click="survey.if3 = '2'" type="radio" name="if3" value="2" required></li>
                    <li><input ng-click="survey.if3 = '3'" type="radio" name="if3" value="3" required><br/>Neither agree nor disagree
                    </li>
                    <li><input ng-click="survey.if3 = '4'" type="radio" name="if3" value="4" required></li>
                    <li><input ng-click="survey.if3 = '5'" type="radio" name="if3" value="5" required><br/>Strongly agree</li>
                </ul>
            </div>
            <div class="minor-section">
                <p>
                    The information in the message I just read was unconvincing.
                </p>
                <ul class="likert">
                    <li><input ng-click="survey.if4 = '1'" type="radio" name="if4" value="1" required><br/>Strongly disagree</li>
                    <li><input ng-click="survey.if4 = '2'" type="radio" name="if4" value="2" required></li>
                    <li><input ng-click="survey.if4 = '3'" type="radio" name="if4" value="3" required><br/>Neither agree nor disagree
                    </li>
                    <li><input ng-click="survey.if4 = '4'" type="radio" name="if4" value="4" required></li>
                    <li><input ng-click="survey.if4 = '5'" type="radio" name="if4" value="5" required><br/>Strongly agree</li>
                </ul>
            </div>
        <div class="minor-section">
            <p>
                Please select the item 'Neither agree nor disagree'
            </p>
            <ul class="likert">
                <li><input ng-click="survey.ifc = '1'" type="radio" name="ifc" value="1" required><br/>Strongly disagree</li>
                <li><input ng-click="survey.ifc = '2'" type="radio" name="ifc" value="2" required></li>
                <li><input ng-click="survey.ifc = '3'" type="radio" name="ifc" value="3" required><br/>Neither agree nor disagree
                </li>
                <li><input ng-click="survey.ifc = '4'" type="radio" name="ifc" value="4" required></li>
                <li><input ng-click="survey.ifc = '5'" type="radio" name="ifc" value="5" required><br/>Strongly agree</li>
            </ul>
        </div>
            <div class="minor-section">
                <p>
                    The information in the message I just read was important.
                </p>
                <ul class="likert">
                    <li><input ng-click="survey.if5 = '1'" type="radio" name="if5" value="1" required><br/>Strongly disagree</li>
                    <li><input ng-click="survey.if5 = '2'" type="radio" name="if5" value="2" required></li>
                    <li><input ng-click="survey.if5 = '3'" type="radio" name="if5" value="3" required><br/>Neither agree nor disagree
                    </li>
                    <li><input ng-click="survey.if5 = '4'" type="radio" name="if5" value="4" required></li>
                    <li><input ng-click="survey.if5 = '5'" type="radio" name="if5" value="5" required><br/>Strongly agree</li>
                </ul>
            </div>
            <div class="minor-section">
                <p>
                    The information in the message I just read made me think.
                </p>
                <ul class="likert">
                    <li><input ng-click="survey.if6 = '1'" type="radio" name="if6" value="1" required><br/>Strongly disagree</li>
                    <li><input ng-click="survey.if6 = '2'" type="radio" name="if6" value="2" required></li>
                    <li><input ng-click="survey.if6 = '3'" type="radio" name="if6" value="3" required><br/>Neither agree nor disagree
                    </li>
                    <li><input ng-click="survey.if6 = '4'" type="radio" name="if6" value="4" required></li>
                    <li><input ng-click="survey.if6 = '5'" type="radio" name="if6" value="5" required><br/>Strongly agree</li>
                </ul>
            </div>

            <h2>Please indicate how much you agree with the following statements about your interest in sharing the story you
            just read.</h2>

            <div class="minor-section">
                <p>
                    If I could, I would share this message with close friends and family members.
                </p>
                <ul class="likert">
                    <li><input ng-click="survey.sh1 = '1'" type="radio" name="sh1" value="1" required><br/>Strongly disagree</li>
                    <li><input ng-click="survey.sh1 = '2'" type="radio" name="sh1" value="2" required></li>
                    <li><input ng-click="survey.sh1 = '3'" type="radio" name="sh1" value="3" required><br/>Neither agree nor disagree
                    </li>
                    <li><input ng-click="survey.sh1 = '4'" type="radio" name="sh1" value="4" required></li>
                    <li><input ng-click="survey.sh1 = '5'" type="radio" name="sh1" value="5" required><br/>Strongly agree</li>
                </ul>
            </div>
            <div class="minor-section">
                <p>
                    If I could, I would share the message I just read on social media (e.g. Facebook or Twitter) publicly.
                </p>
                <ul class="likert">
                    <li><input ng-click="survey.sh2 = '1'" type="radio" name="sh2" value="1" required><br/>Strongly disagree</li>
                    <li><input ng-click="survey.sh2 = '2'" type="radio" name="sh2" value="2" required></li>
                    <li><input ng-click="survey.sh2 = '3'" type="radio" name="sh2" value="3" required><br/>Neither agree nor disagree
                    </li>
                    <li><input ng-click="survey.sh2 = '4'" type="radio" name="sh2" value="4" required></li>
                    <li><input ng-click="survey.sh2 = '5'" type="radio" name="sh2" value="5" required><br/>Strongly agree</li>
                </ul>
            </div>
            <div class="minor-section">
                <p>
                    If I could, I would share the message I just read on social media but not publicly.
                </p>
                <ul class="likert">
                    <li><input ng-click="survey.sh3 = '1'" type="radio" name="sh3" value="1" required><br/>Strongly disagree</li>
                    <li><input ng-click="survey.sh3 = '2'" type="radio" name="sh3" value="2" required></li>
                    <li><input ng-click="survey.sh3 = '3'" type="radio" name="sh3" value="3" required><br/>Neither agree nor disagree
                    </li>
                    <li><input ng-click="survey.sh3 = '4'" type="radio" name="sh3" value="4" required></li>
                    <li><input ng-click="survey.sh3 = '5'" type="radio" name="sh3" value="5" required><br/>Strongly agree</li>
                </ul>
            </div>

            <h2>Please indicate how likely it is you will perform the following activities with the specified frequency.</h2>

            <g:each var="action" in="${actions}" status="status">
                <div class="minor-section">
                    <p>
                        How likely is it that in the next 7 days you will ${action} daily.
                    </p>
                    <ul class="likert">
                        <li><input ng-click="survey.action1_${status} = '1'" type="radio" name="action1_${status}"
                                   value="1" required><br/>Definitely will not
                        </li>
                        <li><input ng-click="survey.action1_${status} = '2'" type="radio" name="action1_${status}" value="2" required>
                        </li>
                        <li><input ng-click="survey.action1_${status} = '3'" type="radio" name="action1_${status}"
                                   value="3" required><br/>Uncertain
                        </li>
                        <li><input ng-click="survey.action1_${status} = '4'" type="radio" name="action1_${status}" value="4" required>
                        </li>
                        <li><input ng-click="survey.action1_${status} = '5'" type="radio" name="action1_${status}"
                                   value="5" required><br/>Definitely will
                        </li>
                    </ul>
                </div>

                <div class="minor-section">
                    <p>
                        How likely is it that in the next 7 days you will ${action} some days.
                    </p>
                    <ul class="likert">
                        <li><input ng-click="survey.action2_${status} = '1'" type="radio" name="action2_${status}"
                                   value="1" required><br/>Definitely will not
                        </li>
                        <li><input ng-click="survey.action2_${status} = '2'" type="radio" name="action2_${status}" value="2" required>
                        </li>
                        <li><input ng-click="survey.action2_${status} = '3'" type="radio" name="action2_${status}"
                                   value="3" required><br/>Uncertain
                        </li>
                        <li><input ng-click="survey.action2_${status} = '4'" type="radio" name="action2_${status}" value="4" required>
                        </li>
                        <li><input ng-click="survey.action2_${status} = '5'" type="radio" name="action2_${status}"
                                   value="5" required><br/>Definitely will
                        </li>
                    </ul>
                </div>

                <div class="minor-section">
                    <p>
                        How likely is it that in the next 7 days you will ${action} on one or more days.
                    </p>
                    <ul class="likert">
                        <li><input ng-click="survey.action3_${status} = '1'" type="radio" name="action3_${status}"
                                   value="1" required><br/>Definitely will not
                        </li>
                        <li><input ng-click="survey.action3_${status} = '2'" type="radio" name="action3_${status}" value="2" required>
                        </li>
                        <li><input ng-click="survey.action3_${status} = '3'" type="radio" name="action3_${status}"
                                   value="3" required><br/>Uncertain
                        </li>
                        <li><input ng-click="survey.action3_${status} = '4'" type="radio" name="action3_${status}" value="4" required>
                        </li>
                        <li><input ng-click="survey.action3_${status} = '5'" type="radio" name="action3_${status}"
                                   value="5" required><br/>Definitely will
                        </li>
                    </ul>
                </div>
            </g:each>


            <p>
                <button type="submit" class="button">Continue</button>
            </p>
        </form>

    </div>


    <div ng-show="stage === 6">
        <p>
            <b>Please read the following brief passage and answer the question as best as you can.</b>
        </p>
        <p>
            <%

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

    <div ng-show="stage === 7">
        <p>
        In the text box below, please retell the <b>initial</b> story (called "${narrative.root_narrative.title}") for the next person.
        </p>
        <p>
            Please tell the story as best as you can. If you copy the text, or it appears that you have put very little effort into recalling the story, this assigment may be <b>rejected</b>.
        </p>
        <form ng-submit="narrative='${narrative.id}';wf.advance();wf.submitStory()"
              class="gwurkignore">

            <textarea ng-model="story" name="story"></textarea><br>

            <p><button type="submit" class="button">Continue</button></p>

        </form>

    </div>

    <div ng-show="stage === 8">
        <p>Thank you for your participation in this study.  Clicking the button below will submit
        your answers and complete this HIT.</p>
        <form action="complete" method="POST">
            <input type="hidden" name="narrative" value="${narrative.id}">

            <g:submitButton name="Complete HIT" class="button">Complete HIT</g:submitButton>
        </form>



    </div>
</div>
</body>
</html>
