<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<head>
<link rel="stylesheet"	href="webjars/bootstrap/3.2.0/css/bootstrap.min.css">
<link rel="stylesheet"	href="webjars/bootstrap-multiselect/0.9.8/css/bootstrap-multiselect.css"/>
</head>
<body class="container">
	<c:url var="findAnnotator" value="/annotators" />
	<script src="webjars/jquery/2.1.1/jquery.min.js"></script>
	<script src="webjars/bootstrap/3.2.0/js/bootstrap.min.js"></script>
	<script src="webjars/bootstrap-multiselect/0.9.8/js/bootstrap-multiselect.js"></script>

	<script type="text/javascript">
		$(document).ready(function() {
			$('#type').change(function() {
				$('#annotator').html('');
				$.getJSON('${findAnnotator}', {
					experimentType : $(this).val(),
					ajax : 'false'
				}, function(data) {
					console.log(data)
					var formattedData = [];

					var len = data.length;
					for (var i = 0; i < len; i++) {
						var dat = {};
						dat.label = data[i];
						dat.value = data[i];
						console.log(dat);
						formattedData.push(dat);
					}
					console.log(formattedData);
					$('#annotator').multiselect('dataprovider', formattedData);
					$('#annotator').multiselect('rebuild');
				});

			});
		});
	</script>
	<script type="text/javascript">
		$(document).ready(function() {
			$('#annotator').multiselect({
				disableIfEmpty : true
			});
		});
	</script>
	<%@include file="navbar.jsp"%>
	<h1>Gerbil Experiment Configuration</h1>


	<c:url var="url" value="/execute" />
	<form:form class="form-horizontal" action="${url}">
		<fieldset>

			<!-- Form Name -->
			<legend>New Experiment</legend>

			<!-- Select Basic -->
			<div class="form-group">
				<label class="col-md-4 control-label" for="type">Experiment
					Type</label>
				<div class="col-md-4">
					<form:select id="type" path="type" class="form-control">
						<form:options />
					</form:select>
				</div>
			</div>

			<div class="form-group">
				<label class="col-md-4 control-label" for="annotator">Annotator</label>
				<div class="col-md-4 btn-group">
					<form:select id="annotator" multiple="multiple" path="annotator"
						style="display: none;">
					</form:select>
				</div>
			</div>

			<!-- Button -->
			<div class="form-group">
				<label class="col-md-4 control-label" for="submit"></label>
				<div class="col-md-4">
					<input type="submit" id="submit" name="singlebutton"
						class="btn btn-primary" value="Run" />
				</div>
			</div>

		</fieldset>
	</form:form>


</body>