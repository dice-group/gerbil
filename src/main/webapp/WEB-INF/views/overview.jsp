<%@page import="org.aksw.gerbil.web.ExperimentTaskStateHelper" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<head>
    <link rel="stylesheet" href="webjars/bootstrap/3.2.0/css/bootstrap.min.css">
</head>
<style>
table{
table-layout: fixed;
}

.table > thead > tr > th{
vertical-align:middle !important;
height:280px;
width: 30px !important;
} 
.rotated_cell div {
display:block;
transform: rotate(270deg);
-moz-transform: rotate(270deg);
-ms-transform: rotate(270deg);
-o-transform: rotate(270deg);
-webkit-transform: rotate(270deg);
width:250px;
position:relative;
top: -10;
left:-100;

}
</style>

<body >
 <div class="container">
    <!-- mappings to URLs in back-end controller -->
    <script src="webjars/jquery/2.1.1/jquery.min.js"></script>
    <script src="webjars/bootstrap/3.2.0/js/bootstrap.min.js"></script>
    <c:url var="experimentoverview" value="/experimentoverview" />
    <c:url var="matchings" value="/matchings" />
    <c:url var="exptypes" value="/exptypes" />

    <%@include file="navbar.jsp" %>
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
                    <div class="col-md-8">
                        <button id="show" type="button" class="btn btn-default">Show table!</button>
                    </div>
                </div>
            </div>
         
        </div>
        
        </div>
        
        <div class="container-fluid">
             <table id="outputTable" class="table table-hover table-condensed" >
                <thead></thead>
                <tbody></tbody>
             </table>
        </div>
        
        <script type="text/javascript">
            $(document)
                .ready(
                    function() {
                        //++++++++++++
                        //creating the table
                        //++++++++++++
                        var loadTable;
                        //declaration of functions for loading experiment types, annotators, matchings and datasets  
                        (loadTable = function() {
                            $.getJSON('${experimentoverview}', {
                                    experimentType: $('#expTypes input:checked').val(),
                                    matching: $('#matching input:checked').val(),
                                    ajax: 'false'
                                },
                                function(data) {
                                    //http://stackoverflow.com/questions/1051061/convert-json-array-to-an-html-table-in-jquery
                                    var tbl_body = "";
                                    var tbl_hd = "";

                                    $.each(data, function(i) {
                                        var tbl_row = "";
                                        if(i>0){
                                             $.each(this, function(k, v) {
                                                 tbl_row += "<td>" + v + "</td>";
                                             });
                                             tbl_body += "<tr>" + tbl_row + "</tr>";
                                        } else {
                                              $.each(this, function(k, v) {
                                                  tbl_row += "<th class=\"rotated_cell\"><div >" + v + "</div></th>";
                                              });
                                              tbl_hd += "<tr>" + tbl_row + "</tr>";
                                        }
                                    });
                                    $("#outputTable thead").html(tbl_hd);
                                    $("#outputTable tbody").html(tbl_body);

                                }).fail(function() {
                                console.log("error");
                            });
                        });

                        //++++++++++++
                        //creating the radioboxes
                        //++++++++++++
                        var loadExperimentTypes;
                        (loadExperimentTypes = function() {
                            $
                                .getJSON(
                                    '${exptypes}', {
                                        ajax: 'false'
                                    },
                                    function(data) {
                                        var htmlExperimentTypes = "";
                                        for (var i = 0; i < data.length; i++) {
                                            htmlExperimentTypes += "<label class=\"btn btn-primary\" >";
                                            htmlExperimentTypes += " <input class=\"toggle\" type=\"radio\" name=\"experimentType\" id=\"" + data[i] + "\" value=\"" + data[i] + "\" >" + data[i];
                                            htmlExperimentTypes += "</label>";
                                        }
                                        $('#expTypes').html(htmlExperimentTypes);
                                        $('#expTypes input')[0].checked = true;
                                    });
                        })();
                        //++++++++++++
                        //creating the radioboxes
                        //++++++++++++
                        var loadMatching;
                        (loadMatching = function() {
                            $
                                .getJSON(
                                    '${matchings}', {
                                        experimentType: $('#expTypes input').length != 0 ? $('#expTypes input:checked').val() : "D2W",
                                        ajax: 'false'
                                    },
                                    function(data) {
                                        var htmlMatchings = "";
                                        for (var i = 0; i < data.length; i++) {
                                            htmlMatchings += "<label class=\"btn btn-primary\" >";
                                            htmlMatchings += " <input class=\"toggle\" type=\"radio\" name=\"matchingType\" id=\"" + data[i] + "\" value=\"" + data[i] + "\" >" + data[i];
                                            htmlMatchings += "</label>";
                                        }
                                        $('#matching').html(htmlMatchings);
                                        $('#matching input')[0].checked = true;
                                    });
                        })();

                        $("#show").click(function(e) {
                            loadTable();
                        });
                    });
        </script>
</body>