<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<head>
<link rel="stylesheet" href="webjars/bootstrap/3.2.0/css/bootstrap.min.css">
<link rel="stylesheet" href="webjars/bootstrap-multiselect/0.9.8/css/bootstrap-multiselect.css" />
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
	<script src="webjars/bootstrap-multiselect/0.9.8/js/bootstrap-multiselect.js"></script>

	<%@include file="navbar.jsp"%>
	<h1>GERBIL Experiment</h1>
	<c:if test="${not empty objects}">
		<table class="table  table-hover">
			<c:forEach var="o" items="${objects}">
				<tr>
					<td>${o.id}</td>
					<td>${o.name}</td>
					<td>${o.description}</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>



	<script type="text/javascript">
		$(document).ready(function() {

		});
	</script>
</body>