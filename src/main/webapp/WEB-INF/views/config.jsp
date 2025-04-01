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

.custom-multiselect-dropdown {
	position: relative;
	width: 100%;
}

.dropdown-btn {
	width: 100%;
	padding: 10px;
	border: 1px solid #ccc;
	background-color: #f9f9f9;
	cursor: pointer;
	text-align: left;
}

.dropdown-list {
	position: absolute;
	width: 100%;
	max-height: 200px;
	overflow-y: auto;
	border: 1px solid #ccc;
	background-color: #fff;
	display: none;
	z-index: 10;
}

.dropdown-list.active {
	display: block;
}

.dropdown-list .optgroup {
	font-weight: bold;
	padding: 8px;
	cursor: pointer;
}

.dropdown-list .optgroup-options {
	display: none;
	padding-left: 20px;
}

.dropdown-list .optgroup:hover .optgroup-options {
	display: block;
}

.dropdown-list label {
	display: block;
	padding: 5px;
	cursor: pointer;
}

.dropdown-list input[type="checkbox"] {
	margin-right: 5px;
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
				<div class="col-md-2"></div>
				<div class="col-md-2 text-right">
					<label class="control-label" for="type">Experiment
						Type</label>
					<a title="The QA experiment type will benchmark how good your system is at answering questions.">
							<span class="glyphicon glyphicon-question-sign"></span>
					</a>	
				</div>		
				
				<div class="col-md-4">
					<select id="type" style="display: none;">
					</select>
				</div>
				
			</div>
			<div class="row" id="matchingsSeparator">
				<div class="col-md-8 col-md-offset-2">
					<hr />
				</div>
			</div>
			<!--Matching dropdown filled by loadMatching() function -->
			<div class="form-group" id="matchingsDiv">
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
			<!--Dataset dropdown filled by loadDatasets() function -->
			<div class="form-group">
				<div class="col-md-2"></div>
					<div class="col-md-2 text-right">
						<label class="control-label" for="datasets">Dataset</label>
					<a title="You can
1) select multiple of the datasets from the drop-down menu or
2) upload a QALD-formatted JSON or XML containing your custom benchmark data.

(Beta) Further on you can change the language which should be tested (default: en)">
						<span class="glyphicon glyphicon-question-sign"></span>
					</a>
				</div>
				<div class="col-md-4">
					<div id="multiselect-container" class="custom-multiselect-dropdown"></div>


					<hr />
					<div>
						<span> Or upload another dataset:</span>
						<dv>
							<label for="nameDataset">Name:</label> <input
								class="form-control" type="text" id="nameDataset" name="name"
								placeholder="Type something" /> <br> <span
								class="btn btn-success fileinput-button"> <i
								class="glyphicon glyphicon-plus"></i> <span>Select
									file...</span> <!-- The file input field used as target for the file upload widget -->
								<input id="fileupload" type="file" name="files[]" onchange="addDatasetFile(this)">
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
            			<!--System dropdown filled by loadAnnotator() function -->
            			<div class="form-group">
            				<div class="col-md-2"></div>
            					<div class="col-md-2 text-right">
            						<label class="control-label" for="annotator">System</label>
            					<a title="You can
            1) select any of the system from the drop-down menu or
            2) add a system via its URI (needs to understand the query parameter and return valid QALD JSON or XML) or
            3) upload a QALD-formatted XML or JSON file which has the answers to one of the datasets. NOTE: First, type in the name of your system if you use option 2 or 3 and then type in the URI.">
            						<span class="glyphicon glyphicon-question-sign"></span>
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
            							class="btn btn-primary pull-right" value="Add system"
            							style="margin-top: 15px" /><br> <br>
            					</div>
            					<hr id="uploadAnswersSeparator" />
            					<div id="uploadAnswers">
            						<span> Or upload a file with answers:</span>
            						<div>
            							<label for="nameAnswerFile">Name:</label> <input
            								class="form-control" type="text" id="nameAnswerFile" name="name"
            								placeholder="Type something" />

            							 <br> <select id="answerFileDataset"
            								class="form-control">
            							</select> <br> <br> <span
            								class="btn btn-success fileinput-button"> <i
            								class="glyphicon glyphicon-plus"></i> <span>Select
            									file...</span> <!-- The file input field used as target for the file upload widget -->
            								<input id="answerFileUpload" type="file" name="files[]">
            							</span> <br> <br>
            							<!-- The global progress bar -->
            							<div id="answerFileProgress" class="progress">
            								<div class="progress-bar progress-bar-success"></div>
            							</div>
            							<div>
            								<!-- list to be filled by button press and javascript function addDataset -->
            								<ul class="unstyled" id="answerFileList"
            									style="margin-top: 15px; list-style-type: none;">
            								</ul>
            							</div>
            							<div id="warningEmptyAnswerFileName" class="alert alert-warning"
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
				<div class="col-md-2"></div>
					<div class="col-md-2 text-right">
						<label class="control-label" >Language</label>					
					<a title="(Beta) You can change the language which should be tested (default: en)
					
F.e. if you want to use French, type in: fr">
						<span class="glyphicon glyphicon-question-sign"></span>
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

		// Adds the given data to the given (multi) select element. It is assumed that data is an array of Strings that are used as label and value of the single options.
		function addDataToSelect(elementId, data) {
			var formattedData = [];
			for (var i = 0; i < data.length; i++) {
				var dat = {};
				dat.label = data[i];
				dat.value = data[i];
				formattedData.push(dat);
			}
			$(elementId).multiselect('dataprovider', formattedData);
			$(elementId).multiselect('rebuild');
		}

		function addDatasetFile(val){
		    var label = $("#nameDataset").val();
		    var name = val.files[0].name;
		    console.log(val)
		    $("#answerFileDataset").append("<option value=\"AFDS_" + name + "\">" + label + "</option>");
		    $("#answerFileDataset").multiselect('rebuild');

		}

		// Adds the given data (with tooltips) to the given (multi) select element. It is assumed that data is an array of objects each having a label, a name and a description.
		function addDataToSelectWithTooltips(elementId, data) {
			var formattedData = [];
			for (var i = 0; i < data.length; i++) {
				var dat = {};
				dat.label = data[i].label;
				dat.value = data[i].name;
				formattedData.push(dat);
			}
			$(elementId).multiselect('dataprovider', formattedData);
			$(elementId).multiselect('rebuild');
			// Add tooltips
			$($(elementId).next().find('li')).each(
					function(index) {
						for (var i = 0; i < data.length; i++) {
							if (data[i].name == $(this).find('input').val()) {
								$(this).attr('data-toggle', 'tooltip').attr(
										'data-placement', 'top').attr('title',
										data[i].description);
							}
						}
					});
			$('[data-toggle="tooltip"]').tooltip();
		}
		//declaration of functions for loading experiment types, annotators, matchings and datasets
		function loadExperimentTypes() {
			$.getJSON('${exptypes}', {
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
			$.getJSON('${matchings}', {
				experimentType : $('#type').val(),
				ajax : 'false'
			}, function(data) {
				addDataToSelectWithTooltips('#matching', data.Matching);
			});
		}
		function loadAnnotator() {
			$('#annotator').html('');
			$.getJSON('${annotators}', {
				experimentType : $('#type').val(),
				ajax : 'false'
			}, function(data) {
				addDataToSelect('#annotator', data);
			});
		}
		function loadDatasets() {
			$('#dataset').html('');
			$.getJSON('${datasets}', {
				experimentType : $('#type').val(),
				ajax : 'false'
			}, function(data) {
				createCustomMultiselect(data)
			});
		}
		// This function can be used to adapt the GUI for the chosen experiment type
		function adaptGuiForExperimentType() {
			console.log($('#type').val());
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
		function createCustomMultiselect(data) {
			const container = document.getElementById('multiselect-container');
			while (container.firstChild) {
				container.removeChild(container.firstChild);
			}

			const button = document.createElement('div');
			button.className = 'dropdown-btn';
			button.textContent = 'Select Options'; // Default text
			container.appendChild(button);

			const dropdownList = document.createElement('div');
			dropdownList.className = 'dropdown-list';

			const groupedData = {}; // Group datasets by their group property
			data.forEach(item => {
				const group = item.group || item.expType || 'Default'; // Use expType or group for grouping
				if (!groupedData[group]) {
					groupedData[group] = [];
				}
				groupedData[group].push(item);
			});

			const sortedGroups = Object.keys(groupedData).sort()

			sortedGroups.forEach(group => {
				const optgroupDiv = document.createElement('div');
				optgroupDiv.className = 'optgroup';
				optgroupDiv.textContent = group;
				const optionsContainer = document.createElement('div');
				optionsContainer.className = 'optgroup-options';

				groupedData[group].forEach(item => {
					const label = document.createElement('label');
					const checkbox = document.createElement('input');
					checkbox.type = 'checkbox';
					checkbox.value = item.name;

					// Set initial checked state from item if necessary
					checkbox.checked = item.selected || false;

					// Event listener for checkbox click
					checkbox.addEventListener('change', () => {
						item.selected = checkbox.checked;
						updateButtonText(button, data);
					});

					label.appendChild(checkbox);
					label.appendChild(document.createTextNode(item.name));
					optionsContainer.appendChild(label);
				});

				optgroupDiv.appendChild(optionsContainer);
				dropdownList.appendChild(optgroupDiv);
			});

			container.appendChild(dropdownList);

			button.addEventListener('click', () => {
				dropdownList.classList.toggle('active');
			});

			document.addEventListener('click', (event) => {
				if (!container.contains(event.target)) {
					dropdownList.classList.remove('active');
				}
			});
		}

		function createCustomMultiselectOLD(data) {
			const container = document.getElementById('multiselect-container');
			while (container.firstChild) {
				container.removeChild(container.firstChild);
			}

			const button = document.createElement('div');
			button.className = 'dropdown-btn';
			button.textContent = 'Select Options';
			container.appendChild(button);

			const dropdownList = document.createElement('div');
			dropdownList.className = 'dropdown-list';

			const groupedData = {};
			data.forEach(item => {
				const group = item.group || 'Default';
				if (!groupedData[group]) {
					groupedData[group] = [];
				}
				groupedData[group].push(item);
			});

			console.log("aaakahs JS")
			console.log(groupedData)

			Object.keys(groupedData).forEach(group => {
				const optgroupDiv = document.createElement('div');
				optgroupDiv.className = 'optgroup';
				optgroupDiv.textContent = group;
				const optionsContainer = document.createElement('div');
				optionsContainer.className = 'optgroup-options';

				groupedData[group].forEach(item => {
					const label = document.createElement('label');
					const checkbox = document.createElement('input');
					checkbox.type = 'checkbox';
					checkbox.value = item.name;

					checkbox.checked = item.selected || false;

					checkbox.addEventListener('change', () => {
						item.selected = checkbox.checked;
						updateButtonText(button, data);
					});

					label.appendChild(checkbox);
					label.appendChild(document.createTextNode(item.name));
					optionsContainer.appendChild(label);
				});

				optgroupDiv.appendChild(optionsContainer);
				dropdownList.appendChild(optgroupDiv);
			});

			container.appendChild(dropdownList);

			button.addEventListener('click', () => {
				dropdownList.classList.toggle('active');
			});

			document.addEventListener('click', (event) => {
				if (!container.contains(event.target)) {
					dropdownList.classList.remove('active');
				}
			});
		}

		function updateButtonText(button, data) {
			const selectedItems = data.filter(item => item.selected);
			if (selectedItems.length > 0) {
				const selectedNames = selectedItems.map(item => item.name).join(', ');
				button.textContent = selectedNames;
			} else {
				button.textContent = 'Select Options';
			}
		}

		function checkExperimentConfiguration() {
			var dataset = [];
			const container = document.getElementById('multiselect-container');
			container.querySelectorAll('.dropdown-list input[type="checkbox"]:checked').forEach((checkbox) => {
				dataset.push(checkbox.value);
			});
			var numberOfSystems = $('#annotator option:selected').length
					+ $("#annotatorList li span.li_content").length;
			var numberOfDataset = dataset.length;
			var numberOfAnswerFiles = $("#answerFileList li span.li_content").length;

			//check whether there is at least one dataset and at least one annotator or at least one answerFile
			//and the disclaimer checkbox should be clicked
			if (((numberOfSystems > 0 && numberOfDataset > 0) || (numberOfAnswerFiles > 0))
					&& $('#disclaimerCheckbox:checked').length == 1) {
				$('#submit').attr("disabled", false);
			} else {
				$('#submit').attr("disabled", true);
			}
		}
		function addItemToList(listElement, item) {
			$(listElement)
					.append(
							"<li><span class=\"glyphicon glyphicon-remove\"></span>&nbsp<span class=\"li_content\">"
									+ item + "</span></li>");
			var listItems = $(listElement, ' > li > span');
			for (var i = 0; i < listItems.length; i++) {
				listItems[i].onclick = function() {
					this.parentNode.parentNode.removeChild(this.parentNode);
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
					$.getJSON('${testNifWs}', {
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
		// Adds the values of the elements from the given array to the given list
		function addToList(list, array) {
			$(array).each(function() {
				list.push($(this).val());
			});
		}
		// Adds the text of the elements from the given array to the given list adding the given prefix 
		function addToList(list, array, prefix) {
			$(array).each(function() {
				if ((typeof prefix != 'undefined') && (prefix != "")) {
					list.push(prefix + $(this).text());
				} else {
					list.push($(this).text());
				}
			});
		}

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
			const container = document.getElementById('multiselect-container');
			container.querySelectorAll('.dropdown-list input[type="checkbox"]:checked').forEach((checkbox) => {
				dataset.push(checkbox.value);
			});
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
					.ajax('${execute}', {
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
			$('#type').multiselect();
			$('#matching').multiselect();
			$('#annotator').multiselect();
			$('#dataset').multiselect();

			// listeners for dropdowns 
			$('#type').change(loadMatching);
			$('#type').change(loadAnnotator);
			$('#type').change(loadDatasets);
			$('#type').change(adaptGuiForExperimentType);

			loadExperimentTypes();

			addDataToSelect('#answerFileType', [ 'QALD JSON', 'QALD XML' ]);

			//supervise configuration of experiment and let it only run
			//if everything is ok 
			//initially it is turned off 
			$('#submit').attr("disabled", true);
			//check showing run button if something is changed in dropdown menu
			$('#annotator').change(function() {
				checkExperimentConfiguration();
			});
			$('#multiselect-container').change(function() {
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
			var url = '${upload}';
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
			var url = '${upload}';
			$('#answerFileUpload').fileupload(
					{
						url : url,
						dataType : 'json',
						done : function(e, data) {
							var name = $('#nameAnswerFile').val();
							var dataset = $('#answerFileDataset').val().replace("(", "%28").replace(")", "%29");
							console.log(dataset);console.log(dataset);
						    var type = $('#answerFileType').val();
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