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
<form action="narrative/start" method="POST">
    <input type="hidden" name="task" value="99999999"/>
    <input type="hidden" name="assignmentId" value="manual"/>

    Enter starting information:<br/>
    (Simulated) Turker ID: <input name="workerId"><br/>
    Story to Do: (check db for ID first) <input name="rootNarrativeId"><br/>
    <g:submitButton name="Start">Start</g:submitButton>
</form>

</body>
</html>