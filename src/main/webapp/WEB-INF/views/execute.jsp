<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<head>
<link rel='stylesheet'
	href='webjars/bootstrap/3.2.0/css/bootstrap.min.css'>
</head>
<body class="col-md-6">
<h1>Executing Experiment with Configuration:</h1>
<div class="input-group">
<span class="label label-primary">Type:</span><span class="label label-success">${command.type}</span>
</div>
<div class="input-group">
<span class="label label-primary">Annotator:</span><span class="label label-success">${command.annotator}</span>
</div>
<script
		src="webjars/jquery/2.1.1/jquery.min.js"></script>
	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<script src="webjars/bootstrap/3.2.0/js/bootstrap.min.js"></script>
</body>