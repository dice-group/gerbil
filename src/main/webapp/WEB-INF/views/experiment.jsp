<%@page import="org.aksw.gerbil.web.ExperimentTaskStateHelper"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<head>
<link rel="stylesheet"
	href="/gerbil/webjars/bootstrap/3.2.0/css/bootstrap.min.css">
<link rel="stylesheet"
	href="/gerbil/webjars/bootstrap-multiselect/0.9.8/css/bootstrap-multiselect.css" />
<link rel="icon" type="image/png"
	href="/gerbil/webResources/gerbilicon_transparent.png">
<style type="text/css">
/* making the buttons wide enough and right-aligned */
.btn-group>.btn {
	float: none;
	width: 100%;
	text-align: right;
}

.btn-group {
	width: 100%;
}

#type {
	text-align: right;
}

#type>option {
	text-align: right;
}
.gerbil-experiment-warn {
	color: red;
}
.gerbil-center-align {
	text-align: center;
}
</style>

</head>
<body class="container">
	<!-- mappings to URLs in back-end controller -->
	<c:url var="experiment" value="/experiment" />

	<script src="/gerbil/webjars/jquery/2.1.1/jquery.min.js"></script>
	<script src="/gerbil/webjars/bootstrap/3.2.0/js/bootstrap.min.js"></script>
	<script
		src="/gerbil/webjars/tablesorter/2.15.5/js/jquery.tablesorter.js"></script>
		
	<script type="application/ld+json">
	${dataid}
	</script>
	
	<%@include file="navbar.jsp"%>
	<h1>GERBIL Experiment</h1>
	<c:if test="${not empty tasks}">
		<c:set var="hasSubTasks" value="false" />
		<c:forEach var="task" items="${tasks}">

			<c:if test="${task.numberOfSubTasks > 0}">
				<c:set var="hasSubTasks" value="true" />
			</c:if>
		</c:forEach>
		<c:choose>
			<c:when test="${isRunning && precedingTaskCount > workers}">
				<span class="gerbil-experiment-warn"> Experiments could take a while <br> <c:out
						value="There are ${precedingTaskCount} other tasks pending before your latest task on ${workers} Worker(s)." />
				</span>
			</c:when>
			<c:when test="${!isRunning}">
    			Your Experiments are completed			
    		</c:when>
			<c:otherwise>
			</c:otherwise>
		</c:choose>
		<br>
		<br>
	Experiment URI: <span id="experimentUri"></span>
		<br>
	Type: <c:out value="${tasks[0].type.label}" />
		<br>
		<table id="resultTable"
			class="table  table-hover table-condensed tableScroll">
			<!--<table id="resultTable"
			class="table  table-hover table-condensed tablesorter tableScroll">-->
			<thead>
				<tr>
					<th>System</th>
					<th>Reference</th>
					<c:if test="${hasSubTasks}">
						<th></th>
					</c:if>
					<c:forEach var="resName" items="${resultNames}">
						<th><c:out value="${resName}" /></th>
					</c:forEach>
					<th>Timestamp</th>
					<th>GERBIL version</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="task" varStatus="taskId" items="${tasks}">
					<c:if test="${empty task.stateMsg}">
						<tr>
							<td><c:out value="${task.annotator}" /></td>
							<td><c:out value="${task.dataset}" /></td>
							<c:if test="${hasSubTasks}">
								<td></td>
							</c:if>
							<c:forEach var="resName" items="${resultNames}">
								<td> <c:choose>
										<c:when test="${task.resultsMap.get(resName).getResValue()!=null}">
									        <fmt:formatNumber type="number" maxFractionDigits="4"
										value="${task.resultsMap.get(resName).getResValue()}" />
										</c:when>
										<c:otherwise> </c:otherwise>
									</c:choose></td>
							</c:forEach>
							<td><c:out value="${task.timestampstring}" /></td>
							<td><c:out value="${task.version}" /></td>
						</tr>
						<c:forEach var="subTask" items="${task.subTasks}">
							<tr>
								<td><c:out value="${subTask.annotator}" /></td>
								<td><c:out value="${subTask.dataset}" /></td>
								<td><c:out value="${subTask.type.label}" /></td>
								<c:forEach var="resName" items="${resultNames}">
									<td><c:choose>
										<c:when test="${subTask.resultsMap.get(resName).getResValue()!=null}">
									        <fmt:formatNumber type="number" maxFractionDigits="4"
										value="${subTask.resultsMap.get(resName).getResValue()}" />
										</c:when>
										<c:otherwise> </c:otherwise>
									</c:choose></td>
								</c:forEach>
								<td><c:out value="${subTask.timestampstring}" /></td>
								<td><c:out value="${subTask.version}" /></td>
							</tr>
						</c:forEach>
					</c:if>
					<c:if test="${!empty task.stateMsg}">
						<tr>
							<td><c:out value="${task.annotator}" /></td>
							<td><c:out value="${task.dataset}" /></td>
							<c:if test="${hasSubTasks}">
								<td></td>
							</c:if>
							<td colspan="${fn:escapeXml(resultNames.size())}"
								class="gerbil-center-align"><c:out value="${task.stateMsg}" /></td>
							<td><c:out value="${task.timestampstring}" /></td>
							<td><c:out value="${task.version}" /></td>
						</tr>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
	</c:if>



	<script type="text/javascript">
		$(document)
				.ready(
						function() {
							$("#resultTable").tablesorter({
								sortList : [ [ 0, 0 ], [ 1, 0 ] ]
							});

							// print the URI of the experiment
							var origin = window.location.origin;
							var experimentId = window.location.search;
							var content = "<a href=\"/gerbil/experiment"
				+ experimentId
				+ "\">"
									+ origin
									+ "/gerbil/experiment"
									+ experimentId + "</a>";
							// If this is the AKSW instance of GERBIL
							if (origin == "https://beng.dice-research.org") {
								content += " and <a href=\"http://w3id.org/gerbil/nlg/experiment"
					+ experimentId
					+ "\">http://w3id.org/gerbil/experiment"
										+ experimentId + "</a>";
							}
							// If this is the AKSW instance of GERBIL QA
							if (origin == "http://gerbil-qa.aksw.org") {
								content += " and <a href=\"http://w3id.org/gerbil/qa/experiment"
					+ experimentId
					+ "\">http://w3id.org/gerbil/qa/experiment"
										+ experimentId + "</a>";
							}
							$("#experimentUri").html(content);
						});
	</script>
</body>
