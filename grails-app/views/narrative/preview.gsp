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
        <p>This HIT is part of a research study. You will be given two minutes to read a brief story, and then
        write what you can remember of the story for someone else to read. An example of a story is:</p>

        <p>A GOATHERD, driving his flock from their pasture at eventide, found some
        Wild Goats mingled among them, and shut them up together with his own
        for the night. The next day it snowed very hard, so that he could not
        take the herd to their usual feeding places, but was obliged to keep
        them in the fold. He gave his own goats just sufficient food to keep
        them alive, but fed the strangers more abundantly in the hope of
        enticing them to stay with him and of making them his own. When the thaw
        set in, he led them all out to feed, and the Wild Goats scampered away
        as fast as they could to the mountains. The Goatherd scolded them for
        their ingratitude in leaving him, when during the storm he had taken
        more care of them than of his own herd. One of them, turning about,
        said to him: "That is the very reason why we are so cautious; for if you
        yesterday treated us better than the Goats you have had so long, it is
        plain also that if others came after us, you would in the same manner
        prefer them to ourselves."  (from Aesop's Fables)</p>

        <p><b>Instructions.</b>  When you press the “begin” button below, you will
        be asked for some basic demographic information and then
        be given 2 minutes to read a very short story. Afterward, you will be asked to
        retell as much of the story as possible in your own words for the
        next person. Do not copy/paste; any assignment that is copied / pasted will be
        rejected.

        <p><b>Research Disclosure.</b> The task below is part of a research study
        conducted by a team from the Media and Information department at Michigan
        State University. This purpose of this study is to better understand certain
        aspects of how people communicate. No sensitive information will be collected,
        the results will be kept confidential, and after the HIT is marked complete
        the data linking your responses to MTurk identifiers will be destroyed.  If
        you have any questions, you can contact
            <a href="http://cas.msu.edu/people/faculty-staff/staff-listing/name/joshua-introne/" target="_blank">Dr. Joshua Introne</a>
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
