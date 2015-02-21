<%@ page import="nardiff.mt.Narrative" %>



<div class="fieldcontain ${hasErrors(bean: narrativeInstance, field: 'root_narrative_id', 'error')} ">
	<label for="root_narrative_id">
		<g:message code="narrative.root_narrative_id.label" default="Rootnarrativeid" />
		
	</label>
	<g:field name="root_narrative_id" type="number" value="${narrativeInstance.root_narrative_id}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: narrativeInstance, field: 'parent_narrative', 'error')} ">
	<label for="parent_narrative">
		<g:message code="narrative.parent_narrative.label" default="Parentnarrative" />
		
	</label>
	<g:select id="parent_narrative" name="parent_narrative.id" from="${nardiff.mt.Narrative.list()}" optionKey="id" value="${narrativeInstance?.parent_narrative?.id}" class="many-to-one" noSelection="['null': '']"/>

</div>

<div class="fieldcontain ${hasErrors(bean: narrativeInstance, field: 'completed_by', 'error')} ">
	<label for="completed_by">
		<g:message code="narrative.completed_by.label" default="Completedby" />
		
	</label>
	<g:select id="completed_by" name="completed_by.id" from="${nardiff.mt.Turker.list()}" optionKey="id" value="${narrativeInstance?.completed_by?.id}" class="many-to-one" noSelection="['null': '']"/>

</div>

<div class="fieldcontain ${hasErrors(bean: narrativeInstance, field: 'distractor_answer', 'error')} ">
	<label for="distractor_answer">
		<g:message code="narrative.distractor_answer.label" default="Distractoranswer" />
		
	</label>
	<g:textField name="distractor_answer" value="${narrativeInstance?.distractor_answer}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: narrativeInstance, field: 'time_reading', 'error')} ">
	<label for="time_reading">
		<g:message code="narrative.time_reading.label" default="Timereading" />
		
	</label>
	<g:field name="time_reading" type="number" value="${narrativeInstance.time_reading}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: narrativeInstance, field: 'time_distrator', 'error')} ">
	<label for="time_distrator">
		<g:message code="narrative.time_distrator.label" default="Timedistrator" />
		
	</label>
	<g:field name="time_distrator" type="number" value="${narrativeInstance.time_distrator}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: narrativeInstance, field: 'time_writing', 'error')} ">
	<label for="time_writing">
		<g:message code="narrative.time_writing.label" default="Timewriting" />
		
	</label>
	<g:field name="time_writing" type="number" value="${narrativeInstance.time_writing}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: narrativeInstance, field: 'text', 'error')} required">
	<label for="text">
		<g:message code="narrative.text.label" default="Text" />
		<span class="required-indicator">*</span>
	</label>
	<g:textArea name="text" required="" value="${narrativeInstance?.text}"/>

</div>

