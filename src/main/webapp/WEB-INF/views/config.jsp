<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<head>
<link rel="stylesheet" href="webjars/bootstrap/3.2.0/css/bootstrap.min.css">
<link rel="stylesheet" href="webjars/bootstrap-multiselect/0.9.8/css/bootstrap-multiselect.css" />
<style type="text/css">
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
	<c:url var="annotators" value="/annotators" />
	<c:url var="matchings" value="/matchings" />
	<c:url var="exptypes" value="/exptypes" />
	
	<script src="webjars/jquery/2.1.1/jquery.min.js"></script>
	<script src="webjars/bootstrap/3.2.0/js/bootstrap.min.js"></script>
	<script src="webjars/bootstrap-multiselect/0.9.8/js/bootstrap-multiselect.js"></script>

	<script type="text/javascript">
		$(document).ready(function() {
			$.getJSON('${exptypes}', {
				ajax : 'false'
			}, function(data) {
				var formattedData = [];
				for ( var i = 0; i < data.length; i++) {
					var dat = {};
					dat.label = data[i];
					dat.value = data[i];
					formattedData.push(dat);
				}
				$('#type').multiselect('dataprovider', formattedData);
				$('#type').multiselect('rebuild');

			});
			
			$('#type').change(function() {
				$('#matching').html('');
				$.getJSON('${matchings}', {
					experimentType: $('#type').val()[0],
					ajax : 'false',
					traditional: true
				}, function(data) {
					var formattedData = [];
					for ( var i = 0; i < data.length; i++) {
						var dat = {};
						dat.label = data[i];
						dat.value = data[i];
						formattedData.push(dat);
					}
					$('#matching').multiselect('dataprovider', formattedData);
					$('#matching').multiselect('rebuild');

				});
			});
			$('#matching').change(function() {
				$('#annotator').html('');
				$.getJSON('${annotators}', {
					experimentType : $('#type').val()[0],
					ajax : 'false'
				}, function(data) {
					var formattedData = [];
					for ( var i = 0; i < data.length; i++) {
						var dat = {};
						dat.label = data[i];
						dat.value = data[i];
						formattedData.push(dat);
					}
					$('#annotator').multiselect('dataprovider', formattedData);
					$('#annotator').multiselect('rebuild');
				});
			});

			$('#annotator').change(function() {
				$('#submit').attr("disabled", false);
			});
			$('#annotator').multiselect({
				disableIfEmpty : true
			});
			$('#matching').multiselect({
				disableIfEmpty : true
			});
			$('#submit').attr("disabled", true);
		});
	</script>
	<%@include file="navbar.jsp"%>
	<h1>GERBIL Experiment Configuration</h1>


	<c:url var="url" value="/execute" />
	<form:form class="form-horizontal" action="${url}">
		<fieldset>

			<!-- Form Name -->
			<legend>New Experiment</legend>

			<div class="form-group">
				<label class="col-md-4 control-label" for="type">Experiment Type</label>
				<div class="col-md-4">
					<form:select id="type"  multiple="multiple" path="type" style="display: none;" >
						<form:option value="Nothing selected" />
					</form:select>
				</div>
			</div>

			<div class="form-group">
				<label class="col-md-4 control-label" for="annotator">Matching</label>
				<div class="col-md-4">
					<form:select id="matching" multiple="multiple" path="annotator" style="display: none;">
					</form:select>
				</div>
			</div>

			<div class="form-group">
				<label class="col-md-4 control-label" for="annotator">Annotator</label>
				<div class="col-md-4">
					<form:select id="annotator" multiple="multiple" path="annotator" style="display: none;">
					</form:select>
				</div>
			</div>

			<!-- Button -->
			<div class="form-group">
				<label class="col-md-4 control-label" for="submit"></label>
				<div class="col-md-4">
					<input type="submit" id="submit" name="singlebutton" class="btn btn-primary" value="Run" />
				</div>
			</div>
		</fieldset>
	</form:form>
</body>