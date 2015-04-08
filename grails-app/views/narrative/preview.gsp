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

    <img class="center" src="../images/wordmark_green_RGB.png"/>


    <div ng-show="stage === 1">
        <p>(Task Preview)</p>

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
        be asked for some basic demographic information and then
        be given 2 minutes to read a very short story. Afterward, you will be asked to
        retell as much of the story as possible in your own words for the
        next person. Do not copy/paste; any assignment that is copied / pasted will be
        rejected.
        </p>

        <p>
            <button>Begin</button>
        </p>
    </div>


    </div>
</div>
</body>
</html>
