<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<head>
<link rel="stylesheet"
	href="/gerbil/webjars/bootstrap/4.6.2/css/bootstrap.min.css">
<link rel="stylesheet"
	href="/gerbil/webjars/bootstrap-multiselect/dist/css/bootstrap-multiselect.css" />
<link rel="icon" type="image/png"
	href="/gerbil/webResources/gerbilicon_transparent.png">
<style type="text/css">
/* making the buttons wide enough while keeping the dropdown text left-aligned */
.btn-group>.btn {
	float: none;
	width: 100%;
	text-align: left;
}

.btn-group {
	width: 100%;
}

.multiselect-native-select {
	width: 100%;
}

.multiselect-native-select .btn-group {
	display: block;
}

.form-horizontal .control-label {
	padding-top: 7px;
	margin-bottom: 0;
}

.form-horizontal .checkbox {
	padding-top: 7px;
}

.text-right {
	text-align:right ;
}

.text-right a {
	cursor: pointer; cursor: hand;
}

#type>option {
	text-align: right;
}

.fileinput-button {
	position: relative;
	overflow: hidden;
}

.help-icon {
	display: inline-block;
	width: 1.1rem;
	height: 1.1rem;
	margin-left: 0.25rem;
	border: 1px solid #6c757d;
	border-radius: 50%;
	color: #6c757d;
	font-size: 0.75rem;
	font-weight: 700;
	line-height: 1rem;
	text-align: center;
	vertical-align: middle;
}

.multiselect-selected-text {
	display: inline-block;
	max-width: 100%;
	overflow: hidden;
	text-overflow: ellipsis;
	vertical-align: middle;
	white-space: nowrap;
}

.list-remove {
	cursor: pointer;
	font-size: 1rem;
	font-weight: 700;
	line-height: 1;
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

	<script src="/gerbil/webjars/jquery/3.7.1/jquery.min.js"></script>
	<script src="/gerbil/webjars/bootstrap/4.6.2/js/bootstrap.bundle.min.js"></script>
	<script
		src="/gerbil/webjars/bootstrap-multiselect/dist/js/bootstrap-multiselect.js"></script>
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
			<div class="form-group row">
				<div class="col-md-2"></div>
				<div class="col-md-2 text-right">
					<label class="control-label" for="type">Experiment
						Type</label>
					<a title="The QA experiment type will benchmark how good your system is at answering questions.">
							<span class="help-icon" aria-hidden="true">?</span>
					</a>	
				</div>		
				
				<div class="col-md-4">
					<select id="type" style="display: none;">
					</select>
				</div>
				
			</div>
			<div class="row" id="matchingsSeparator">
				<div class="col-md-8 offset-md-2">
					<hr />
				</div>
			</div>
			<!--Matching dropdown filled by loadMatching() function -->
			<div class="form-group row" id="matchingsDiv">
				<label class="col-md-4 control-label" for="annotator">Matching</label>
				<div class="col-md-4">
					<select id="matching" style="display: none;">
					</select>
				</div>
			</div>

			<div class="row">
				<div class="col-md-8 offset-md-2">
					<hr />
				</div>
			</div>
			<!--Dataset dropdown filled by loadDatasets() function -->
			<div class="form-group row">
				<div class="col-md-2"></div>
					<div class="col-md-2 text-right">
						<label class="control-label" for="datasets">Dataset</label>
					<a title="You can
1) select multiple of the datasets from the drop-down menu or
2) upload a QALD-formatted JSON or XML containing your custom benchmark data.

(Beta) Further on you can change the language which should be tested (default: en)">
						<span class="help-icon" aria-hidden="true">?</span>
					</a>
				</div>
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
								class="btn btn-success fileinput-button"> <span
								aria-hidden="true">+</span> <span>Select
									file...</span> <!-- The file input field used as target for the file upload widget -->
								<input id="fileupload" type="file" name="files[]" onchange="addDatasetFile(this)">
							</span> <br> <br>
							<!-- The global progress bar -->
							<div id="progress" class="progress">
								<div class="progress-bar bg-success"></div>
							</div>
							<div>
								<!-- list to be filled by button press and javascript function addDataset -->
								<ul class="unstyled" id="datasetList"
									style="margin-top: 15px; list-style-type: none;">
								</ul>
							</div>
							<div id="warningEmptyDataset" class="alert alert-warning"
								role="alert">
								<button type="button" class="close" data-dismiss="alert" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<strong>Warning!</strong> Enter a name.
							</div>
						</div>
					</div>	
				</div>
			</div>
			<div class="row">
				<div class="col-md-8 offset-md-2">
					<hr />
				</div>
			</div>
			<!--System dropdown filled by loadAnnotator() function -->
			<div class="form-group row">
				<div class="col-md-2"></div>
				<div class="col-md-2 text-right">
					<label class="control-label" for="annotator">System</label>
					<a title="You can
            1) select any of the system from the drop-down menu or
            2) add a system via its URI (needs to understand the query parameter and return valid QALD JSON or XML) or
            3) upload a QALD-formatted XML or JSON file which has the answers to one of the datasets. NOTE: First, type in the name of your system if you use option 2 or 3 and then type in the URI.">
						<span class="help-icon" aria-hidden="true">?</span>
					</a>
				</div>
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
							<button type="button" class="close" data-dismiss="alert" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<strong>Warning!</strong> Enter a name and an URI.
						</div>
						<div id="infoAnnotatorTest" class="alert alert-info" role="alert">
							<button type="button" class="close" data-dismiss="alert" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<strong>Please wait</strong> while the communication with your
							annotator is tested...
						</div>
						<div id="dangerAnnotatorTestError" class="alert alert-danger"
							role="alert">
							<button type="button" class="close" data-dismiss="alert" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<strong>Warning!</strong> There was an error while testing the
							annotator.<br> <span id="annotatorTestErrorMsg"></span>
						</div>
						<input type="button" id="addAnnotator"
							class="btn btn-primary float-right" value="Add system"
							style="margin-top: 15px" /><br> <br>
					</div>
					<hr id="uploadAnswersSeparator" />
					<div id="uploadAnswers">
						<span> Or upload a file with answers:</span>
						<div>
							<label for="nameAnswerFile">Name:</label> <input
								class="form-control" type="text" id="nameAnswerFile" name="name"
								placeholder="Type something" />
							<br>
							<select id="answerFileDataset" class="form-control">
							</select> <br> <span
								class="btn btn-success fileinput-button"> <span
								aria-hidden="true">+</span> <span>Select
									file...</span> <!-- The file input field used as target for the file upload widget -->
								<input id="answerFileUpload" type="file" name="files[]">
							</span> <br> <br>
							<!-- The global progress bar -->
							<div id="answerFileProgress" class="progress">
								<div class="progress-bar bg-success"></div>
							</div>
							<div>
								<!-- list to be filled by button press and javascript function addDataset -->
								<ul class="unstyled" id="answerFileList"
									style="margin-top: 15px; list-style-type: none;">
								</ul>
							</div>
							<div id="warningEmptyAnswerFileName" class="alert alert-warning"
								role="alert">
								<button type="button" class="close" data-dismiss="alert" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<strong>Warning!</strong> Enter a name.
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-8 offset-md-2">
					<hr />
				</div>
			</div>

			<div class="form-group row">
				<div class="col-md-2"></div>
					<div class="col-md-2 text-right">
						<label class="control-label" >Language</label>					
					<a title="(Beta) You can change the language which should be tested (default: en)
					
F.e. if you want to use French, type in: fr">
						<span class="help-icon" aria-hidden="true">?</span>
					</a>
				</div>
				<div class="col-md-4">

					<div>
						<input
								class="form-control" type="text" id="qLang" name="qlang"
								placeholder="Type something: e.g. fr" /> <br>
					</div>		
					
					
				</div>
			</div>
			<div class="row">
				<div class="col-md-8 offset-md-2">
					<hr />
				</div>
			</div>
			<div class="form-group row">
				<label class="col-md-4 control-label" for="datasets">Disclaimer</label>
				<div class="col-md-4 checkbox">
					<label> <input id="disclaimerCheckbox" type="checkbox">
						I have read and understand the <a
						href="https://github.com/AKSW/gerbil/wiki/Disclaimer">disclaimer</a>.
					</label>
				</div>
			</div>
			<!-- Button -->
			<div class="form-group row">
				<label class="col-md-4 control-label" for="submit"></label>
				<div id="submitField" class="col-md-4">
					<input type="button" id="submit" name="singlebutton"
						class="btn btn-primary" value="Run Experiment" />
				</div>
			</div>
		</fieldset>
	</form>
	<script type="text/javascript">

		function getBaseMultiselectOptions() {
			return {
				buttonClass : 'btn btn-outline-secondary',
				buttonWidth : '100%',
				maxHeight : 300
			};
		}

		function initializeMultiselect(elementId, options) {
			if ($(elementId).length === 0) {
				return;
			}
			$(elementId).multiselect($.extend({}, getBaseMultiselectOptions(), options));
		}

		function normalizeDisplayValue(value, fallbackValue) {
			var normalizedValue = $.trim(value || '');
			if (normalizedValue.length > 0) {
				return normalizedValue;
			}
			return fallbackValue;
		}

		function getSelectOptionLabel(optionElement) {
			var currentOption = $(optionElement);
			return normalizeDisplayValue(currentOption.attr('label'),
					normalizeDisplayValue(currentOption.text(), currentOption.val()));
		}

		function populateMissingOptionTexts(elementId) {
			$(elementId).find('option').each(function() {
				$(this).text(getSelectOptionLabel(this));
			});
		}

		// Adds the given data to the given (multi) select element. It is assumed that data is an array of Strings that are used as label and value of the single options.
		function addDataToSelect(elementId, data) {
			if ($(elementId).length === 0) {
				return;
			}
			var formattedData = [];
			for (var i = 0; i < data.length; i++) {
				formattedData.push({
					label : data[i],
					value : data[i]
				});
			}
			$(elementId).multiselect('dataprovider', formattedData);
			$(elementId).multiselect('rebuild');
		}

		function addDatasetFile(val) {
			if (!val.files || val.files.length === 0) {
				return;
			}
			var label = normalizeDisplayValue($("#nameDataset").val(), "Unnamed Dataset");
			var name = val.files[0].name;
			$("#answerFileDataset").append("<option value=\"AFDS_" + name + "\">" + label + "</option>");
			$("#answerFileDataset").multiselect('rebuild');
			$("#answerFileDataset").multiselect('refresh');
		}

		// Adds the given data (with tooltips) to the given (multi) select element. It is assumed that data is an array of objects each having a label, a name and a description.
		function addDataToSelectWithTooltips(elementId, data) {
			if ($(elementId).length === 0) {
				return;
			}
			var formattedData = [];
			for (var i = 0; i < data.length; i++) {
				formattedData.push({
					label : data[i].label,
					value : data[i].name
				});
			}
			$(elementId).multiselect('dataprovider', formattedData);
			$(elementId).multiselect('rebuild');
			$($(elementId).next().find('li')).each(function() {
				for (var i = 0; i < data.length; i++) {
					if (data[i].name == $(this).find('input').val()) {
						$(this).attr('data-toggle', 'tooltip').attr('data-placement', 'top').attr('title',
								data[i].description);
					}
				}
			});
			$('[data-toggle="tooltip"]').tooltip();
		}

		function getDatasetGroupName(dataset) {
			return normalizeDisplayValue(dataset.group, 'Others');
		}

		function getDatasetName(dataset) {
			return normalizeDisplayValue(dataset.name, 'Unnamed Dataset');
		}

		function addDatasetsToSelect(data) {
			var groupedDatasets = {};
			for (var i = 0; i < data.length; i++) {
				var groupName = getDatasetGroupName(data[i]);
				if (!groupedDatasets[groupName]) {
					groupedDatasets[groupName] = [];
				}
				groupedDatasets[groupName].push({
					label : getDatasetName(data[i]),
					value : getDatasetName(data[i])
				});
			}

			var sortedGroups = Object.keys(groupedDatasets).sort();
			var formattedData = [];
			for (var groupIndex = 0; groupIndex < sortedGroups.length; groupIndex++) {
				var currentGroup = sortedGroups[groupIndex];
				groupedDatasets[currentGroup].sort(function(left, right) {
					return left.label.localeCompare(right.label);
				});
				formattedData.push({
					label : currentGroup,
					children : groupedDatasets[currentGroup]
				});
			}

			$('#dataset').multiselect('dataprovider', formattedData);
			populateMissingOptionTexts('#dataset');
			$('#dataset').multiselect('rebuild');
			$('#dataset').multiselect('refresh');
			syncAnswerFileDatasetOptions();
			checkExperimentConfiguration();
		}

		// Keep the answer-file dataset selector aligned with the selected top-level datasets
		// while preserving uploaded dataset entries and a valid single selected value.
		function syncAnswerFileDatasetOptions() {
			var answerFileDataset = $('#answerFileDataset');
			var selectedValue = answerFileDataset.val();
			var nextSelectedValue = null;
			var selectedDatasets = [];
			var formattedData = [];
			var uploadedOptions = [];

			answerFileDataset.find('option').each(function() {
				if ($(this).val().indexOf('AFDS_') === 0) {
					uploadedOptions.push({
						value : $(this).val(),
						text : getSelectOptionLabel(this)
					});
				}
			});

			answerFileDataset.empty();

			$('#dataset option:selected').each(function() {
				selectedDatasets.push({
					value : $(this).val(),
					text : getSelectOptionLabel(this)
				});
			});

			for (var selectedDatasetIndex = 0; selectedDatasetIndex < selectedDatasets.length; selectedDatasetIndex++) {
				formattedData.push({
					label : selectedDatasets[selectedDatasetIndex].text,
					value : selectedDatasets[selectedDatasetIndex].value
				});
			}

			for (var i = 0; i < uploadedOptions.length; i++) {
				formattedData.push({
					label : uploadedOptions[i].text,
					value : uploadedOptions[i].value
				});
			}

			if (selectedValue && $.grep(formattedData, function(option) {
				return option.value === selectedValue;
			}).length > 0) {
				nextSelectedValue = selectedValue;
			} else if (selectedDatasets.length > 0) {
				nextSelectedValue = selectedDatasets[0].value;
			} else if (uploadedOptions.length > 0) {
				nextSelectedValue = uploadedOptions[0].value;
			}

			for (var optionIndex = 0; optionIndex < formattedData.length; optionIndex++) {
				formattedData[optionIndex].selected = (nextSelectedValue !== null)
						&& (formattedData[optionIndex].value === nextSelectedValue);
			}

			answerFileDataset.multiselect('dataprovider', formattedData);
			populateMissingOptionTexts('#answerFileDataset');
			if (nextSelectedValue !== null) {
				answerFileDataset.multiselect('select', nextSelectedValue, true);
			}
			answerFileDataset.multiselect('refresh');
		}

		function removeUploadedDatasetOption(datasetEntry) {
			var startPos = datasetEntry.lastIndexOf('(');
			var endPos = datasetEntry.lastIndexOf(')');
			if ((startPos < 0) || (endPos <= startPos)) {
				return;
			}
			var uploadedFileName = datasetEntry.substring(startPos + 1, endPos);
			$('#answerFileDataset').find('option').filter(function() {
				return $(this).val() === ('AFDS_' + uploadedFileName);
			}).remove();
		}
		//declaration of functions for loading experiment types, annotators, matchings and datasets
		function loadExperimentTypes() {
			$.getJSON('/gerbil/exptypes', {
				ajax : 'false'
			}, function(data) {
				addDataToSelectWithTooltips('#type', data.ExperimentType);
				adaptGuiForExperimentType();
				loadMatching();
				loadAnnotator();
				loadDatasets();
			});
		}
		function loadMatching() {
			$('#matching').html('');
			$('#annotator').html('');
			$.getJSON('/gerbil/matchings', {
				experimentType : $('#type').val(),
				ajax : 'false'
			}, function(data) {
				addDataToSelectWithTooltips('#matching', data.Matching);
			});
		}
		function loadAnnotator() {
			$('#annotator').html('');
			$.getJSON('/gerbil/annotators', {
				experimentType : $('#type').val(),
				ajax : 'false'
			}, function(data) {
				addDataToSelect('#annotator', data);
			});
		}
		function loadDatasets() {
			$('#dataset').html('');
			$.getJSON('/gerbil/datasets', {
				experimentType : $('#type').val(),
				ajax : 'false'
			}, function(data) {
				addDatasetsToSelect(data);
			});
		}
		// This function can be used to adapt the GUI for the chosen experiment type
		function adaptGuiForExperimentType() {
			if ($('#type').val() == "QA") {
				$("#uploadAnswersSeparator").show();
				$("#uploadAnswers").show();
				$("#matchingsSeparator").hide();
				$("#matchingsDiv").hide();
			} else {
				$("#matchingsSeparator").show();
				$("#matchingsDiv").show();
				$("#uploadAnswersSeparator").hide();
				$("#uploadAnswers").hide();
			}
		}

		function checkExperimentConfiguration() {
			// Get the number of selected and uploaded datasets
			var numberOfDataset = $('#dataset option:selected').length + $("#datasetList li span.li_content").length;
			// Get the number of selected systems, configured web services and uploaded answer files
			var numberOfSystems = $('#annotator option:selected').length
					+ $("#annotatorList li span.li_content").length
					+ $("#answerFileList li span.li_content").length;

			//check whether there is at least one dataset and at least one annotator or at least one answerFile
			//and the disclaimer checkbox should be clicked
			//if (((numberOfSystems > 0 && numberOfDataset > 0) || (numberOfAnswerFiles > 0))
			if (numberOfSystems > 0 && numberOfDataset > 0 && $('#disclaimerCheckbox:checked').length == 1) {
				$('#submit').attr("disabled", false);
			} else {
				$('#submit').attr("disabled", true);
			}
		}
		function addItemToList(listElement, item) {
			$(listElement)
					.append(
							"<li><span class=\"list-remove\" aria-hidden=\"true\">&times;</span>&nbsp;<span class=\"li_content\">"
									+ item + "</span></li>");
			var listItems = $(listElement).find('> li > span.list-remove');
			for (var i = 0; i < listItems.length; i++) {
				listItems[i].onclick = function() {
					var listEntryText = $(this).siblings('.li_content').text();
					if ($(listElement).attr('id') === 'datasetList') {
						removeUploadedDatasetOption(listEntryText);
					}
					this.parentNode.parentNode.removeChild(this.parentNode);
					syncAnswerFileDatasetOptions();
					checkExperimentConfiguration();
				};
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
				// If this is not a question answering web ervice 
				if ($('#type').val() != "QA") {
					$('#infoAnnotatorTest').show();
					$.getJSON('/gerbil/testNifWs', {
						experimentType : $('#type').val(),
						url : uri
					},
							function(data) {
								$('#infoAnnotatorTest').hide();
								if (data.testOk === true) {
									addItemToList('#annotatorList', name + "("
											+ uri + ")");
									$('#nameAnnotator').val('');
									$('#URIAnnotator').val('');
								} else {
									$('span#annotatorTestErrorMsg').text(
											data.errorMsg);
									$('#dangerAnnotatorTestError').show();
								}
							});
				} else {
					addItemToList('#annotatorList', name + "(" + uri + ")");
					$('#nameAnnotator').val('');
					$('#URIAnnotator').val('');
				}
			}
			//check showing run button if something is changed in dropdown menu
			checkExperimentConfiguration();
		}
		function addToList(list, array, prefix) {
			$(array).each(function() {
				var currentElement = $(this);
				var currentValue = '';
				if (currentElement.is('option')) {
					currentValue = normalizeDisplayValue(currentElement.val(), getSelectOptionLabel(currentElement));
				} else {
					currentValue = normalizeDisplayValue(currentElement.text(), currentElement.val());
				}
				if ((typeof prefix != 'undefined') && (prefix != "")) {
					list.push(prefix + currentValue);
				} else {
					list.push(currentValue);
				}
			});
		}
		var globalExperimentId = null;


		function performSubmit() {
			//fetch list of selected and manually added annotators
			var annotatorMultiselect = $('#annotator option:selected');
			var annotator = [];
			addToList(annotator, annotatorMultiselect);
			addToList(annotator, $("#annotatorList li span.li_content"),
					"NIFWS_");
			//fetch list of selected and manually added datasets
			var datasetMultiselect = $('#dataset option:selected');
			var dataset = [];
			addToList(dataset, datasetMultiselect);
			addToList(dataset, $("#datasetList li span.li_content"), "NIFDS_");
			var qLang = $("#qLang").val();
			var answerFiles = [];
			addToList(answerFiles, $("#answerFileList li span.li_content"),
					"AF_");
			var type = $('#type').val() ? $('#type').val() : "A2KB";
			var matching = $('#matching').val() ? $('#matching').val()
					: "Mw - weak annotation match";
			var data = {};
			data.type = type;
			data.matching = matching;
			data.annotator = annotator;
			data.dataset = dataset;
			data.answerFiles = answerFiles;
			data.questionLanguage = qLang;
			$
					.ajax('/gerbil/execute', {
						data : {
							'experimentData' : JSON.stringify(data)
						}
					})
					.done(
							function(data) {
								$('#submit').remove();
								var origin = window.location.origin;
								var link = "<a href=\"/gerbil/experiment?id="
										+ data + "\">" + origin
										+ "/gerbil/experiment?id=" + data
										+ "</a>";
								var span = "<span>Find your experimental data here: </span>";
								$('#submitField').append(span);
								$('#submitField').append(link);
							}).fail(function() {
						alert("The server reported an Error.");
					});
		}

		$(document).ready(function() {
			// load dropdowns when document loaded 
			initializeMultiselect('#type');
			initializeMultiselect('#matching');
			initializeMultiselect('#annotator');
			initializeMultiselect('#dataset', {
				enableCollapsibleOptGroups : true,
				onChange : function() {
					syncAnswerFileDatasetOptions();
					checkExperimentConfiguration();
				}
			});
			initializeMultiselect('#answerFileDataset');



			// listeners for dropdowns 
			$('#type').change(loadMatching);
			$('#type').change(loadAnnotator);
			$('#type').change(loadDatasets);
			$('#type').change(adaptGuiForExperimentType);

			loadExperimentTypes();

			//supervise configuration of experiment and let it only run
			//if everything is ok 
			//initially it is turned off 
			$('#submit').attr("disabled", true);
			//check showing run button if something is changed in dropdown menu
			$('#annotator').change(function() {
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
			//if system file add button is clicked check whether there is a name and a dataset
			$('#warningEmptyAnswerFileName').hide();
			$('#answerFileUpload').click(function() {
				var name = $('#nameAnswerFile').val();
				if (name == '') {
					$('#answerFileUpload').fileupload('disable');
					$('#warningEmptyAnswerFileName').show();
				} else {
					$('#answerFileUpload').fileupload('enable');
					$('#warningEmptyAnswerFileName').hide();
				}
			});
			//if dataset file add button is clicked check whether there is a name 
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
			$('#submit').click(performSubmit);
		});

		// define dataset file upload
		$(function() {
			'use strict';
			// Change this to the location of your server-side upload handler:
			var url = '/gerbil/file/upload';
			$('#fileupload').fileupload(
					{
						url : url,
						dataType : 'json',
						done : function(e, data) {
							var name = $('#nameDataset').val().replace("(", "%28").replace(")", "%29");
							$.each(data.result.files, function(index, file) {
							    file.name = file.name.replace("(", "%28").replace(")", "%29");
								addItemToList($('#datasetList'), name + "("
										+ file.name + ")");
								$('#nameDataset').val('');
								$('#URIDataset').val('');
							});
							syncAnswerFileDatasetOptions();
							checkExperimentConfiguration();
						},
						progressall : function(e, data) {
							var progress = parseInt(data.loaded / data.total
									* 100, 10);
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
		// define answer file upload
		$(function() {
			'use strict';
			// Change this to the location of your server-side upload handler:
			var url = '/gerbil/file/upload';
			$('#answerFileUpload').fileupload(
					{
						url : url,
						dataType : 'json',
						done : function(e, data) {
							var name = $('#nameAnswerFile').val();
							var dataset = ($('#answerFileDataset').val() || '').replace("(", "%28").replace(")", "%29");
						    var type = 'QALD JSON';
							$.each(data.result.files, function(index, file) {
							    file.name = file.name.replace("(", "%28").replace(")", "%29");
								addItemToList($('#answerFileList'), name + "("
										+ file.name + ")(" + type + ")("
										+ dataset + ")");
								$('#nameAnswerFile').val('');
							});
						},
						progressall : function(e, data) {
							var progress = parseInt(data.loaded / data.total
									* 100, 10);
							$('#answerFileProgress .progress-bar').css('width',
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
