<%@page import="org.aksw.gerbil.web.ExperimentTaskStateHelper"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
</style>

</head>
<body class="container">
	<!-- mappings to URLs in back-end controller -->
	<c:url var="experiment" value="/experiment" />

	<script src="/gerbil/webjars/jquery/2.1.1/jquery.min.js"></script>
	<script src="/gerbil/webjars/bootstrap/3.2.0/js/bootstrap.min.js"></script>
	<script
		src="/gerbil/webjars/tablesorter/2.15.5/js/jquery.tablesorter.js"></script>
	<script src="/gerbil/webResources/js/Chart.js"></script>
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
	<!-- Matching does not make sense for SWC tasks Matching: <c:out value="${tasks[0].matching.label}" /> -->
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

					<c:forEach var="resName" items="${resultNames}">
						<th>${resName}</th>
					</c:forEach>
					
					<c:if test="${!empty tasks[0].stateMsg}">
					<th colspan="7"
								style="text-align: center">Error Message</th>
					</c:if>						
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
							<c:forEach var="resName" items="${resultNames}">
									<c:choose>
										<c:when test="${task.resultsMap.get(resName).getResValue()!=null && !resName.equalsIgnoreCase(\"ROC\") && !resName.equalsIgnoreCase(\"PR\")}">
									       <td> <fmt:formatNumber type="number" maxFractionDigits="4"
										value="${task.resultsMap.get(resName).getResValue()}" /></td>
										</c:when>
										<c:when test="${resName.equalsIgnoreCase(\"ROC\") or resName.equalsIgnoreCase(\"PR\")}"> 
									       <td><canvas id="${resName.hashCode()+task.hashCode()}" width="300" height="300"></canvas></td>		
			 								<script>
			 								var ctx = document.getElementById("${resName.hashCode()+task.hashCode()}").getContext('2d');
			 								var myLineChart = new Chart(ctx, {
			    								type: 'scatter',
			   		 							data : { datasets : [{showLine : true, label : "", lineTension : 0, fill : true, pointRadius : 0, borderColor : "#0084e5", ${task.resultsMap.get(resName).resValue}}]},
			   		 							options: { legend: {display: false} , scales: {yAxes: [{ticks: {max : 1, beginAtZero:true}}], xAxes : [{ticks: {max : 1, beginAtZero:true}}]}}
											});
			 								</script>
										</c:when>
										<c:otherwise><td></td> </c:otherwise>
									</c:choose>
							</c:forEach>
 													
							<td>${task.timestampstring}</td>
							<td>${task.version}</td>
						</tr>
						<c:forEach var="subTask" items="${task.subTasks}">
							<tr>
								<td>${task.annotator}</td>
								<td>${task.dataset}</td>
								<td>${subTask.type.label}</td>

								<c:forEach var="resName" items="${resultNames}">
									<td> 
										<c:choose>
											<c:when test="${task.resultsMap.get(resName).getResValue()!=null && !resName.equalsIgnoreCase(\"ROC\") && !resName.equalsIgnoreCase(\"PR\")}">
										        <fmt:formatNumber type="number" maxFractionDigits="4"
											value="${task.resultsMap.get(resName).getResValue()}" />
											</c:when>
											<c:when test="${resName.equalsIgnoreCase(\"ROC\") or resName.equalsIgnoreCase(\"PR\")}">
										       <td><canvas id="${task.hashCode()}" width="300" height="300"></canvas></td>		
				 								<script>
				 								var ctx = document.getElementById("${resName.hashCode()+task.hashCode()}").getContext('2d');
				 								var myLineChart = new Chart(ctx, {
				    								type: 'scatter',
				   		 							data : { datasets : [{showLine : true, label : "", lineTension : 0, fill : true, pointRadius : 0, borderColor : "#0084e5", ${task.resultsMap.get(resName).resValue}}]},
				   		 							options: { legend: {display: false} , scales: {yAxes: [{ticks: {max : 1, beginAtZero:true}}], xAxes : [{ticks: {max : 1, beginAtZero:true}}]}}
												});
				 								</script>
											</c:when>
											<c:otherwise> </c:otherwise>
										</c:choose>
									</td>
								</c:forEach>
								<td>${task.timestampstring}</td>
								<td>${task.version}</td>
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
							<td colspan="7"
								style="text-align: center">${task.stateMsg}</td>
							<td>${task.timestampstring}</td>
							<td>${task.version}</td>
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
			// If this is the AKSW instance of GERBIL SW
			if(origin == "http://gerbil-kbc.aksw.org" || origin == "http://gerbil-sw.aksw.org" || origin == "http://swc2017.aksw.org") {
				content += " and <a href=\"http://w3id.org/gerbil/kbc/experiment"
					+ experimentId
					+ "\">http://w3id.org/gerbil/kbc/experiment"
					+ experimentId
					+ "</a>";
			}
			$("#experimentUri").html(content);
		});
	</script>
</body>
