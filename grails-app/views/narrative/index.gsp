
<%@ page import="nardiff.mt.Narrative" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'narrative.label', default: 'Narrative')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-narrative" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-narrative" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="root_narrative_id" title="${message(code: 'narrative.root_narrative_id.label', default: 'Rootnarrativeid')}" />
					
						<th><g:message code="narrative.parent_narrative.label" default="Parentnarrative" /></th>
					
						<th><g:message code="narrative.completed_by.label" default="Completedby" /></th>
					
						<g:sortableColumn property="distractor_answer" title="${message(code: 'narrative.distractor_answer.label', default: 'Distractoranswer')}" />
					
						<g:sortableColumn property="time_reading" title="${message(code: 'narrative.time_reading.label', default: 'Timereading')}" />
					
						<g:sortableColumn property="time_distrator" title="${message(code: 'narrative.time_distrator.label', default: 'Timedistrator')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${narrativeInstanceList}" status="i" var="narrativeInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${narrativeInstance.id}">${fieldValue(bean: narrativeInstance, field: "root_narrative_id")}</g:link></td>
					
						<td>${fieldValue(bean: narrativeInstance, field: "parent_narrative")}</td>
					
						<td>${fieldValue(bean: narrativeInstance, field: "completed_by")}</td>
					
						<td>${fieldValue(bean: narrativeInstance, field: "distractor_answer")}</td>
					
						<td>${fieldValue(bean: narrativeInstance, field: "time_reading")}</td>
					
						<td>${fieldValue(bean: narrativeInstance, field: "time_distrator")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${narrativeInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
