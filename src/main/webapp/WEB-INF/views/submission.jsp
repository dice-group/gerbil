<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<head>
    <link rel="stylesheet"
          href="/gerbil/webjars/bootstrap/3.2.0/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css"
          href="webResources/css/dropdown.css">
    <link rel="stylesheet"
          href="/gerbil/webjars/bootstrap-multiselect/0.9.8/css/bootstrap-multiselect.css" />
    <link rel="icon" type="image/png"
          href="/gerbil/webResources/gerbilicon_transparent.png">
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-beta.1/dist/css/select2.min.css" rel="stylesheet" />

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
<c:url var="submit" value="/submit" />
<c:url var="submissionfile" value="/submissionfile/upload" />

<script src="/gerbil/webjars/jquery/2.1.1/jquery.min.js"></script>
<script src="/gerbil/webjars/bootstrap/3.2.0/js/bootstrap.min.js"></script>
<script src="/gerbil/webjars/bootstrap-multiselect/0.9.8/js/bootstrap-multiselect.js"></script>
<script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-beta.1/dist/js/select2.min.js"></script>
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
<h1>Submission</h1>


<form id="configForm" class="form-horizontal">
    <fieldset>
        <!-- Form Name -->
        <div class="form-group">
            <label class="col-md-4 control-label" for="type">Team Name</label>
            <div class="col-md-4">
                <input onchange="verify()" class="form-control" type="text" id="teamName" name="teamName" placeholder="Your Team Name" /> <br>
            </div>
        </div>
        <div class="form-group">
            <label class="col-md-4 control-label" for="type">Email</label>
            <div class="col-md-4">
                <input  onchange="verify()" class="form-control" type="text" id="email" name="email" placeholder="Your Email Address" /> <br>
            </div>
        </div>
        <div class="form-group">
            <label class="col-md-4 control-label" for="type">Experiment Task</label>
            <div class="col-md-4">
                <select  onchange="verify()" id="task">
                    <option disabled selected value> -- select an option -- </option>
                    <option value="rdf2textEN">RDF-to-Text (English)</option>
                    <option value="rdf2textRU">RDF-to-Text (Russian)</option>
                    <option value="text2rdfEN">Text-to-RDF (English)</option>
                    <option value="text2rdfRU">Text-to-RDF (Russian)</option>
                </select>
            </div>
        </div>
        <div class="row">
            <div class="col-md-8 col-md-offset-2">
                <hr />
            </div>
        </div>
        <div>
        <div class="form-group">
                    <label class="col-md-4 control-label" for="type">Upload your Zip File</label>
                    <div class="col-md-4">

                        <span class="btn btn-success fileinput-button">
                                <i class="glyphicon glyphicon-plus"></i>
                                <span>Select file...</span>
                                <!-- The file input field used as target for the source file upload widget -->
        								<input id="fileupload" accept=".zip,.bz2,.tar,.tar.gz,.gz,7z" type="file" name="file">
        						</span>
                                <br> <br>
                                <!-- The global progress bar -->
                                <div id="progress" class="progress">
                                    <div class="progress-bar progress-bar-success"></div>
                                </div>
                                <div>
                                   <!-- list to be filled by button press and javascript function addDataset -->
                                   <ul class="unstyled" id="fileList"
                                        tyle="margin-top: 15px; list-style-type: none;">
                                   </ul>
                                <div>
                                <div>
                                    <!-- list to be filled by button press and javascript function addDataset -->
                                    <ul class="unstyled" id="file"
                                        style="margin-top: 15px; list-style-type: none;">
                                    </ul>
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
        <div>
        <!-- Button -->
        <div class="form-group">
            <label class="col-md-4 control-label" for="submit"></label>
            <div id="submitField" class="col-md-4">
                <input type="button" id="submit" name="singlebutton"
                       class="btn btn-primary" value="Submit" />
            </div>
        </div>
    </fieldset>
</form>

<script>

    function verify(){
        var task = $('#task option:selected').val();
        var file = $('#zipFile').attr("value");
        var teamName = $("#teamName").val();
        var email = $("#email").val();
        if (task!="" && file !="" && teamName!="" && email!="") {
                    $('#submit').attr("disabled", false);
                } else {
                    $('#submit').attr("disabled", true);
                }
    }

    $(document)
        .ready(
            function() {
                $('#submit').attr("disabled", true);

                //submit button clicked will collect and sent experiment data to backend
                $('#submit')
                    .click(
                        function() {
                            //fetch list of selected and manually added datasets
                            var task = $('#task option:selected').val();
                            var file = $('#zipFile').attr("value");
                            var data = {};
                            data.teamName = $("#teamName").val();
                            data.email = $("#email").val();
                            data.task = task;
                            data.file = file;

                            $
                                .ajax(
                                    '${submit}',
                                    {
                                        data : {
                                            'submissionData' : JSON
                                                .stringify(data)
                                        }
                                    })
                                .done(
                                    function(data) {
                                        $('#submit')
                                            .remove();
                                        var origin = window.location.origin;

                                        $('#submitField')
                                            .append(data);
                                    })
                                .fail(
                                    function() {
                                        alert("Error, insufficient parameters.");
                                    });
                        });
                //submit button clicked will collect and sent experiment data to backend
            });



    // define dataset file upload
    $(function() {
        'use strict';
        // Change this to the location of your server-side upload handler:
        var url = '${submissionfile}';
        $('#fileupload')
            .fileupload(
                {
                    url : url,
                    dataType : 'json',
                    done : function(e, data) {
                        $
                            .each(
                                data.result.files,
                                function(index, file) {
                                    $('#fileList').empty();

                                    $('#fileList')
                                        .append(
                                            "<li><span class=\"glyphicon glyphicon-remove\"></span>&nbsp<span id=\"zipFile\" value=\""+file.name+"\"class=\"li_content\">"
                                            + file.name.substring(file.name.indexOf("_")+1)
                                            + "</span></li>");
                                    var listItems = $('#fileList > li > span');
                                    for (var i = 0; i < listItems.length; i++) {
                                        listItems[i].onclick = function() {
                                            this.parentNode.parentNode
                                                .removeChild(this.parentNode);
                                            $('#progress .progress-bar').css('width',
                                                                        0 + '%');
                                        };
                                    }
                                });
                        verify();
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