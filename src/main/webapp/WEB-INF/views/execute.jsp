<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<head>
<link rel='stylesheet'	href='/gerbil/webjars/bootstrap/3.2.0/css/bootstrap.min.css'>
<script src="/gerbil/webjars/jquery/2.1.1/jquery.min.js"></script>
<script src="/gerbil/webjars/bootstrap/3.2.0/js/bootstrap.min.js"></script>
<link rel="icon" type="image/png" href="/gerbil/webResources/gerbilicon_transparent.png">
</head>
<body class="col-md-6">
	<%@include file="navbar.jsp"%>

	<h1>Executing Experiment with Configuration:</h1>
	<div class="input-group">
		<span class="label label-primary">Type:</span><span
			class="label label-success">${command.type}</span>
	</div>
	<div class="input-group">
		<span class="label label-primary">Annotator:</span><span
			class="label label-success">${command.annotator}</span>
	</div>
</body>