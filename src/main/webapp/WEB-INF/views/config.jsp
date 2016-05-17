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

.fileinput-button {
	position: relative;
	overflow: hidden;
}

.fileinput-button input {
	position: absolute;
	top: 0;
	right: 0;
	margin: 0;
	opacity: 0;
	-ms-filter: 'alpha(opacity=0)';
	font-size: 200px;
	direction: ltr;
	cursor: pointer;
}

/* Fixes for IE < 8 */
@media screen\9 {
	.fileinput-button input {
		filter: alpha(opacity =                           0);
		font-size: 100%;
		height: 100%;
	}
}
</style>
</head>
<c:url value="/file/upload" var="upload" />
<body class="container">
	<!-- mappings to URLs in back-end controller -->
	<c:url var="annotators" value="/annotators" />
	<c:url var="matchings" value="/matchings" />
	<c:url var="exptypes" value="/exptypes" />
	<c:url var="datasets" value="/datasets" />
	<c:url var="execute" value="/execute" />
	<c:url var="testNifWs" value="/testNifWs" />

	<script src="/gerbil/webjars/jquery/2.1.1/jquery.min.js"></script>
	<script src="/gerbil/webjars/bootstrap/3.2.0/js/bootstrap.min.js"></script>
	<script
		src="/gerbil/webjars/bootstrap-multiselect/0.9.8/js/bootstrap-multiselect.js"></script>
	<c:url var="jquerywidget"
		value="/webResources/js/vendor/jquery.ui.widget.js" />
	<script src="${jquerywidget}"></script>
	<c:url var="jqueryiframe"
		value="/webResources/js/jquery.iframe-transport.js" />
	<script src="${jqueryiframe}"></script>
	<c:url var="jqueryfileupload"
		value="/webResources/js/jquery.fileupload.js" />
	<script src="${jqueryfileupload}"></script>
	<%@include file="navbar.jsp"%>
	<h1>GERBIL Experiment Configuration</h1>


	<form id="configForm" class="form-horizontal">
		<fieldset>
			<!-- Form Name -->
			<legend>New Experiment</legend>
			<!-- experiment type dropdown filled by loadexptype() function -->
			<div class="form-group">
				<label class="col-md-4 control-label" for="type">Experiment
					Type</label>
				<div class="col-md-4">
					<select id="type" style="display: none;">
					</select>
				</div>
			</div>
			<div class="row">
				<div class="col-md-8 col-md-offset-2">
					<hr />
				</div>
			</div>
			<!--Matching dropdown filled by loadMatching() function -->
			<div class="form-group">
				<label class="col-md-4 control-label" for="annotator">Matching</label>
				<div class="col-md-4">
					<select id="matching" style="display: none;">
					</select>
				</div>
			</div>
			<div class="row">
				<div class="col-md-8 col-md-offset-2">
					<hr />
				</div>
			</div>
			<!--Annotator dropdown filled by loadAnnotator() function -->
			<div class="form-group">
				<label class="col-md-4 control-label" for="annotator">Annotator</label>
				<div class="col-md-4">
					<select id="annotator" multiple="multiple" style="display: none;">
					</select>
					<hr />
					<div>
						<span> Or add another webservice via URI:</span>
						<div>
							<label for="nameAnnotator">Name:</label> <input
								class="form-control" type="text" id="nameAnnotator" name="name"
								placeholder="Type something" /> <label for="URIAnnotator">URI:</label>
							<input class="form-control" type="text" id="URIAnnotator"
								name="URI" placeholder="Type something" />
						</div>
						<div>
							<!-- list to be filled by button press and javascript function addAnnotator -->
							<ul id="annotatorList"
								style="margin-top: 15px; list-style-type: none;">
							</ul>
						</div>
						<div id="warningEmptyAnnotator" class="alert alert-warning"
							role="alert">
							<button type="button" class="close" data-dismiss="alert"></button>
							<strong>Warning!</strong> Enter a name and an URI.
						</div>
						<div id="infoAnnotatorTest" class="alert alert-info" role="alert">
							<button type="button" class="close" data-dismiss="alert"></button>
							<strong>Please wait</strong> while the communication with your
							annotator is tested...
						</div>
						<div id="dangerAnnotatorTestError" class="alert alert-danger"
							role="alert">
							<button type="button" class="close" data-dismiss="alert"></button>
							<strong>Warning!</strong> There was an error while testing the
							annotator.<br> <span id="annotatorTestErrorMsg"></span>
						</div>
						<input type="button" id="addAnnotator"
							class="btn btn-primary pull-right" value="Add another annotator"
							style="margin-top: 15px" />
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-8 col-md-offset-2">
					<hr />
				</div>
			</div>
			<!--Dataset dropdown filled by loadDatasets() function -->
			<div class="form-group">
				<label class="col-md-4 control-label" for="datasets">Dataset</label>
				<div class="col-md-4">
					<select id="dataset" multiple="multiple" style="display: none;">
					</select>
					<hr />
					<div>
						<span> Or upload another dataset:</span>
						<div>
							<label for="nameDataset">Name:</label> <input
								class="form-control" type="text" id="nameDataset" name="name"
								placeholder="Type something" /> <br> <span
								class="btn btn-success fileinput-button"> <i
								class="glyphicon glyphicon-plus"></i> <span>Select
									file...</span> <!-- The file input field used as target for the file upload widget -->
								<input id="fileupload" type="file" name="files[]">
							</span> <br> <br>
							<!-- The global progress bar -->
							<div id="progress" class="progress">
								<div class="progress-bar progress-bar-success"></div>
							</div>
							<div>
								<!-- list to be filled by button press and javascript function addDataset -->
								<ul class="unstyled" id="datasetList"
									style="margin-top: 15px; list-style-type: none;">
								</ul>
							</div>
							<div id="warningEmptyDataset" class="alert alert-warning"
								role="alert">
								<button type="button" class="close" data-dismiss="alert"></button>
								<strong>Warning!</strong> Enter a name.
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-8 col-md-offset-2">
					<hr />
				</div>
			</div>
			<div class="form-group">
				<label class="col-md-4 control-label" for="datasets">Disclaimer</label>
				<div class="checkbox">
					<label> <input id="disclaimerCheckbox" type="checkbox">
						I have read and understand the <a
						href="https://github.com/AKSW/gerbil/wiki/Disclaimer">disclaimer</a>.
					</label>
				</div>
			</div>
			<!-- Button -->
			<div class="form-group">
				<label class="col-md-4 control-label" for="submit"></label>
				<div id="submitField" class="col-md-4">
					<input type="button" id="submit" name="singlebutton"
						class="btn btn-primary" value="Run Experiment" />
				</div>
			</div>
		</fieldset>
	</form>
	<script type="text/javascript">
		// PLEASE DECLARE FUNCTION _OUTSIDE_ OF THE $(document).ready(function() {...}) !!!

		//declaration of functions for loading experiment types, annotators, matchings and datasets
		function loadExperimentTypes() {
			$
					.getJSON(
							'${exptypes}',
							{
								ajax : 'false'
							},
							function(data) {
								var formattedData = [];
								for (var i = 0; i < data.ExperimentType.length; i++) {
									var dat = {};
									dat.label = data.ExperimentType[i].label;
									dat.value = data.ExperimentType[i].name;
									formattedData.push(dat);
								}
								$('#type').multiselect('dataprovider',
										formattedData);
								$('#type').multiselect('rebuild');

								$($('#type').next().find('li'))
										.each(
												function(index) {
													for (var i = 0; i < data.ExperimentType.length; i++) {
														if (data.ExperimentType[i].name == $(
																this).find(
																'input').val()) {
															$(this)
																	.attr(
																			'data-toggle',
																			'tooltip')
																	.attr(
																			'data-placement',
																			'top')
																	.attr(
																			'title',
																			data.ExperimentType[i].description);
														}
													}
												});

								$('[data-toggle="tooltip"]').tooltip();

								loadMatching();
								loadAnnotator();
								loadDatasets();
							});
		}
		function loadMatching() {
			$('#matching').html('');
			$('#annotator').html('');
			$
					.getJSON(
							'${matchings}',
							{
								experimentType : $('#type').val(),
								ajax : 'false'
							},
							function(data) {
								var formattedData = [];
								for (var i = 0; i < data.Matching.length; i++) {
									var dat = {};
									dat.label = data.Matching[i].label;
									dat.value = data.Matching[i].name;
									formattedData.push(dat);
								}
								$('#matching').multiselect('dataprovider',
										formattedData);
								$('#matching').multiselect('rebuild');

								$($('#matching').next().find('li'))
										.each(
												function(index) {
													for (var i = 0; i < data.Matching.length; i++) {
														if (data.Matching[i].name == $(
																this).find(
																'input').val()) {
															$(this)
																	.attr(
																			'data-toggle',
																			'tooltip')
																	.attr(
																			'data-placement',
																			'top')
																	.attr(
																			'title',
																			data.Matching[i].description);
														}
													}
												});

								$('[data-toggle="tooltip"]').tooltip();

							});
		}
		function loadAnnotator() {
			$('#annotator').html('');
			$.getJSON('${annotators}', {
				experimentType : $('#type').val(),
				ajax : 'false'
			}, function(data) {
				var formattedData = [];
				for (var i = 0; i < data.length; i++) {
					var dat = {};
					dat.label = data[i];
					dat.value = data[i];
					formattedData.push(dat);
				}
				$('#annotator').multiselect('dataprovider', formattedData);
				<c:url value="/file/upload" var="upload"/>
				$('#annotator').multiselect('rebuild');
			});
		}
		function loadDatasets() {
			$('#dataset').html('');
			$.getJSON('${datasets}', {
				experimentType : $('#type').val(),
				ajax : 'false'
			}, function(data) {
				var formattedData = [];
				for (var i = 0; i < data.length; i++) {
					var dat = {};
					dat.label = data[i];
					dat.value = data[i];
					formattedData.push(dat);
				}
				$('#dataset').multiselect('dataprovider', formattedData);
				$('#dataset').multiselect('rebuild');
			});
		}
		function checkExperimentConfiguration() {
			//fetch list of selected and manually added annotators
			var annotatorMultiselect = $('#annotator option:selected');
			var annotator = [];
			$(annotatorMultiselect).each(function(index, annotatorMultiselect) {
				annotator.push([ $(this).val() ]);
			});
			$("#annotatorList li span.li_content").each(function() {
				annotator.push($(this).text());
			});
			//fetch list of selected and manually added datasets
			var datasetMultiselect = $('#dataset option:selected');
			var dataset = [];
			$(datasetMultiselect).each(function(index, datasetMultiselect) {
				dataset.push([ $(this).val() ]);
			});
			$("#datasetList li span.li_content").each(function() {
				dataset.push($(this).text());
			});

			//check whether there is at least one dataset and at least one annotator 
			//and the disclaimer checkbox should be clicked
			if (dataset.length > 0 && annotator.length > 0
					&& $('#disclaimerCheckbox:checked').length == 1) {
				$('#submit').attr("disabled", false);
			} else {
				$('#submit').attr("disabled", true);
			}
		}
		function defineNIFAnnotator() {
			// hide all message that might be outdated
			$('#warningEmptyAnnotator').hide();
			$('#infoAnnotatorTest').hide();
			$('#dangerAnnotatorTestError').hide();

			var name = $('#nameAnnotator').val();
			var uri = $('#URIAnnotator').val();
			// check that URL and name are set
			if (name === '' || uri === '') {
				$('#warningEmptyAnnotator').show();
			} else {
				$('#infoAnnotatorTest').show();
				$
						.getJSON(
								'${testNifWs}',
								{
									experimentType : $('#type').val(),
									url : uri
								},
								function(data) {
									$('#infoAnnotatorTest').hide();
									if (data.testOk === true) {
										$('#annotatorList')
												.append(
														"<li><span class=\"glyphicon glyphicon-remove\"></span>&nbsp<span class=\"li_content\">"
																+ name
																+ "("
																+ uri
																+ ")</span></li>");
										var listItems = $('#annotatorList > li > span');
										for (var i = 0; i < listItems.length; i++) {
											listItems[i].onclick = function() {
												this.parentNode.parentNode
														.removeChild(this.parentNode);
												checkExperimentConfiguration();
											};
										}
										$('#nameAnnotator').val('');
										$('#URIAnnotator').val('');
									} else {
										$('span#annotatorTestErrorMsg').text(
												data.errorMsg);
										$('#dangerAnnotatorTestError').show();
									}
								});
			}
			//check showing run button if something is changed in dropdown menu
			checkExperimentConfiguration();
		}

		$(document)
				.ready(
						function() {
							// load dropdowns when document loaded 
							$('#type').multiselect();
							$('#matching').multiselect();
							$('#annotator').multiselect();
							$('#dataset').multiselect();

							// listeners for dropdowns 
							$('#type').change(loadMatching);
							$('#type').change(loadAnnotator);
							$('#type').change(loadDatasets);

							loadExperimentTypes();

							//supervise configuration of experiment and let it only run
							//if everything is ok 
							//initially it is turned off 
							$('#submit').attr("disabled", true);
							//check showing run button if something is changed in dropdown menu
							$('#annotator').change(function() {
								checkExperimentConfiguration();
							});
							$('#dataset').change(function() {
								checkExperimentConfiguration();
							});
							$('#disclaimerCheckbox').change(function() {
								checkExperimentConfiguration();
							});

							//if add button is clicked check whether there is a name and a uri 
							$('#warningEmptyAnnotator').hide();
							$('#infoAnnotatorTest').hide();
							$('#dangerAnnotatorTestError').hide();
							$('#addAnnotator').click(defineNIFAnnotator);
							//if add button is clicked check whether there is a name and a uri 
							$('#warningEmptyDataset').hide();
							$('#fileupload').click(function() {
								var name = $('#nameDataset').val();
								if (name == '') {
									$('#fileupload').fileupload('disable');
									$('#warningEmptyDataset').show();
								} else {
									$('#fileupload').fileupload('enable');
									$('#warningEmptyDataset').hide();
								}
							});

							//submit button clicked will collect and sent experiment data to backend
							$('#submit')
									.click(
											function() {
												//fetch list of selected and manually added annotators
												var annotatorMultiselect = $('#annotator option:selected');
												var annotator = [];
												$(annotatorMultiselect)
														.each(
																function(index,
																		annotatorMultiselect) {
																	annotator
																			.push($(
																					this)
																					.val());
																});
												$(
														"#annotatorList li span.li_content")
														.each(
																function() {
																	annotator
																			.push("NIFWS_"
																					+ $(
																							this)
																							.text());
																});
												//fetch list of selected and manually added datasets
												var datasetMultiselect = $('#dataset option:selected');
												var dataset = [];
												$(datasetMultiselect)
														.each(
																function(index,
																		datasetMultiselect) {
																	dataset
																			.push($(
																					this)
																					.val());
																});
												$(
														"#datasetList li span.li_content")
														.each(
																function() {
																	dataset
																			.push("NIFDS_"
																					+ $(
																							this)
																							.text());
																});
												var type = $('#type').val() ? $(
														'#type').val()
														: "D2KB";
												var matching = $('#matching')
														.val() ? $('#matching')
														.val()
														: "Ma - strong annotation match";
												var data = {};
												data.type = type;
												data.matching = matching;
												data.annotator = annotator;
												data.dataset = dataset;
												$
														.ajax(
																'${execute}',
																{
																	data : {
																		'experimentData' : JSON
																				.stringify(data)
																	}
																})
														.done(
																function(data) {
																	$('#submit')
																			.remove();
																	var origin = window.location.origin;
																	var link = "<a href=\"/gerbil/experiment?id="
																			+ data
																			+ "\">"
																			+ origin
																			+ "/gerbil/experiment?id="
																			+ data
																			+ "</a>";
																	var span = "<span>Find your experimental data here: </span>";
																	$(
																			'#submitField')
																			.append(
																					span);
																	$(
																			'#submitField')
																			.append(
																					link);
																})
														.fail(
																function() {
																	alert("Error, insufficient parameters.");
																});
											});
						});

		// define file upload
		$(function() {
			'use strict';
			// Change this to the location of your server-side upload handler:
			var url = '${upload}';
			$('#fileupload')
					.fileupload(
							{
								url : url,
								dataType : 'json',
								done : function(e, data) {
									var name = $('#nameDataset').val();
									$
											.each(
													data.result.files,
													function(index, file) {
														$('#datasetList')
																.append(
																		"<li><span class=\"glyphicon glyphicon-remove\"></span>&nbsp<span class=\"li_content\">"
																				+ name
																				+ "("
																				+ file.name
																				+ ")</span></li>");
														var listItems = $('#datasetList > li > span');
														for (var i = 0; i < listItems.length; i++) {
															listItems[i].onclick = function() {
																this.parentNode.parentNode
																		.removeChild(this.parentNode);
																checkExperimentConfiguration();
															};
														}
														$('#nameDataset').val(
																'');
														$('#URIDataset')
																.val('');
													});
								},
								progressall : function(e, data) {
									var progress = parseInt(data.loaded
											/ data.total * 100, 10);
									$('#progress .progress-bar').css('width',
											progress + '%');
								},
								processfail : function(e, data) {
									alert(data.files[data.index].name + "\n"
											+ data.files[data.index].error);
								}
							}).prop('disabled', !$.support.fileInput).parent()
					.addClass($.support.fileInput ? undefined : 'disabled');
		});
	</script>
</body>