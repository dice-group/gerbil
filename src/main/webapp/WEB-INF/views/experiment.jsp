<%@page import="org.aksw.gerbil.web.ExperimentTaskStateHelper"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<head>
<link rel="stylesheet"
	href="webjars/bootstrap/3.2.0/css/bootstrap.min.css">
<link rel="stylesheet"
	href="webjars/bootstrap-multiselect/0.9.8/css/bootstrap-multiselect.css" />
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

	<script src="webjars/jquery/2.1.1/jquery.min.js"></script>
	<script src="webjars/bootstrap/3.2.0/js/bootstrap.min.js"></script>
	<script src="webjars/tablesorter/2.15.5/js/jquery.tablesorter.js"></script>

	<script type="application/ld+json">
	${dataid}
	</script>

	<%@include file="navbar.jsp"%>
	<h1>GERBIL Experiment</h1>
	<c:if test="${not empty tasks}">

	Type: <c:out value="${tasks[0].type}" />
		</br>
	Matching: <c:out value="${tasks[0].matching}" />
		<table id="resultTable"
			class="table  table-hover table-condensed tablesorter tableScroll">
			<thead>
				<tr>
					<th>Annotator</th>
					<th>Dataset</th>
					<th>Micro F1</th>
					<th>Micro Precision</th>
					<th>Micro Recall</th>
					<th>Macro F1</th>
					<th>Macro Precision</th>
					<th>Macro Recall</th>
					<!-- <th>State</th> -->
					<th>Error Count</th>
					<th>Timestamp</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="task" items="${tasks}">
					<tr>
						<td>${task.annotator}</td>
						<td>${task.dataset}</td>
						<c:if test="${empty task.stateMsg}">
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
						</c:if>
						<c:if test="${not empty task.stateMsg}">
							<td colspan="7" style="text-align:center">${task.stateMsg}</td>
						</c:if>
						<td>${task.timestampstring}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>



	<script type="text/javascript">
		$(document).ready(function() {
	        $("#resultTable").tablesorter({
		        sortList : [ [ 0, 0 ], [ 1, 0 ] ]
	        });
        });
	</script>
</body>