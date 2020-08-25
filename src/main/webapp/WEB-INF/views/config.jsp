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
<!-- mappings to URLs in back-end controller -->
<c:url var="exptypes" value="/exptypes" />
<c:url var="datasets" value="/datasets" />
<c:url var="execute" value="/execute" />
<c:url var="testNifWs" value="/testNifWs" />

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

        <!--Upload hypothesis file from system -->
        <div class="form-group">
            <label class="col-md-4 control-label">System</label>
            <div class="col-md-4">
                <div id="uploadHypothesis">
                    <span> Upload file:</span>
                    <div>
                        <label for="nameHypothesisFile">Name:</label> <input
                            class="form-control" type="text" id="nameHypothesisFile" name="name"
                            placeholder="Type something" />
                        <br> <br> <span
                            class="btn btn-success fileinput-button"> <i
                            class="glyphicon glyphicon-plus"></i> <span>Select
									file...</span> <!-- The file input field used as target for the file upload widget -->
								<input id="hypothesisFileUpload" type="file" name="files[]">
							</span> <br> <br>
                        <!-- The global progress bar -->
                        <div id="hypothesisFileProgress" class="progress">
                            <div class="progress-bar progress-bar-success"></div>
                        </div>
                        <div>
                            <!-- list to be filled by button press-->
                            <ul class="unstyled" id="hypothesisFileList"
                                style="margin-top: 15px; list-style-type: none;">
                            </ul>

                        </div>
                        <div id="warningEmptyHypothesisFileName" class="alert alert-warning"
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

        <!--Dataset dropdown filled by loadDatasets() function -->
        <div class="form-group">
            <label class="col-md-4 control-label" for="datasets">Dataset</label>
            <div class="col-md-4">
                <div class="">

                    <span id="chosenDelomrade" style="font-weight:bold" ></span>
                    <div id="datasetMenu" class="dropdown datasetDropdown">
                        <select id="setdata" class="js-example-basic-multiple" name="datasets[]" style="width:100%"  multiple="multiple"></select>
                    </div>

                </div>
                <hr/>
                <div>
                    <span> Or upload reference:</span>
                    <div>
                        <label for="nameDataset">Name:</label> <input
                            class="form-control" type="text" id="nameDataset" name="name"
                            placeholder="Type something" /> <br> <span
                            class="btn btn-success fileinput-button"> <i
                            class="glyphicon glyphicon-plus"></i> <span>Select file...</span>
                        <!-- The file input field used as target for the source file upload widget -->
								<input id="fileupload" type="file" name="files[]">
							</span>
                        <br> <br>
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
            <div class="col-md-2"></div>
            <div class="col-md-2 text-right">
                <label class="control-label" >Language</label>
                <a title="(Beta) You can change the language which should be tested (default: en)

                 e.g. if you want to use Russian, type in: ru">
                    <span class="glyphicon glyphicon-question-sign"></span>
                </a>
            </div>
            <div class="col-md-4">

                <div>
                    <input
                            class="form-control" type="text" id="qLang" name="qlang"
                            placeholder="Type language: e.g. en or ru" /> <br>
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

                    loadDatasets();
                    loadMatching();
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
    function loadDatasets() {
        $(document).on('click', '.dropdown-submenu', function (e) {
            $(this).find('ul').toggle();
        });


        $('.datasetDropdown').on('click', function(e) {

        });

        function loadDatasetsMenu(){
            var formattedData = [];
            var n;
            $.getJSON('${datasets}', {
                experimentType : $('#type').val(),
                ajax : 'false'
            }, function(data) {

                for (var i = 0; i < data.length; i++) {
                    n = data[i].split("/",2);
                    var dat = {};
                    dat.label = n[0];
                    dat.value = n[1];
                    formattedData.push(dat);
                }
                const groupBy= key => array =>
                    array.reduce(
                        (objectsByKeyValue, obj) => ({
                            ...objectsByKeyValue,
                            [obj[key]]: (objectsByKeyValue[obj[key]] || []).concat(obj)
                        }),
                        {}
                    );
                const groupByLabel = groupBy('label');
                var arr =(groupByLabel(formattedData));
                var kes = Object.keys(arr);
                var val = Object.values(arr);
                showDropdownMenuHack(kes, arr);
                //console.log(arr)
                //console.log(kes + val);
            });
        };

        loadDatasetsMenu();

        function showDropdownMenuHack(datasets, language){

            $('#setdata').html("");

            var dropDownMenuStr = ''
            for (var i in datasets) {
                dropDownMenuStr += '<optgroup label="'+datasets[i]+'">';
                for( var x in language[datasets[i]]){
                    dropDownMenuStr += '<option value="'+datasets[i]+'/'+language[datasets[i]][x]["value"]+'">'+language[datasets[i]][x]["value"]+'</option>';
                }
                dropDownMenuStr+='</optgroup>';
            }
            dropDownMenuStr+='';
            $('#setdata').append(dropDownMenuStr);
            $('#setdata').select2({closeOnSelect: false});
            //awful, but works for now
            $('#datasetMenu div.btn-group').hide();
        }

        function showDropdownMenu(datasets, language) {

            $('#setdata').html("");
            var dropDownMenuStr = ""
            for (var i in datasets) {


                dropDownMenuStr += '<li class="dropdown-submenu"><a href="#">' + datasets[i] + '</a><ul class="dropdown-menu"> ';
                for( var x in language[datasets[i]]){
                    dropDownMenuStr += '<li> '+language[datasets[i]][x]["value"] +'<br>'+ '</a> </li>';
                }
                dropDownMenuStr+='</ul></li>';
            }
            $('#setdata').append(dropDownMenuStr);
        }
    }


    function singles(array) {
        for (var index = 0, single = []; index < array.length; index++) {
            if (array.indexOf(array[index], array.indexOf(array[index]) + 1) == -1)
                single.push(array[index]);
        };
        return single;
    };

    function checkExperimentConfiguration() {
        //fetch list of selected and manually added datasets
        var datasetMultiselect = $('#setdata option:selected');
        var dataset = [];
        $(datasetMultiselect).each(function(index, datasetMultiselect) {
            dataset.push([ $(this).val() ]);
        });
        $("#datasetList li span.li_content").each(function() {
            dataset.push($(this).text());
        });

        var hypothesis = [];
        $("#hypothesisFileList li span.li_content").each(function() {
            hypothesis.push($(this).text());
        });

        var candidate = [];
        $("#candidateFileList li span.li_content").each(function() {
            candidate.push($(this).text());
        });

        //check whether there is at least one dataset and at least one annotator
        //and the disclaimer checkbox should be clicked
        if (dataset.length > 0
            && $('#disclaimerCheckbox:checked').length == 1) {
            $('#submit').attr("disabled", false);
        } else {
            $('#submit').attr("disabled", true);
        }
    }

    $(document)
        .ready(
            function() {
                // load dropdowns when document loaded
                $('#type').multiselect();
                $('#setdata').multiselect();
                // listeners for dropdowns
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
                $('#setdata').change(function() {
                    checkExperimentConfiguration();
                });
                $('#disclaimerCheckbox').change(function() {
                    checkExperimentConfiguration();
                });
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
                //if hypothesis file add button is clicked check whether there is a name and a file
                $('#warningEmptyHypothesisFileName').hide();
                $('#hypothesisFileUpload').click(function() {
                    var name = $('#nameHypothesisFile').val();
                    if (name == '') {
                        $('#hypothesisFileUpload').fileupload('disable');
                        $('#warningEmptyHypothesisFileName').show();
                    } else {
                        $('#hypothesisFileUpload').fileupload('enable');
                        $('#warningEmptyHypothesisFileName').hide();
                    }
                });

                //if candidate file add button is clicked check whether there is a name and a file
                $('#warningEmptyCandidateFileName').hide();
                $('#candidateFileUpload').click(function() {
                    var name = $('#nameCandidateFile').val();
                    if (name == '') {
                        $('#candidateFileUpload').fileupload('disable');
                        $('#warningEmptycandidateFileName').show();
                    } else {
                        $('#candidateFileUpload').fileupload('enable');
                        $('#warningEmptycandidateFileName').hide();
                    }
                });

                //submit button clicked will collect and sent experiment data to backend
                $('#submit')
                    .click(
                        function() {
                            //fetch list of selected and manually added datasets
                            var qLang = $("#qLang").val();
                            var datasetMultiselect = $('#setdata option:selected');
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
                            var hypothesis = [];
                            $(
                                "#hypothesisFileList li span.li_content")
                                .each(
                                    function() {
                                        hypothesis
                                            .push("HF_"
                                                + $(
                                                    this)
                                                    .text());
                                    });
                            var candidate = [];
                            $(
                                "#candidateFileList li span.li_content")
                                .each(
                                    function() {
                                        candidate
                                            .push("HF_"
                                                + $(
                                                    this)
                                                    .text());
                                    });
                            var type = $('#type').val() ? $(
                                '#type').val()
                                : "MT";
                            var data = {};
                            data.type = type;
                            data.dataset = dataset;
                            data.hypothesis = hypothesis;
                            data.candidate = candidate;
                            data.language = qLang;

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
                //submit button clicked will collect and sent experiment data to backend
            });

    // define dataset file upload
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


    // define hypothesis file upload
    $(function() {
        'use strict';
        // Change this to the location of your server-side upload handler:
        var url = '${upload}';
        $('#hypothesisFileUpload')
            .fileupload(
                {
                    url : url,
                    dataType : 'json',
                    done : function(e, data) {
                        var name = $('#nameHypothesisFile').val();
                        $
                            .each(
                                data.result.files,
                                function(index, file) {
                                    $('#hypothesisFileList')
                                        .append(
                                            "<li><span class=\"glyphicon glyphicon-remove\"></span>&nbsp<span class=\"li_content\">"
                                            + name
                                            + "("
                                            + file.name
                                            + ")</span></li>");
                                    var listItems = $('#hypothesisFileList > li > span');
                                    for (var i = 0; i < listItems.length; i++) {
                                        listItems[i].onclick = function() {
                                            this.parentNode.parentNode
                                                .removeChild(this.parentNode);
                                            checkExperimentConfiguration();
                                        };
                                    }
                                    $('#nameHypothesisFile').val(
                                        '');
                                    $('#URIHypothesis')
                                        .val('');
                                });
                    },
                    progressall : function(e, data) {
                        var progress = parseInt(data.loaded / data.total
                            * 100, 10);
                        $('#hypothesisFileProgress .progress-bar').css('width',
                            progress + '%');
                    },
                    processfail : function(e, data) {
                        alert(data.files[data.index].name + "\n"
                            + data.files[data.index].error);
                    }
                }).prop('disabled', !$.support.fileInput).parent()
            .addClass($.support.fileInput ? undefined : 'disabled');
    });

    // define candidate file upload
    $(function() {
        'use strict';
        // Change this to the location of your server-side upload handler:
        var url = '${upload}';
        $('#candidateFileUpload')
            .fileupload(
                {
                    url : url,
                    dataType : 'json',
                    done : function(e, data) {
                        var name = $('#nameCandidateFile').val();
                        $
                            .each(
                                data.result.files,
                                function(index, file) {
                                    $('#candidateFileList')
                                        .append(
                                            "<li><span class=\"glyphicon glyphicon-remove\"></span>&nbsp<span class=\"li_content\">"
                                            + name
                                            + "("
                                            + file.name
                                            + ")</span></li>");
                                    var listItems = $('#candidateFileList > li > span');
                                    for (var i = 0; i < listItems.length; i++) {
                                        listItems[i].onclick = function() {
                                            this.parentNode.parentNode
                                                .removeChild(this.parentNode);
                                            checkExperimentConfiguration();
                                        };
                                    }
                                    $('#nameCandidateFile').val(
                                        '');
                                    $('#URICandidate')
                                        .val('');
                                });
                    },
                    progressall : function(e, data) {
                        var progress = parseInt(data.loaded / data.total
                            * 100, 10);
                        $('#candidateFileProgress .progress-bar').css('width',
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