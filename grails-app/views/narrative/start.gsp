<%@ page import="nardiff.mt.NarrativeRequest; nardiff.mt.NardiffStuff" contentType="text/html;charset=UTF-8" %>
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
    </style>
</head>

<body>
<div ng-controller="WorkflowController as wf" id="mainPanel">

    <%
        NarrativeRequest nr = params.get("narrativeRequest")
        boolean askForDemographics = params.get("askForDemographics")
        int beginStage = 2;
        if (!askForDemographics)
            beginStage = 3;

    %>
    <timer></timer>

    <div ng-show="stage === 1">

        <p><b>Instructions.</b>  When you click the 'begin' button, you will
        <% if (askForDemographics) out.println("be asked for some basic demographic information and then ") %>
        be given 2 minutes to read a very short story. Afterward, you will be asked to
        retell as much of the story as possible in your own words for the
        next person. Do not copy/paste; any assignment that is copied / pasted will be
        rejected.
        </p>

        <p>
            <button class="button" ng-click="stage = <%=beginStage%>; wf.request_id = <%=
                nr.id
            %>; ">Begin</button>
        </p>
    </div>


    <div ng-show="stage === 2">
        <p>To start with, we need a small amount of demographic information.</p>

        <form ng-submit="wf.submitDemographics()" class="gwurkignore">
            <p>Year of Birth:

                <select id="age" name="age" ng-model="wf.demographics.age">
                    <option value="1998">1998</option>
                    <option value="1997">1997</option>
                    <option value="1996">1996</option>
                    <option value="1995">1995</option>
                    <option value="1994">1994</option>
                    <option value="1993">1993</option>
                    <option value="1992">1992</option>
                    <option value="1991">1991</option>
                    <option value="1990">1990</option>
                    <option value="1989">1989</option>
                    <option value="1988">1988</option>
                    <option value="1987">1987</option>
                    <option value="1986">1986</option>
                    <option value="1985">1985</option>
                    <option value="1984">1984</option>
                    <option value="1983">1983</option>
                    <option value="1982">1982</option>
                    <option value="1981">1981</option>
                    <option value="1980">1980</option>
                    <option value="1979">1979</option>
                    <option value="1978">1978</option>
                    <option value="1977">1977</option>
                    <option value="1976">1976</option>
                    <option value="1975">1975</option>
                    <option value="1974">1974</option>
                    <option value="1973">1973</option>
                    <option value="1972">1972</option>
                    <option value="1971">1971</option>
                    <option value="1970">1970</option>
                    <option value="1969">1969</option>
                    <option value="1968">1968</option>
                    <option value="1967">1967</option>
                    <option value="1966">1966</option>
                    <option value="1965">1965</option>
                    <option value="1964">1964</option>
                    <option value="1963">1963</option>
                    <option value="1962">1962</option>
                    <option value="1961">1961</option>
                    <option value="1960">1960</option>
                    <option value="1959">1959</option>
                    <option value="1958">1958</option>
                    <option value="1957">1957</option>
                    <option value="1956">1956</option>
                    <option value="1955">1955</option>
                    <option value="1954">1954</option>
                    <option value="1953">1953</option>
                    <option value="1952">1952</option>
                    <option value="1951">1951</option>
                    <option value="1950">1950</option>
                    <option value="1949">1949</option>
                    <option value="1948">1948</option>
                    <option value="1947">1947</option>
                    <option value="1946">1946</option>
                    <option value="1945">1945</option>
                    <option value="1944">1944</option>
                    <option value="1943">1943</option>
                    <option value="1942">1942</option>
                    <option value="1941">1941</option>
                    <option value="1940">1940</option>
                    <option value="1939">1939</option>
                    <option value="1938">1938</option>
                    <option value="1937">1937</option>
                    <option value="1936">1936</option>
                    <option value="1935">1935</option>
                    <option value="1934">1934</option>
                    <option value="1933">1933</option>
                    <option value="1932">1932</option>
                    <option value="1931">1931</option>
                    <option value="1930">1930</option>
                    <option value="1929">1929</option>
                    <option value="1928">1928</option>
                    <option value="1927">1927</option>
                    <option value="1926">1926</option>
                    <option value="1925">1925</option>
                    <option value="1924">1924</option>
                    <option value="1923">1923</option>
                    <option value="1922">1922</option>
                    <option value="1921">1921</option>
                    <option value="1920">1920</option>
                    <option value="1919">1919</option>
                    <option value="1918">1918</option>
                    <option value="1917">1917</option>
                    <option value="1916">1916</option>
                    <option value="1915">1915</option>
                    <option value="1914">1914</option>
                    <option value="1913">1913</option>
                    <option value="1912">1912</option>
                    <option value="1911">1911</option>
                    <option value="1910">1910</option>
                    <option value="1909">1909</option>
                    <option value="1908">1908</option>
                    <option value="1907">1907</option>
                    <option value="1906">1906</option>
                    <option value="1905">1905</option>
                </select>
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

        <p id="toremove"><img src="../images/narratives/<%
            NarrativeRequest.withTransaction { tx ->
                nr.attach();
                out.print(nr.parent_narrative.id)
            } %>.png"/></p>

        <p>Time Remaining: {{ timeRemaining + " seconds" }}</p>

        <p><button class="button" ng-click="wf.st(wf); stage = 5">Skip Remaining Time</button></p>
    </div>

    <div ng-show="stage === 5">
        <%-- f-ing grails doesn't have a simple server side include?  you've got to be kidding --%>
        <% if (nr.parent_narrativeId == 1) { %>
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
            <input ng-click="wf.distractorAnswer = 'A'; wf.dt(wf); stage = 6" type="radio"
                   name="dt">France, to attack the Austrian ground forces<br/>
            <input ng-click="wf.distractorAnswer = 'B'; wf.dt(wf); stage = 6" type="radio"
                   name="dt">Britain, to destroy the French naval fleet<br/>
            <input ng-click="wf.distractorAnswer = 'C'; wf.dt(wf); stage = 6" type="radio"
                   name="dt">Austria, to attack the French naval fleet<br/>
            <input ng-click="wf.distractorAnswer = 'D'; wf.dt(wf); stage = 6" type="radio"
                   name="dt">Britain, to destroy Dutch ground forces<br/>
        </form>

        <% } else if (nr.parent_narrativeId == 2) { %>
        <p>Before you retell the story, please answer the following short question:</p>

        <p>"Mumbai (also known as Bombay) is the capital city of the Indian state of Maharashtra.
        It is the most populous city in India, most populous metropolitan area in India, and
        the eighth most populous city in the world, with an estimated city population of 18.4
        million and metropolitan area population of 20.7 million as of 2011. Along with the
        urban areas, including the cities of Navi Mumbai, Thane, Bhiwandi, Kalyan, it is one
        of the most populous urban regions in the world. Mumbai lies on the west coast of
        India and has a deep natural harbour. In 2009, Mumbai was named an alpha world
        city. It is also the wealthiest city in India, and has the highest GDP of any city
        in South, West, or Central Asia. Mumbai has the highest number of billionaires and
        millionaires among all cities in India.”  (Wikipedia, on Mumbai)
        </p>

        <p>
            Based on the passage above, which of the following is true about the city of Mumbai?
        </p>

        <form>
            <input ng-click="wf.distractorAnswer = 'A'; wf.dt(wf); stage = 6" type="radio"
                   name="dt">It is the most populous city in the world<br/>
            <input ng-click="wf.distractorAnswer = 'B'; wf.dt(wf); stage = 6" type="radio"
                   name="dt">It is the capital city of India<br/>
            <input ng-click="wf.distractorAnswer = 'C'; wf.dt(wf); stage = 6" type="radio"
                   name="dt">It is on located on the east coast of India<br/>
            <input ng-click="wf.distractorAnswer = 'D'; wf.dt(wf); stage = 6" type="radio"
                   name="dt">It has a deep natural harbor<br/>
        </form>

        <% } else if (nr.parent_narrativeId == 3) { %>
        <p>Before you retell the story, please answer the following short question:</p>

        <p>"Aldwych tube station is a closed station on the London Underground, in the City of
        Westminster in central London. The station building, near the junction of Strand and
        Surrey Street, opened in 1907 as the only other station on the Piccadilly line's short
        branch from Holborn. Suffering from low passenger numbers, the station and branch were
        considered for closure several times. Service was offered only during weekday peak
        hours from 1962 and finally discontinued altogether in 1994, when the cost of replacing
        the lifts was considered too high compared to the income generated. Disused parts of
        the station and the running tunnels were used to shelter artworks from the National
        Gallery during the First World War, and from the British Museum during the Second
        World War.”  (Wikipedia, on the Aldwych tube station)
        </p>

        <p>
            Based on the passage above, what is the busiest time of day for the Aldwych tube station?
        </p>

        <form>
            <input ng-click="wf.distractorAnswer = 'A'; wf.dt(wf); stage = 6" type="radio"
                   name="dt">Weekend Evenings<br/>
            <input ng-click="wf.distractorAnswer = 'B'; wf.dt(wf); stage = 6" type="radio"
                   name="dt">Weekday Mornings<br/>
            <input ng-click="wf.distractorAnswer = 'C'; wf.dt(wf); stage = 6" type="radio"
                   name="dt">Weekday Evenings<br/>
            <input ng-click="wf.distractorAnswer = 'D'; wf.dt(wf); stage = 6" type="radio"
                   name="dt">None, the station is now closed.<br/>
        </form>

        <% } else if (nr.parent_narrativeId == 4) { %>
        <p>Before you retell the story, please answer the following short question:</p>

        <p>"The American paddlefish is one of the largest freshwater fish in North America. Closely
        related to the sturgeon, it is one of only two extant taxa in the paddlefish family
        (the other being the Chinese paddlefish). The rostrum and cranium of American
        paddlefish are covered with tens of thousands of sensory receptors for locating
        swarms of zooplankton, their primary food source. American paddlefish are native
        to the Mississippi River Basin and once moved freely under the relatively natural,
        unaltered conditions that existed prior to the early 1900s. Their numbers have declined
        dramatically primarily because of overfishing, habitat destruction, and pollution.
        They are currently found in only twenty-two U.S. states, and their range has been
        reduced to the Mississippi and Missouri river tributaries and Mobile Bay drainage
        basin.”  (Wikipedia, on the American paddlefish)
        </p>

        <p>
            Based on the passage above, what is the purpose of the sensory receptors on the cranium and
            rostrum of the American paddlefish?
        </p>

        <form>
            <input ng-click="wf.distractorAnswer = 'A'; wf.dt(wf); stage = 6" type="radio"
                   name="dt">To find food<br/>
            <input ng-click="wf.distractorAnswer = 'B'; wf.dt(wf); stage = 6" type="radio"
                   name="dt">To find other paddlefish<br/>
            <input ng-click="wf.distractorAnswer = 'C'; wf.dt(wf); stage = 6" type="radio"
                   name="dt">To navigate rivers<br/>
            <input ng-click="wf.distractorAnswer = 'D'; wf.dt(wf); stage = 6" type="radio"
                   name="dt">None of the above<br/>
        </form>

        <% } else { %>
        <p>Before you retell the story, please answer the following short question:</p>

        <p>"The Colorado River is the principal river of the Southwestern United States and northwest
        Mexico.  Known for its dramatic scenery and its whitewater, the Colorado carves numerous
        gorges, including the Grand Canyon in northern Arizona. For 8,000 years, the Colorado
        Basin was only sparsely populated by Native Americans, though some of their ancient
        civilizations employed advanced irrigation techniques. Even after becoming part of the
        U.S. in the 1800s, the Colorado River country remained extremely remote until John Wesley
        Powell's 1869 river-running expedition, which began to open up the river for future
        development. Since the completion of Hoover Dam in 1935, the Colorado has been tamed by
        an extensive system of dams and canals, providing for irrigation, cities, and hydropower.
        Today the Colorado supports 40 million people in seven U.S. and two Mexican states; with
        every drop of its water allocated, it no longer reaches the sea except in years of heavy
        runoff.”  (Wikipedia, on the Colorado River)
        </p>

        <p>
            Based on the passage above, which of the following are associated with the Colorado River?
        </p>

        <form>
            <input ng-click="wf.distractorAnswer = 'A'; wf.dt(wf); stage = 6" type="radio"
                   name="dt">John Wesley Powell<br/>
            <input ng-click="wf.distractorAnswer = 'B'; wf.dt(wf); stage = 6" type="radio"
                   name="dt">The Grand Canyon<br/>
            <input ng-click="wf.distractorAnswer = 'C'; wf.dt(wf); stage = 6" type="radio"
                   name="dt">The Hoover Dam<br/>
            <input ng-click="wf.distractorAnswer = 'D'; wf.dt(wf); stage = 6" type="radio"
                   name="dt">All of the Above<br/>
        </form>

        <% } %>

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
