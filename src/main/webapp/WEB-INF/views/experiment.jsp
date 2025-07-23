<%@page import="org.aksw.gerbil.web.ExperimentTaskStateHelper"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
		.llm-result-box {
			text-align: left;
			white-space: pre-wrap;
			word-wrap: break-word;
			background-color: #f8f9fa;
			padding: 10px;
			border-radius: 5px;
			border: 1px solid #ccc;
			font-family: monospace;
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
	Experiment URI: <span id="experimentUri"></span>
	<br>
	Type: <c:out value="${tasks[0].type.label}" />
	<br>
	<br>
	<c:if test="${not empty explanationURL}">
		<p><strong>Explanation URL:</strong>
			<a href="${explanationURL}" target="_blank">${explanationURL}</a>
		</p>
	</c:if>

	Matching: <c:out value="${tasks[0].matching.label}" />
	<table id="resultTable"
		   class="table  table-hover table-condensed tableScroll">
		<!--<table id="resultTable"
        class="table  table-hover table-condensed tablesorter tableScroll">-->
		<thead>
		<tr>
			<th>Annotator</th>
			<th>Dataset</th>
			<th>Language</th>
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
					<td>${task.language}</td>
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
						<td>${task.language}</td>
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
					<td>${task.language}</td>
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



<c:if test="${not empty explanationURL}">
	<p><strong>Explanation URL:</strong>
		<a href="${explanationURL}" target="_blank">${explanationURL}</a>
	</p>
</c:if>

<c:if test="${not empty llm_result}">
	<p><strong>LLM Result:</strong></p>
	<pre class="llm-result-box">${llm_result}</pre>
</c:if>

<c:if test="${not empty prunecl_result}">
	<p><strong>PruneCEL Result:</strong></p>
	<pre class="llm-result-box">${prunecl_result}</pre>
</c:if>

<!-- Dynamic placeholders (will be updated via AJAX polling) -->
<p><strong>Explanation URL:</strong>
	<a id="explanationUrlLink" href="#" target="_blank">Loading...</a>
</p>

<p><strong>LLM Result:</strong></p>
<pre id="llmResultBox" class="llm-result-box">Explanation yet to be loaded...</pre>

<p><strong>PruneCEL Result:</strong></p>
<pre id="pruneResultBox" class="llm-result-box">Explanation yet to be loaded...</pre>



<script type="text/javascript">
	var globalExperimentId = null;

	$(document).ready(function() {
		$("#resultTable").tablesorter({
			sortList : [ [ 0, 0 ], [ 1, 0 ] ]
		});

		// print the URI of the experiment
		var origin = window.location.origin;
		var experimentId = window.location.search;
		// Extract experimentId from URL
		const urlParams = new URLSearchParams(window.location.search);
		globalExperimentId = urlParams.get('id');

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
<script>

	const datasetName = "${tasks[0].dataset}";
	let explanationPoller = null;
	let resultPoller = null;

	function pollExplanationUrl() {

		explanationPoller = setInterval(() => {
			$.ajax({
				url: "/gerbil/explanation-url-dataset",
				method: "GET",
				data: { dataset: globalExperimentId },
				success: function (url) {
					if (url && !url.includes("not found")) {
						clearInterval(explanationPoller);
						$("#explanationUrlLink")
								.attr("href", url)
								.text(url);
						pollExplanationResult(); // start second poll for results
					}
				},
				error: function (xhr, status, error) {
					console.error("Failed to fetch explanation URL:", error);
				}
			});
		}, 5000);
	}


	function pollExplanationResult() {
		resultPoller = setInterval(() => {
			$.ajax({
				url: "/gerbil/explanation-details",
				method: "GET",
				data: { dataset: globalExperimentId },
				success: function (data) {
					if (data.llm_result || data.prunecl_result) {
						clearInterval(resultPoller);
						$("#llmResultBox").text(data.llm_result || "Not available");
						$("#pruneResultBox").text(data.prunecel_result || "Not available");
					}
				}
			});
		}, 5000);
	}


	$(document).ready(function () {
		pollExplanationUrl();
	});
</script>

</body>
