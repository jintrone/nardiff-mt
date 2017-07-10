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
        width: 900px;

        font-size: 125%;
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

    .button {
        font-size: 125%;
    }
    </style>
</head>

<body>

<div ng-controller="WorkflowController as wf" id="mainPanel">

    <div ng-show="stage === 1">
        <p>This HIT is part of a research study. You will be given 1 minute to read a brief story, and then
        write what you can remember of the story for someone else to read.  You will be asked to fill in a brief survey, and then an unrelated question between
        reading the story and writing it down.</p>

        <h2>Please note:<br/>
            <em>We have implemented a programmatic restriction that will
            only allow you to complete ${storycount} HIT${storycount>1?"s":""} from this batch.  If you attempt to do more than this many HITs, you will be notified
            and asked to return the HIT. This will not affect your reputation.</em>
        </h2>


        <p>We estimate that the whole task should take you less than 6 min (based on trial runs).  Based on this estimate, the pay rate for this task is about $9.00 / hr.</p>

        <p><b>Instructions.</b>  When you press the “begin” button below, you will
        be asked for some basic demographic information and then
        be given 1 minute to read a very short story. Afterward, you will be asked to
        retell as much of the story as possible in your own words for the
        next person. Do not try to copy the story, because we seek to understand how people remember stories.
        Any assignment that appears to be a simple copy will be
        rejected.
        </p>

        <p>


        <p><b>Research Disclosure.</b> The task below is part of a research study
        conducted by a team from the Media and Information department at Michigan
        State University. This purpose of this study is to better understand certain
        aspects of how people communicate. No sensitive information will be collected,
        the results will be kept confidential, and after the HIT is marked complete
        the data linking your responses to MTurk identifiers will be destroyed.  If
        you have any questions, you can contact
            <a href="http://cas.msu.edu/people/faculty-staff/staff-listing/name/joshua-introne/"
               target="_blank">Dr. Joshua Introne</a>
            (<a href="mailto:jintrone@msu.edu" target="_blank">email</a>) or the
            <a href="http://hrpp.msu.edu/" target="_blank">MSU Institutional Review Board</a>
            (<a href="mailto:irb@msu.edu" target="_blank">email</a>).
        </p>

        <p>By accepting this HIT, you indicate your voluntary agreement to
        participate in this study.</p>


    </p>

        <p>
            <button>Begin</button>
        </p>
    </div>

</div>
</div>
</body>
</html>
