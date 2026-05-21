<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<head>
<link rel='stylesheet'	href='/gerbil/webjars/bootstrap/4.6.2/css/bootstrap.min.css'>
<script src="/gerbil/webjars/jquery/3.7.1/jquery.min.js"></script>
<script src="/gerbil/webjars/bootstrap/4.6.2/js/bootstrap.bundle.min.js"></script>
<link rel="icon" type="image/png" href="/gerbil/webResources/gerbilicon_transparent.png">
</head>
<body class="col-md-6">
	<%@include file="navbar.jsp"%>

	<h1>Executing Experiment with Configuration:</h1>
	<div class="input-group">
		<span class="badge badge-primary mr-2">Type</span><span
			class="badge badge-success">${command.type}</span>
	</div>
	<div class="input-group">
		<span class="badge badge-primary mr-2">Annotator</span><span
			class="badge badge-success">${command.annotator}</span>
	</div>
</body>
