<%--
  Created by IntelliJ IDEA.
  User: kkoning
  Date: 2/25/15
  Time: 1:24 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title></title>
</head>
<body>
<form action="test.gsp" method="PUT">
    Enter starting information:<br/>
    (Simulated) Turker ID: <input name="turker_id"><br/>
    Story to Do: (check db for ID first) <input name="story_id"><br/>
    <g:submitButton name="Start">Start</g:submitButton>
</form>

</body>
</html>