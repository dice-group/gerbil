<%@page import="org.aksw.gerbil.web.ExperimentTaskStateHelper"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<head>
<link rel="stylesheet" href="webjars/bootstrap/3.2.0/css/bootstrap.min.css">
<link rel="stylesheet" href="webjars/bootstrap-multiselect/0.9.8/css/bootstrap-multiselect.css" />
<link rel="stylesheet" href="webResources/jquery.handsontable.css" />
</head>
<body class="container">
	<!-- mappings to URLs in back-end controller -->
	<script src="webjars/jquery/2.1.1/jquery.min.js"></script>
	<script src="webjars/bootstrap/3.2.0/js/bootstrap.min.js"></script>
	<script src="webjars/tablesorter/2.15.5/js/jquery.tablesorter.js"></script>
	<script src="webResources/jquery.handsontable.js"></script>
	<c:url var="experimentoverview" value="/experimentoverview" />
	<c:url var="matchings" value="/matchings" />
	<c:url var="exptypes" value="/exptypes" />
	
	<%@include file="navbar.jsp"%>
	<h1>GERBIL Experiment Overview</h1>
	<div class="form-horizontal">
		<div class="col-md-12">
			<div class="control-group">
				<label class="col-md-4 control-label">Experiment Type</label>
				<div id="expTypes" class="col-md-8"></div>
			</div>
		</div>
		
		<div class="col-md-12">
				<div class="control-group">
				<label class="col-md-4 control-label">Matching</label>
				<div id="matching" class="col-md-8"></div>
			</div>
		</div>
		<div class="col-md-12">
				<div class="control-group">
				<label class="col-md-4 control-label"></label>
				<div  class="col-md-8">
					 <button id="show" type="button" class="btn btn-default">Show table!</button>
				</div>
			</div>
		</div>
	</div>
	<div id="outputTable" class="handsontable"></div>
	<script type="text/javascript">
		$(document).ready(
				function() {
					//++++++++++++
					//creating the table
					//++++++++++++
					var $container = $("#outputTable");
					$container.handsontable({
						data : []
					});

					$container.handsontable('render'); //refresh the grid to display the new value
					var handsontable = $container.data('handsontable');
					var loadTable;
					//declaration of functions for loading experiment types, annotators, matchings and datasets  
					(loadTable = function() {
						$.getJSON(
								'${experimentoverview}',
								{
									experimentType : $('#expTypes input:checked').val(),
									matching : 	 $('#matching input:checked').val(),
									ajax : 'false'
								}, function(data) {
									handsontable.loadData(data);
									$container.handsontable('render');

								}).fail(function() {
							console.log("error");
						});
					});

					//++++++++++++
					//creating the radioboxes
					//++++++++++++
					var loadExperimentTypes;
					(loadExperimentTypes = function() {
						$.getJSON('${exptypes}', {
							ajax : 'false'
						}, function(data) {
							var htmlExperimentTypes = "";
							for ( var i = 0; i < data.length; i++) {
								htmlExperimentTypes += "<label class=\"btn btn-primary\" >";
								htmlExperimentTypes +=" <input class=\"toggle\" type=\"radio\" name=\"experimentType\" id=\""+ data[i] +"\" value=\""+ data[i]+"\" >"+ data[i];
								htmlExperimentTypes +="</label>";
							}
							$('#expTypes').html(htmlExperimentTypes);
							$('#expTypes input')[0].checked=true;
						});
					})();
					//++++++++++++
					//creating the radioboxes
					//++++++++++++
					var loadMatching;
					(loadMatching = function() {
						$.getJSON('${matchings}', {
						experimentType : $('#expTypes input').length!=0 ? $('#expTypes input:checked').val() : "D2W",
						ajax : 'false'
						}, function(data) {
							var htmlMatchings = "";
							for ( var i = 0; i < data.length; i++) {
								htmlMatchings += "<label class=\"btn btn-primary\" >";
								htmlMatchings +=" <input class=\"toggle\" type=\"radio\" name=\"matchingType\" id=\""+ data[i] +"\" value=\""+ data[i]+"\" >"+ data[i];
								htmlMatchings +="</label>";
							}
							$('#matching').html(htmlMatchings);
							$('#matching input')[0].checked=true;
						});
					})();
					
					$("#show").click(function(e){
						loadTable();
					});
				});
	</script>
</body>