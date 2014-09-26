<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<head>
<link rel='stylesheet'
	href='../webjars/bootstrap/3.2.0/css/bootstrap.min.css'>

<link rel="stylesheet"
	href="../webjars/bootstrap-multiselect/0.9.8/css/bootstrap-multiselect.css"
	type="text/css" />
</head>
<body class="col-md-6">
	<c:url var="findAnnotator" value="/annotators" />
	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="../webjars/jquery/2.1.1/jquery.min.js"></script>
	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<script src="../webjars/bootstrap/3.2.0/js/bootstrap.min.js"></script>
	<script type="text/javascript"
		src="../webjars/bootstrap-multiselect/0.9.8/js/bootstrap-multiselect.js"></script>

	<script type="text/javascript">
		$(document).ready(
				function() {
					$('#type')
							.change(
									function() {
										alert('changed ' + $(this).val());
										$('#annotator').html('');
										var dropdownOptionList = [];
										$.getJSON('${findAnnotator}', {
											experimentType : $(this).val(),
											ajax : 'true'
										}, function(data) {
											var len = data.length;
											for (var i = 0; i < len; i++) {
												dropdownOptionList.push({
													'label' : data[i],
													'value' : data[i]
												});
												$('#annotator').append(
														'<option value="'+data[i]+'">'
																+ data[i]
																+ '</option>');
											}

										});
									
										$('#annotator').multiselect('rebuild');

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