<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<h1>Gerbil Experiment Configuration</h1>


<c:url var="url" value="/config/execute" />
<form:form action="${url}">

	<fieldset>
		<div class="form-row">
			<form:select name="typechoice" path="type">
				<form:options items="${types}" />
			</form:select>
		</div>
		<div class="form-row">
			<form:select path="annotator">
				<c:if test="${typechoice =='News' }">
					<form:options items="${model.news}" />
				</c:if>
				<c:if test="${typechoice =='Products' }">
					<form:options items="${model.products}" />
				</c:if>

			</form:select>
		</div>
		<div class="form-buttons">
			<div class="button">
				<input name="submit" type="submit" value="Execute" />
			</div>
		</div>
	</fieldset>
</form:form>