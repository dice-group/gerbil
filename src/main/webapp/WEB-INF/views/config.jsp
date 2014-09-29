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
					<form:select id="type" multiple="radio" path="type" style="display: none;">
					</form:select>
				</div>
			</div>

			<div class="form-group">
				<label class="col-md-4 control-label" for="annotator">Matching</label>
				<div class="col-md-4">
					<form:select id="matching" multiple="radio" path="annotator" style="display: none;">
					</form:select>
				</div>
			</div>

			<div class="form-group">
				<label class="col-md-4 control-label" for="annotator">Annotator</label>
				<div class="col-md-4">
					<form:select id="annotator" multiple="multiple" path="annotator" style="display: none;">
					</form:select>
					<div>
						<span> Or add another webservice via URI:</span>
						<div>
							<label for="name">Name:</label>
							<input class="form-control" type="text" id="name" name="name" placeholder="Type something" />
							<label for="URI">URI:</label>
							<input class="form-control" type="text" id="URI" name="URI" placeholder="Type something" />
						</div>
						<div>
							<ul class="unstyled" id="annotatorList" style="margin-top: 15px">
							</ul>
						</div>
						<div id="warningEmptyAnnotator" class="alert alert-warning" role="alert">
							<button type="button" class="close" data-dismiss="alert">
							</button>
							<strong>Warning!</strong> Enter a name and an URI.
						</div>
						<input type="button" id="addAnnotator" class="btn btn-primary pull-right" value="Add another annotator" style="margin-top: 15px" />
					</div>
				</div>
			</div>
			<!-- Button -->
			<div class="form-group">
				<label class="col-md-4 control-label" for="submit"></label>
				<div class="col-md-4">
					<input type="submit" id="submit" name="singlebutton" class="btn btn-primary" value="Run Experiment" />
				</div>
			</div>
		</fieldset>
	</form:form>
	<script type="text/javascript">
		$(document).ready(function() {
			var loadExperimentTypes;
			var loadMatching;
			var loadAnnotator;

			(loadExperimentTypes = function() {
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
			});

			(loadMatching = function() {
				$('#matching').html('');
				$('#annotator').html('');
				$.getJSON('${matchings}', {
					experimentType : $('#type').val() ? $('#type').val() : "A2W",
					ajax : 'false'
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
			})();
			(loadAnnotator = function() {
				$('#annotator').html('');
				$.getJSON('${annotators}', {
					experimentType : $('#type').val() ? $('#type').val() : "A2W",
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

			loadExperimentTypes();
			loadAnnotator();
			$('#type').change(loadMatching);
			$('#type').change(loadAnnotator);
			$('#matching').change(loadAnnotator);

			$('#annotator').change(function() {
				$('#submit').attr("disabled", false);
			});

			$('#submit').attr("disabled", true);

			$('#warningEmptyAnnotator').hide();
			$('#addAnnotator').click(function() {
				console.log("checking")
				var name = $('#name').val();
				var uri = $('#URI').val();
				if (name === '' || uri === '') {
					$('#warningEmptyAnnotator').show();
				} else {
					$('#warningEmptyAnnotator').hide();
					$('#annotatorList').append("<li>" + name + "(" + uri + ")</li>");
					$('#name').val('');
					$('#URI').val('');
				}
			});
		});
	</script>
</body>