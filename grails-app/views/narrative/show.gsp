
<%@ page import="nardiff.mt.Narrative" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'narrative.label', default: 'Narrative')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-narrative" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-narrative" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list narrative">
			
				<g:if test="${narrativeInstance?.root_narrative_id}">
				<li class="fieldcontain">
					<span id="root_narrative_id-label" class="property-label"><g:message code="narrative.root_narrative_id.label" default="Rootnarrativeid" /></span>
					
						<span class="property-value" aria-labelledby="root_narrative_id-label"><g:fieldValue bean="${narrativeInstance}" field="root_narrative_id"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${narrativeInstance?.parent_narrative}">
				<li class="fieldcontain">
					<span id="parent_narrative-label" class="property-label"><g:message code="narrative.parent_narrative.label" default="Parentnarrative" /></span>
					
						<span class="property-value" aria-labelledby="parent_narrative-label"><g:link controller="narrative" action="show" id="${narrativeInstance?.parent_narrative?.id}">${narrativeInstance?.parent_narrative?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${narrativeInstance?.completed_by}">
				<li class="fieldcontain">
					<span id="completed_by-label" class="property-label"><g:message code="narrative.completed_by.label" default="Completedby" /></span>
					
						<span class="property-value" aria-labelledby="completed_by-label"><g:link controller="turker" action="show" id="${narrativeInstance?.completed_by?.id}">${narrativeInstance?.completed_by?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${narrativeInstance?.distractor_answer}">
				<li class="fieldcontain">
					<span id="distractor_answer-label" class="property-label"><g:message code="narrative.distractor_answer.label" default="Distractoranswer" /></span>
					
						<span class="property-value" aria-labelledby="distractor_answer-label"><g:fieldValue bean="${narrativeInstance}" field="distractor_answer"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${narrativeInstance?.time_reading}">
				<li class="fieldcontain">
					<span id="time_reading-label" class="property-label"><g:message code="narrative.time_reading.label" default="Timereading" /></span>
					
						<span class="property-value" aria-labelledby="time_reading-label"><g:fieldValue bean="${narrativeInstance}" field="time_reading"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${narrativeInstance?.time_distrator}">
				<li class="fieldcontain">
					<span id="time_distrator-label" class="property-label"><g:message code="narrative.time_distrator.label" default="Timedistrator" /></span>
					
						<span class="property-value" aria-labelledby="time_distrator-label"><g:fieldValue bean="${narrativeInstance}" field="time_distrator"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${narrativeInstance?.time_writing}">
				<li class="fieldcontain">
					<span id="time_writing-label" class="property-label"><g:message code="narrative.time_writing.label" default="Timewriting" /></span>
					
						<span class="property-value" aria-labelledby="time_writing-label"><g:fieldValue bean="${narrativeInstance}" field="time_writing"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${narrativeInstance?.text}">
				<li class="fieldcontain">
					<span id="text-label" class="property-label"><g:message code="narrative.text.label" default="Text" /></span>
					
						<span class="property-value" aria-labelledby="text-label"><g:fieldValue bean="${narrativeInstance}" field="text"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:narrativeInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${narrativeInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
