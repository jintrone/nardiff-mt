<%--
  Created by IntelliJ IDEA.
  User: kkoning
  Date: 2/19/15
  Time: 6:05 PM
--%>

<%@ page import="nardiff.mt.NarrativeRequest; nardiff.mt.NardiffStuff" contentType="text/html;charset=UTF-8" %>
<html ng-app="nardiff">
<head>
    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.13/angular.js"></script>
    <script type="text/javascript" src="js/nardiff.js"></script>
    <title></title>

    <style>
    #mainPanel {
        margin-left: auto;
        margin-right: auto;
        width: 500px;
    }

    img.center {
        display: block;
        margin-left: auto;
        margin-right: auto;
    }

    textarea {
        width: 100%;
        height: 200;
    }
    </style>
</head>

<body>
<div ng-controller="WorkflowController as wf" id="mainPanel">
    <img class="center" src="images/wordmark_green_RGB.png"/>
    <%
        NarrativeRequest nr = NardiffStuff.findRequest(Long.parseLong(request.getParameter("story_id")));
        boolean askForDemographics = NardiffStuff.assignRequestToTurker(nr,request.getParameter("turker_id"));
        out.println(nr.toString());
    %>
    <timer/>

    <div ng-show="stage === 1">
        <p><b>Research Disclosure.</b> The task below is part of a research study
        conducted by a team from the Media and Information department at Michigan
        State University. This purpose of this study is to better understand certain
        aspects of how people communicate. No sensitive information will be collected,
        the results will be kept confidential, and after the HIT is marked complete
        the data linking your responses to MTurk identifiers will be destroyed.  If
        you have any questions, you can contact
            <a href="http://cas.msu.edu/people/faculty-staff/staff-listing/name/joshua-introne/">Dr. Joshua Introne</a>
            (<a href="mailto:jintrone@msu.edu">email</a>) or the
            <a href="">MSU Institutional Review Board</a>
            (<a href="mailto:irb@msu.edu">email</a>).
        </p>

        <p>By accepting this HIT, you indicate your voluntary agreement to
        participate in this study.</p>

        <p><b>Instructions.</b>  When you press the “begin” button below, you will
        be given 2 minutes to read a very short story. Afterward, you will be asked to
        retell as much of the story as possible in your own words for the
        next person. Do not copy/paste; any assignment that is copied / pasted will be
        rejected.
        </p>

        <p>
            <button ng-click="stage = 2; wf.startTimer(); wf.request_id = <%=
                nr.id
            %>; ">Begin</button>
        </p>
    </div>

    <div ng-show="stage === 2" id="img-div">
        <p><b>Instructions.</b>  Please spend the next 2 minutes reading the story below.
        Afterward, you will be asked to retell as much of the story as possible in your own
        words for the next person. Do not copy/paste; any assignment that is copied / pasted
        will be rejected.
        </p>

        <p id="toremove"><img src="images/narratives/<%
            NarrativeRequest.withTransaction { tx ->
                nr.attach();
                out.print(nr.parent_narrative.id)
            } %>.png"/></p>

        <p>Time Remaining: {{ timeRemaining + " seconds" }}</p>
    </div>

    <div ng-show="stage === 3">
        <p>Before you retell the story, please answer the following short question:</p>

        <p>The Walcheren Campaign was an unsuccessful British expedition to the
        Netherlands in 1809 intended to open another front in the Austrian Empire's
        struggle with France during the War of the Fifth Coalition. Around 40,000
        soldiers, 15,000 horses together with field artillery and two siege trains
        crossed the North Sea and landed at Walcheren on 30 July.  The primary
        aim of the campaign was to destroy the French fleet thought to be in
        Flushing whilst providing a diversion for the hard-pressed Austrians.
        </p>

        <p>
            Which country conducted the Walcheren Campaign, and what was its aim?
        </p>

        <form>
            <input ng-click="wf.distractorAnswer = 'A'; stage = 4" type="radio"
                   name="dt">France, to attack the Austrian ground forces<br/>
            <input ng-click="wf.distractorAnswer = 'B'; stage = 4" type="radio"
                   name="dt">Britain, to destroy the French naval fleet<br/>
            <input ng-click="wf.distractorAnswer = 'C'; stage = 4" type="radio"
                   name="dt">Austria, to attack the French naval fleet<br/>
            <input ng-click="wf.distractorAnswer = 'D'; stage = 4" type="radio"
                   name="dt">Britain, to destroy Dutch ground forces<br/>
        </form>
    </div>

    <div ng-show="stage === 4">
        In the text box below, please retell the story for the next person.
        <form>
            <textarea ng-model="wf.story" name="story"></textarea>
            <button ng-click="stage = 5">Continue</button>
        </form>
    </div>

    <div ng-show="stage === 5">
        <p>Finally, please give us some basic demographic information about yourself</p>

        <p>Age: <input name="age" ng-model="wf.age"></p>

        <p>Sex: <input ng-click="wf.gender = 'M'" type="radio" name="gender"> Male,
            <input ng-click="wf.gender = 'F'" type="radio" name="gender"> Female,
            <input ng-click="wf.gender = 'O'" type="radio" name="gender"> Other</p>

        <p>Education:</p>

        <form>
            <input ng-click="wf.education = '1'" type="radio" name="edu">Less than High School<br/>
            <input ng-click="wf.education = '2'" type="radio" name="edu">High School<br/>
            <input ng-click="wf.education = '3'" type="radio" name="edu">Some College<br/>
            <input ng-click="wf.education = '4'" type="radio" name="edu">Associate Degree<br/>
            <input ng-click="wf.education = '5'" type="radio" name="edu">Bachelors Degree<br/>
            <input ng-click="wf.education = '6'" type="radio" name="edu">Masters Degree<br/>
            <input ng-click="wf.education = '7'" type="radio" name="edu">Graduate/Professional Degree<br/>
        </form>

        <p><button ng-click="stage = 6">Continue</button></p>

    </div>

    <div ng-show="stage === 6">
        <p>Thank you for your participation in this study.  You can complete this task up to 6 times.</p>

    </div>

</div>
</body>
</html>
