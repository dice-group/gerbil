<%@page import="org.aksw.gerbil.web.ExperimentTaskStateHelper"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<head>
	<link rel="stylesheet"
		  href="/gerbil/webjars/bootstrap/3.2.0/css/bootstrap.min.css">
	<title>Overview</title>
	<script type="text/javascript"
			src="/gerbil/webjars/jquery/2.1.1/jquery.min.js"></script>

	<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css"
		  type="text/css" media="all"/>
	<script type="text/javascript"
			src="/gerbil/webjars/bootstrap/3.2.0/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="http://d3js.org/d3.v3.min.js"></script>
	<script type="text/javascript"
			src="/gerbil/webResources/js/gerbil.color.js"></script>
	<script type="text/javascript"
			src="/gerbil/webResources/js/RadarChart.js"></script>
	<script type="text/javascript"
			src="/gerbil/webResources/js/script_radar_overview.js"></script>
	<script type="text/javascript" src="/gerbil/webResources/js/Chart.js"></script>
	<link rel="icon" type="image/png"
		  href="/gerbil/webResources/gerbilicon_transparent.png">
	<link rel="stylesheet" href="/gerbil/webResources/css/slider.css"/>
	<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.9.0/css/all.css">
	<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.9.0/css/v4-shims.css">
	<script src="https://use.fontawesome.com/559bb5497a.js"></script>
</head>
<style>
	table {
		table-layout: fixed;
	}

	.table>thead>tr>th {
		vertical-align: middle !important;
		height: 280px;
		width: 20px !important;
	}

	.rotated_cell div {
		display: block;
		transform: rotate(270deg);
		-moz-transform: rotate(270deg);
		-ms-transform: rotate(270deg);
		-o-transform: rotate(270deg);
		-webkit-transform: rotate(270deg);
		width: 250px;
		position: relative;
		top: -10;
		left: -100;
	}

	.col-md-12 {
		padding: 5px 0px;
	}

	.chartDiv { /*position: absolute;*/
		top: 50px;
		left: 100px;
		vertical-align: center;
		text-align: center;
	}

	.chartBody {
		overflow: hidden;
		margin: 0;
		font-size: 14px;
		font-family: "Helvetica Neue", Helvetica;
	}


</style>

<body>
<div class="container">
	<!-- mappings to URLs in back-end controller -->
	<c:url var="experimentoverview" value="/experimentoverview" />
	<c:url var="exptypes" value="/exptypes" />



	<%@include file="navbar.jsp"%>
	<h1>Leaderboards</h1>
	<div class="form-horizontal">
		<div class="col-md-12">
			<div class="control-group">
				<label class="col-md-4 control-label">Task</label>
				<div id="expTypes" class="col-md-8"></div>
			</div>
		</div>
		<div class="col-md-12">
			<div class="control-group">
				<label class="col-md-4 control-label">Show Archive</label>
				<div class="col-md-8">
					<label class="switch">
						<input onchange="toggleArchive()" value="false" id="sliderArchive" type="checkbox">
						<span class="slider round"></span>
					</label>
				</div>

			</div>
		</div>
		<div class="col-md-12" style="visibility: hidden;">
			<div class="control-group">
				<label class="col-md-4 control-label">Matching</label>
				<div id="matching" class="col-md-8"></div>
			</div>
		</div>

		<div class="col-md-12">
			<div class="control-group">
				<label class="col-md-4 control-label"></label>
				<div class="col-md-8">
					<button id="show" type="button" class="btn btn-default">Show
						leaderboard!</button>
				</div>

			</div>
		</div>

		<!-- <div class="col-md-12">
            <h2>Leaderboards</h2>
            <p>The tables will show the ranking of current participants.</p>
        </div>
         <div class="container-fluid"> -->
		<div class="col-md-12">
			<div id="resultsChartBody" class="chartBody">
				<div id="resultsChart" class="chartDiv"></div>
			</div>
		</div>
	</div>
</div>
<div class="container-fluid" id="resultsTable">
	<!-- 		<table id="resultsTable" class="table table-hover table-condensed"> -->
	<!-- 			<thead></thead> -->
	<!-- 			<tbody></tbody> -->
	<!-- 		</table> -->

</div>

<div class="container-fluid" id="archiveTable">
	<!-- 		<table id="resultsTable" class="table table-hover table-condensed"> -->
	<!-- 			<thead></thead> -->
	<!-- 			<tbody></tbody> -->
	<!-- 		</table> -->

</div>
<div class="container" style="visibility: hidden;">
	<div class="form-horizontal">
		<div class="col-md-12">
			<h2>Annotator &ndash; Dataset feature correlations</h2>
			<p>The table as well as the diagram contain the pearson
				correlations between the annotators and the dataset features. Note
				that the diagram only shows the absolute values of the correlation.
				For the correlation type, you should take a look at the table.</p>
		</div>
		<!-- <div class="container-fluid"> -->
		<div class="col-md-12">
			<div id="correlationsChartBody" class="chartBody">
				<div id="correlationsChart" class="chartDiv"></div>
			</div>
		</div>
	</div>
</div>
<div class="container-fluid" style="visibility: hidden;">
	<table id="correlationsTable"
		   class="table table-hover table-condensed">
		<thead></thead>
		<tbody></tbody>
	</table>
</div>

<script type="text/javascript">
	var archive = false;
	//initLoading();
	var archiveIsLoaded =false;

	String.prototype.hashCode = function() {
		var hash = 0, i, chr;
		if (this.length === 0) return hash;
		for (i = 0; i < this.length; i++) {
			chr   = this.charCodeAt(i);
			hash  = ((hash << 5) - hash) + chr;
			hash |= 0; // Convert to 32bit integer
		}
		return hash;
	};



	function loadExperimentTypes() {
		$
				.getJSON(
						'${exptypes}',
						{
							ajax : 'false'
						},
						function(data) {
							console.log(data);
							var htmlExperimentTypes = "";
							for (var i = 0; i < data.ExperimentType.length; i++) {
								htmlExperimentTypes += "<label class=\"btn btn-primary\" >";
								htmlExperimentTypes += " <input class=\"toggle\" type=\"radio\" name=\"experimentType\" id=\"" + data.ExperimentType[i].name + "\" value=\"" + data.ExperimentType[i].name + "\" >"
										+ data.ExperimentType[i].label;
								htmlExperimentTypes += "</label>";
							}
							$('#expTypes').html(htmlExperimentTypes);
							$('#expTypes input')[0].checked = true;
							// Add the listener for loading the matchings
							//$("#expTypes input").change(loadMatchings);
							//loadMatchings();

							$('#expTypes label')
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

						}).done(function(){
			initLoading();

		});

	};

	function loadTables() {

		$.getJSON('${experimentoverview}', {
			experimentType : $('#expTypes input:checked').val(),
			ajax : 'false'
		}, function(data) {
			console.log(data)
			$("#resultsTable").html("");
			for (var i = 0; i < data.datasets.length; i++) {
				var tableData = data.datasets[i];
				var experimentType = $('#expTypes input:checked').val();
				showTable(tableData, "resultsTable", experimentType, null);
				console.log(data)
				console.log(tableData.valueOf())
			}
		}).fail(function(data) {
			console.log("error loading data for table");

		});
	};

	function loadArchive() {
		if(archiveIsLoaded === true){
			//just show cached results
			return;
		}
		$.getJSON('experimentarchive', {
			ajax : 'false'
		}, function(data) {
			console.log(data)
			$("#archiveTable").html("");
			for(var i = 0; i < data.archive.length; i++){
				var challenge = data.archive[i].challenge;
				var etypes = data.archive[i].results;
				var challengeId = challenge.name.concat(challenge.start).concat(challenge.end).hashCode();
				var cempty= true;
				for(var j = 0; j < etypes.length; j++){
					var type = etypes[j].type;
					var results = etypes[j].results;
					var dempty = true;
					for (var k = 0; k < results.datasets.length; k++) {
						var tableData = results.datasets[k];
						if(tableData.leader.length > 0){
							dempty = false;
							cempty=false;
							showTable(tableData, "archiveTable", type, challengeId);
							console.log(tableData.valueOf() + type + challengeId)
						}
					}
					if(!dempty){
						$("#archiveTable").prepend("<div class=\"col-md-12\"><h2>Type: "+type+"</h2></div>");
					}
				}
				if(!cempty){
					$("#archiveTable").prepend("<div class=\"col-md-12\"><h1>"+challenge.name+"</h1><p>(from "+challenge.startDate+" to "+challenge.endDate+")"+"</p></div>");
				}
			}

			archiveIsLoaded = true;
			window.location.hash = window.location.hash;
		}).fail(function(data) {
			console.log("error loading archive");

		});


	};

	function initLoading() {
		var param = getUrlParameter("task");

		if (param != null) {
			$('#'+param).prop('checked', true);


			$.getJSON('${experimentoverview}', {
				experimentType : param,
				ajax : 'false'
			}, function(data) {
				$("#resultsTable").html("");
				for (var i = 0; i < data.datasets.length; i++) {
					var tableData = data.datasets[i];
					showTable(tableData, "resultsTable");
				}
				window.location.hash = window.location.hash;

			}).fail(function() {
				console.log("error loading data for table");

			});
		}
		var isArchive = getUrlParameter("archive");
		if(isArchive === 'true'){
			toggleArchive();
			$('#sliderArchive').val(true);
			$('#sliderArchive').prop('checked', true);

		}



	}

	function toggleArchive(){
		archive = !archive;
		$('#sliderArchive').prop('checked', archive);
		$('#sliderArchive').val(archive);
		//TODO show set archive param
		if(archive){

			loadArchive();
			$('#expTypes *').attr("disabled", "disabled").off('click');
			$('#resultsTable ').hide();
			$('#archiveTable').show();

		}
		else {
			$('#expTypes *').removeAttr("disabled").on('click');
			$('#resultsTable ').show();
			$('#archiveTable').hide();
		}
	}

	function getUrlParameter(sParam) {
		var sPageURL = decodeURIComponent(window.location.search
				.substring(1)), sURLVariables = sPageURL.split('&'), sParameterName, i;

		for (i = 0; i < sURLVariables.length; i++) {
			sParameterName = sURLVariables[i].split('=');

			if (sParameterName[0] === sParam) {
				return sParameterName[1] === undefined ? true
						: sParameterName[1];
			}
		}
	};

	function showTable(tableData, tableElementId, experimentType, challengeId) {
		//http://stackoverflow.com/questions/1051061/convert-json-array-to-an-html-table-in-jquery
		var cid = "";
		var challengePre ="";
		if(challengeId != null){
			challengePre = challengeId
			cid = "&cid="+challengeId;
		}
		var str = challengePre+experimentType+tableData.datasetName.replace(/\s/g, "").replace(/[^a-z0-9]/gi,'z');
		var newID = str + "bod";
		var bootDiv = "<div id=\"" + newID + "\" class=\"col-md-12\"></div>";
		$("#" + tableElementId).prepend(bootDiv);

		var tbl_body = "";

		var measures = [];
		var tbl_hd = "<tr><th>System</th>"
		for (var i = 0; i < Object.keys(tableData.leader[0].value).length; i++) {
			measures = Object.keys(tableData.leader[0].value)
			tbl_hd += "<th>"+measures[i]+"</th>"
		}
		tbl_hd += "</tr>";

		var path = "?archive="+archive+cid;
		if(!archive){
			path +="&task="+experimentType;
		}
		path+="#name"+ challengePre + tableData.datasetName.replace(/[^a-z0-9]/gi,'z');

		var tbl = "<h3 id=\"name" + challengePre + tableData.datasetName.replace(/[^a-z0-9]/gi,'z') + "\"><a title=\""+window.location.host+window.location.pathname+path+"\" class=\"reflink\" href=\"overview.html"+path+"\"><i class=\"fas fa-link\"></i></a> Dataset: "
				+ tableData.datasetName
				+ "</h3><table id=\"" + tableData.datasetName.replace(/[^a-z0-9]/gi,'z') + "\" class=\"table table-hover table-condensed\">";
		tbl += tbl_hd;
		var leader = tableData.leader;
		var url = window.location.protocol +"//"+ window.location.host + "/gerbil/experiment?id=";

		for (var i = 0; i < leader.length; i++) {
			//TODO add elements from tableData
			var tbl_row = "<tr>";

			tbl_row += "<td> <a title=\""+url+leader[i].id+"\" href=\""+url+leader[i].id+"\"> <span class=\"glyphicon glyphicon-search\"></span></a> "
					+ leader[i].annotatorName + " </td>";
			values = Object.values(tableData.leader[i].value)
			values.forEach((val) => {
				tbl_row += "<td>"+val+ "</td>"
			});
			tbl_row += "</tr>";
			tbl_body += tbl_row;
		}
		;
		tbl += tbl_body;
		tbl += "</table>";
		$("#" + newID).prepend("<div class=\"col-md-20\">" + tbl + "</div>");
	}



	function drawSpiderDiagram(tableData, chartElementId) {
		//draw spider chart
		var chartData = [];
		//Legend titles  ['Smartphone','Tablet'];
		var LegendOptions = [];
		$.each(tableData, function(i) {
			//iterate over rows
			if (i > 0) {
				var annotatorResults = [];
				$.each(this, function(k, v) {
					if (k == 0) {
						//annotator
						LegendOptions.push(v);
					} else {
						//results like {axis:"Email",value:0.71},
						var tmp = {};
						tmp.axis = tableData[0][k];
						if (v == "n.a." || v.indexOf("error") > -1) {
							tmp.value = 0;
						} else {
							// if the number is negative make it poositive
							if (v.indexOf("-") > -1) {
								v = v.replace("-", "+");
							}
							tmp.value = v;
						}
						annotatorResults.push(tmp);
					}
				});
				chartData.push(annotatorResults);
			}
		});
		//[[{axis:"Email",value:0.71},{axis:"aa",value:0}],[{axis:"Email",value:0.71},{axis:"aa",value:0.1},]];
		console.log("start drawing into " + chartElementId);
		drawChart(chartData, LegendOptions, chartElementId);
		// add the svg namespace
		//$('#' + chartElementId + ' svg').attr("xmlns:svg",
		//		"http://www.w3.org/2000/svg").attr("xmlns",
		//		"http://www.w3.org/2000/svg");
	}

	$(document).ready(function() {
		//++++++++++++
		//creating the radioboxes
		//++++++++++++
		loadExperimentTypes();



		$("#show").click(function(e) {
			loadTables();
		});

	});


</script>
</body>