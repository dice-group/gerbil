<%@page import="org.aksw.gerbil.web.ExperimentTaskStateHelper"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%
	request.setAttribute("additionalResultsCount",
			((String[]) request.getAttribute("additionalResultNames")).length);
%>
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
    		<c:when test="${workers<currentExperimentID && currentState!=0}" >
				<font color="red">
				Experiments could take a while
				<br>
				<c:out value="${currentExperimentID} Experiments before yours on ${workers} Worker"/>
				</font>
    		</c:when>
    		<c:when test="${currentState}==0" >
    			Your Experiments finished			
    		</c:when>
    		<c:otherwise>
    		</c:otherwise>
		</c:choose>

				<br>
	Experiment URI: <span id="experimentUri"></span>
		<br>
	Type: <c:out value="${tasks[0].type.label}" />
		<br>
	Matching: <c:out value="${tasks[0].matching.label}" />
		<table id="resultTable"
			class="table  table-hover table-condensed tableScroll">
			<!--<table id="resultTable"
			class="table  table-hover table-condensed tablesorter tableScroll">-->
			<thead>
				<tr>
					<th>Annotator</th>
					<th>Dataset</th>
					<c:if test="${hasSubTasks}">
						<th></th>
					</c:if>
					<th>Micro F1</th>
					<th>Micro Precision</th>
					<th>Micro Recall</th>
					<th>Macro F1</th>
					<th>Macro Precision</th>
					<th>Macro Recall</th>
					<!-- <th>State</th> -->
					<th>Error Count</th>
					<!-- for every additional result -->
					<c:forEach items="${additionalResultNames}" var="name">
						<th>${name}</th>
					</c:forEach>
					<th>Timestamp</th>
					<th>GERBIL version</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="task" varStatus="taskId" items="${tasks}">
					<c:if test="${empty task.stateMsg}">
						<tr>
							<td>${task.annotator}</td>
							<td>${task.dataset}</td>
							<c:if test="${hasSubTasks}">
								<td></td>
							</c:if>
							<td><fmt:formatNumber type="number" maxFractionDigits="4"
									value="${task.microF1Measure}" /></td>
							<td><fmt:formatNumber type="number" maxFractionDigits="4"
									value="${task.microPrecision}" /></td>
							<td><fmt:formatNumber type="number" maxFractionDigits="4"
									value="${task.microRecall}" /></td>
							<td><fmt:formatNumber type="number" maxFractionDigits="4"
									value="${task.macroF1Measure}" /></td>
							<td><fmt:formatNumber type="number" maxFractionDigits="4"
									value="${task.macroPrecision}" /></td>
							<td><fmt:formatNumber type="number" maxFractionDigits="4"
									value="${task.macroRecall}" /></td>
							<!-- <td>${task.state}</td> -->
							<td>${task.errorCount}</td>
							<!-- for every additional result -->
							<c:forEach items="${additionalResults[taskId.count - 1]}"
								var="additionalResult">
								<td><c:if test="${not empty additionalResult}">
										<fmt:formatNumber type="number" maxFractionDigits="4"
											value="${additionalResult}" />
									</c:if></td>
							</c:forEach>
							<td>${task.timestampstring}</td>
							<td>${task.gerbilVersion}</td>
						</tr>
						<c:forEach var="subTask" items="${task.subTasks}">
							<tr>
								<td>${task.annotator}</td>
								<td>${task.dataset}</td>
								<td>${subTask.type.label}</td>
								<td><fmt:formatNumber type="number" maxFractionDigits="4"
										value="${subTask.microF1Measure}" /></td>
								<td><fmt:formatNumber type="number" maxFractionDigits="4"
										value="${subTask.microPrecision}" /></td>
								<td><fmt:formatNumber type="number" maxFractionDigits="4"
										value="${subTask.microRecall}" /></td>
								<td><fmt:formatNumber type="number" maxFractionDigits="4"
										value="${subTask.macroF1Measure}" /></td>
								<td><fmt:formatNumber type="number" maxFractionDigits="4"
										value="${subTask.macroPrecision}" /></td>
								<td><fmt:formatNumber type="number" maxFractionDigits="4"
										value="${subTask.macroRecall}" /></td>
								<!--<td colspan="3"></td> -->
								<td>${task.errorCount}</td>
								<!-- for every additional result -->
								<c:forEach items="${additionalResultNames}" var="name">
									<td></td>
								</c:forEach>
								<td>${task.timestampstring}</td>
								<td>${task.gerbilVersion}</td>
							</tr>
						</c:forEach>
					</c:if>
					<c:if test="${!empty task.stateMsg}">
						<tr>
							<td>${task.annotator}</td>
							<td>${task.dataset}</td>
							<c:if test="${hasSubTasks}">
								<td></td>
							</c:if>
							<td colspan="${additionalResultsCount + 7}"
								style="text-align: center">${task.stateMsg}</td>
							<td>${task.timestampstring}</td>
							<td>${task.gerbilVersion}</td>
						</tr>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
	</c:if>



	<script type="text/javascript">
		$(document).ready(function() {
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
				+ experimentId
				+ "</a>";
			// If this is the AKSW instance of GERBIL
			if(origin == "http://gerbil.aksw.org") {
				content += " and <a href=\"http://w3id.org/gerbil/experiment"
					+ experimentId
					+ "\">http://w3id.org/gerbil/experiment"
					+ experimentId
					+ "</a>";
			}
			// If this is the AKSW instance of GERBIL QA
			if(origin == "http://gerbil-qa.aksw.org") {
				content += " and <a href=\"http://w3id.org/gerbil/qa/experiment"
					+ experimentId
					+ "\">http://w3id.org/gerbil/qa/experiment"
					+ experimentId
					+ "</a>";
			}
			$("#experimentUri").html(content);
		});
	</script>
</body>