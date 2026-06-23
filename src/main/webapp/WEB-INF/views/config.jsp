<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<head>
    <link rel="stylesheet"
          href="/gerbil/webjars/bootstrap/4.6.2/css/bootstrap.min.css">
    <%-- bootstrap-multiselect 1.1.2 serves its packaged assets from dist/ in the upgraded WebJar. --%>
    <link rel="stylesheet"
          href="/gerbil/webjars/bootstrap-multiselect/dist/css/bootstrap-multiselect.css"/>
    <link rel="icon" type="image/png"
          href="/gerbil/webResources/gerbilicon_transparent.png">
    <style type="text/css">
        /* making the buttons wide enough and right-aligned */
        .btn-group > .btn {
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

        #type > option {
            text-align: right;
        }

        /* These selects stay hidden because bootstrap-multiselect renders the visible replacement controls. */
        #type,
        #matching,
        #annotator,
        #dataset {
            display: none;
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

        /* These dataset-specific rules exist only to support the grouped flyout; the rest of the form now uses native Bootstrap 4 layout/classes. */
        #dataset + .btn-group .multiselect-container {
            max-height: 20rem;
            overflow: auto;
        }

        /* bootstrap-multiselect has optgroups but no second-level menu, so the flyout is attached to group headers. */
        #dataset + .btn-group .multiselect-option {
            display: none;
        }

        #dataset + .btn-group .multiselect-group {
            cursor: pointer;
            padding-right: 1.5rem;
            position: relative;
            white-space: nowrap;
        }

        #dataset + .btn-group .multiselect-group::after {
            content: "\203A";
            position: absolute;
            right: .75rem;
            top: 50%;
            transform: translateY(-50%);
            line-height: 1;
        }

        .gerbil-dataset-flyout {
            position: fixed;
            max-height: 20rem;
            overflow: auto;
            z-index: 1051;
        }

        #annotatorList,
        #datasetList {
            margin-top: 15px;
            list-style-type: none;
        }

        #addAnnotator {
            margin-top: 15px;
        }

        .gerbil-execution-warning {
            padding-top: 10px;
        }

        /* Fixes for IE < 8 */
        @media screen\9 {
            .fileinput-button input {
                filter: alpha(opacity=0);
                font-size: 100%;
                height: 100%;
            }
        }
    </style>
</head>
<c:url value="/file/upload" var="upload"/>
<body class="container">
<!-- mappings to URLs in back-end controller -->
<c:url var="annotators" value="/annotators"/>
<c:url var="matchings" value="/matchings"/>
<c:url var="exptypes" value="/exptypes"/>
<c:url var="datasets" value="/datasets"/>
<c:url var="execute" value="/execute"/>
<c:url var="testNifWs" value="/testNifWs"/>

<script src="/gerbil/webjars/jquery/3.7.1/jquery.min.js"></script>
<%-- Bootstrap 4 uses the bundle here so Popper-backed dropdowns/tooltips keep working after the library upgrade. --%>
<script src="/gerbil/webjars/bootstrap/4.6.2/js/bootstrap.bundle.min.js"></script>
<script
    src="/gerbil/webjars/bootstrap-multiselect/dist/js/bootstrap-multiselect.js"></script>
<c:url var="jquerywidget"
       value="/webResources/js/vendor/jquery.ui.widget.js"/>
<script src="${jquerywidget}"></script>
<c:url var="jqueryiframe"
       value="/webResources/js/jquery.iframe-transport.js"/>
<script src="${jqueryiframe}"></script>
<c:url var="jqueryfileupload"
       value="/webResources/js/jquery.fileupload.js"/>
<script src="${jqueryfileupload}"></script>
<%@include file="navbar.jsp" %>
<h1>GERBIL Experiment Configuration</h1>


<form id="configForm" class="form-horizontal">
    <fieldset>
        <!-- Form Name -->
        <legend>New Experiment</legend>
        <!-- Bootstrap 4 no longer ships the Bootstrap 3 horizontal-form helpers, so this form uses explicit grid rows/labels instead of reintroducing a broad compatibility block. -->
        <!-- experiment type dropdown filled by loadexptype() function -->
        <div class="form-group row">
            <label class="col-md-4 col-form-label text-md-right" for="type">Experiment
                Type</label>
            <div class="col-md-4">
                <select id="type">
                </select>
            </div>
        </div>
        <div class="row">
            <div class="col-md-8 offset-md-2">
                <hr/>
            </div>
        </div>
        <!--Matching dropdown filled by loadMatching() function -->
        <div class="form-group row">
            <label class="col-md-4 col-form-label text-md-right" for="matching">Matching</label>
            <div class="col-md-4">
                <select id="matching">
                </select>
            </div>
        </div>
        <div class="row">
            <div class="col-md-8 offset-md-2">
                <hr/>
            </div>
        </div>
        <!--Annotator dropdown filled by loadAnnotator() function -->
        <div class="form-group row">
            <label class="col-md-4 col-form-label text-md-right" for="annotator">Annotator</label>
            <div class="col-md-4">
                <select id="annotator" multiple="multiple">
                </select>
                <hr/>
                <div>
                    <span> Or add another webservice via URI:</span>
                    <div>
                        <label for="nameAnnotator">Name:</label> <input
                        class="form-control" type="text" id="nameAnnotator" name="name"
                        placeholder="Type something"/> <label for="URIAnnotator">URI:</label>
                        <input class="form-control" type="text" id="URIAnnotator"
                               name="URI" placeholder="Type something"/>
                    </div>
                    <div>
                        <!-- list to be filled by button press and javascript function addAnnotator -->
                        <ul id="annotatorList">
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
                           class="btn btn-primary float-right" value="Add another annotator"/>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-md-8 offset-md-2">
                <hr/>
            </div>
        </div>
        <!--Dataset dropdown filled by loadDatasets() function -->
        <div class="form-group row">
            <label class="col-md-4 col-form-label text-md-right" for="dataset">Dataset</label>
            <div class="col-md-4">
                <!-- Keep the native select as the source of truth; the grouped flyout is layered on top in JavaScript. -->
                <select id="dataset" multiple="multiple">
                </select>
                <hr/>
                <div>
                    <span> Or upload another dataset:</span>
                    <div>
                        <label for="nameDataset">Name:</label> <input
                        class="form-control" type="text" id="nameDataset" name="name"
                        placeholder="Type something"/> <br> <span
                        class="btn btn-success fileinput-button"> <span aria-hidden="true">+</span> <span>Select
									file...</span>
                        <!-- The file input field used as target for the file upload widget -->
								<input id="fileupload" type="file" name="files[]">
							</span> <br> <br>
                        <!-- The global progress bar -->
                        <div id="progress" class="progress">
                            <div class="progress-bar bg-success"></div>
                        </div>
                        <div>
                            <!-- list to be filled by button press and javascript function addDataset -->
                            <ul class="list-unstyled" id="datasetList">
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
            <div class="col-md-8 offset-md-2">
                <hr/>
            </div>
        </div>
        <div class="form-group row">
            <label class="col-md-4 col-form-label text-md-right" for="disclaimerCheckbox">Disclaimer</label>
            <div class="col-md-4">
                <div class="form-check">
                    <input id="disclaimerCheckbox" class="form-check-input" type="checkbox">
                    <label class="form-check-label" for="disclaimerCheckbox">
                    I have read and understand the <a
                        href="https://github.com/AKSW/gerbil/wiki/Disclaimer">disclaimer</a>.
                    </label>
                </div>
            </div>
        </div>
        <!-- Button -->
        <div class="form-group row">
            <label class="col-md-4 col-form-label" for="submit"></label>
            <div id="submitField" class="col-md-4">
                <input type="button" id="submit" name="singlebutton"
                       class="btn btn-primary" value="Run Experiment"/>
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
                    ajax: 'false'
                },
                function (data) {
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
                            function (index) {
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
                    experimentType: $('#type').val(),
                    ajax: 'false'
                },
                function (data) {
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
                            function (index) {
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
            experimentType: $('#type').val(),
            ajax: 'false'
        }, function (data) {
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
            // Rebuild changes the multiselect DOM without a user change event, so refresh the submit state explicitly.
            checkExperimentConfiguration();
        });
    }

    function loadDatasets() {
        $('#dataset').html('');
        hideDatasetSubmenus();
        $.getJSON('${datasets}', {
            experimentType: $('#type').val(),
            ajax: 'false'
        }, function (data) {
            rebuildDatasetOptions(data);
            $('#dataset').multiselect('rebuild');
            initializeDatasetSubmenus();
            // Loading a new dataset list can invalidate previous selections, so re-run the form validity check here.
            checkExperimentConfiguration();
        });
    }

    function checkExperimentConfiguration() {
        //fetch list of selected and manually added annotators
        var annotatorMultiselect = $('#annotator option:selected');
        var annotator = [];
        $(annotatorMultiselect).each(function (index, annotatorMultiselect) {
            annotator.push([$(this).val()]);
        });
        $("#annotatorList li span.li_content").each(function () {
            annotator.push($(this).text());
        });
        //fetch list of selected and manually added datasets
        // Dataset selections now come from the upgraded multiselect's underlying select instead of a custom checkbox DOM.
        var dataset = [];
        $('#dataset option:selected').each(function () {
            dataset.push($(this).val());
        });

        $("#datasetList li span.li_content").each(function () {
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
                        experimentType: $('#type').val(),
                        url: uri
                    },
                    function (data) {
                        $('#infoAnnotatorTest').hide();
                        if (data.testOk === true) {
                            $('#annotatorList')
                                .append(
                                    "<li><span aria-hidden=\"true\">&times;</span>&nbsp<span class=\"li_content\">"
                                    + name
                                    + "("
                                    + uri
                                    + ")</span></li>");
                            var listItems = $('#annotatorList > li > span');
                            for (var i = 0; i < listItems.length; i++) {
                                listItems[i].onclick = function () {
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

    function getDatasetFlyout() {
        var flyout = $('#gerbilDatasetFlyout');
        if (flyout.length > 0) {
            return flyout;
        }

        // Reuse one flyout element and keep its static look in CSS; only its runtime position is calculated in JavaScript.
        flyout = $('<div id="gerbilDatasetFlyout" class="dropdown-menu gerbil-dataset-flyout"></div>');
        flyout.on('mousedown.gerbilDatasetSubmenu click.gerbilDatasetSubmenu', function (event) {
            event.stopPropagation();
        });
        $('body').append(flyout);
        return flyout;
    }

    function hideDatasetSubmenus() {
        $('#gerbilDatasetFlyout').removeClass('show').hide().empty();
    }

    function rebuildDatasetOptions(data) {
        var groupedData = {};
        var groupName;
        for (var i = 0; i < data.length; i++) {
            groupName = data[i].group || 'Default';
            if (!groupedData[groupName]) {
                groupedData[groupName] = [];
            }
            groupedData[groupName].push(data[i]);
        }

        // Keep datasets in a native select/optgroup structure so bootstrap-multiselect owns the rendered menu while the underlying select remains the source of truth.
        var datasetSelect = $('#dataset');
        var sortedGroups = Object.keys(groupedData).sort();
        for (var groupIndex = 0; groupIndex < sortedGroups.length; groupIndex++) {
            var optgroup = $('<optgroup></optgroup>').attr('label', sortedGroups[groupIndex]);
            groupedData[sortedGroups[groupIndex]].sort(function (left, right) {
                return left.name.localeCompare(right.name);
            });

            for (var itemIndex = 0; itemIndex < groupedData[sortedGroups[groupIndex]].length; itemIndex++) {
                var item = groupedData[sortedGroups[groupIndex]][itemIndex];
                optgroup.append($('<option></option>')
                    .val(item.name)
                    .attr('title', item.name)
                    .text(item.name));
            }
            datasetSelect.append(optgroup);
        }
    }

    function showDatasetSubmenu(groupItem) {
        var groupIndex = parseInt(groupItem.attr('data-gerbil-group-index'), 10);
        var groupOptions = $('#dataset').children('optgroup').eq(groupIndex).children('option');
        if (isNaN(groupIndex) || groupOptions.length === 0) {
            hideDatasetSubmenus();
            return;
        }

        hideDatasetSubmenus();

        var flyout = getDatasetFlyout();
        flyout.empty();

        groupOptions.each(function (optionIndex) {
            var option = $(this);
            var optionId = 'gerbil-dataset-flyout-' + groupIndex + '-' + optionIndex;
            var optionItem = $('<label class="dropdown-item mb-0"></label>');
            var checkbox = $('<input type="checkbox" class="mr-2 gerbil-dataset-flyout-checkbox"/>');

            checkbox.attr('id', optionId)
                .prop('checked', option.is(':selected'))
                .on('change', function (event) {
                    event.stopPropagation();
                    option.prop('selected', $(this).prop('checked'));
                    $('#dataset').multiselect('refresh');
                    optionItem.toggleClass('active', option.is(':selected'));
                    $('#dataset').trigger('change');
                });
            optionItem.attr('for', optionId)
                .toggleClass('active', option.is(':selected'))
                .append(checkbox)
                .append(document.createTextNode(option.text()));
            flyout.append(optionItem);
        });

        flyout.css({
            display: 'block',
            visibility: 'hidden'
        }).addClass('show');

        var groupRect = groupItem[0].getBoundingClientRect();
        var viewportWidth = $(window).width();
        var viewportHeight = $(window).height();
        var flyoutHeight = flyout.outerHeight();
        var flyoutWidth = flyout.outerWidth();
        var flyoutLeft = groupRect.right + 2;
        var flyoutTop = Math.max(8, Math.min(groupRect.top, viewportHeight - flyoutHeight - 8));

        if (flyoutLeft + flyoutWidth > viewportWidth - 8) {
            flyoutLeft = Math.max(8, groupRect.left - flyoutWidth - 2);
        }

        // Position the submenu from the active group's viewport box because the flyout is rendered separately with fixed positioning.
        flyout.css({
            left: flyoutLeft + 'px',
            top: flyoutTop + 'px',
            visibility: 'visible'
        });
    }

    function initializeDatasetSubmenus() {
        var multiselect = $('#dataset').data('multiselect');
        if (!multiselect) {
            return;
        }

        // bootstrap-multiselect renders optgroups as flat headers, so the flyout rebuilds the grouped options beside them.
        var popupContainer = multiselect.$popupContainer;
        popupContainer.children('.multiselect-option').hide();
        popupContainer.children('.multiselect-group').attr('tabindex', '0')
            .each(function (index) {
                $(this).attr('data-gerbil-group-index', index);
            });

        popupContainer.off('.gerbilDatasetSubmenu');
        popupContainer.on('mouseenter.gerbilDatasetSubmenu click.gerbilDatasetSubmenu focusin.gerbilDatasetSubmenu',
            '.multiselect-group', function (event) {
                event.preventDefault();
                event.stopPropagation();
                showDatasetSubmenu($(this));
            });
        popupContainer.on('scroll.gerbilDatasetSubmenu', function () {
            hideDatasetSubmenus();
        });
        $(window).off('.gerbilDatasetSubmenu').on('resize.gerbilDatasetSubmenu scroll.gerbilDatasetSubmenu', function () {
            hideDatasetSubmenus();
        });
    }

    function datasetButtonText(options) {
        var selected = [];
        if (options.length === 0) {
            return 'Select Options';
        }
        options.each(function () {
            selected.push($(this).text());
        });
        return selected.join(', ');
    }

    $(document)
        .ready(
            function () {
                // Keep the upgraded Bootstrap 4 multiselects close to the previous right-aligned presentation without reviving the removed Bootstrap 3 form CSS.
                var bootstrap3MultiselectOptions = {
                    buttonClass: 'btn btn-light border',
                    buttonTextAlignment: 'right',
                    buttonWidth: '100%'
                };

                // load dropdowns when document loaded
                $('#type').multiselect($.extend({}, bootstrap3MultiselectOptions));
                $('#matching').multiselect($.extend({}, bootstrap3MultiselectOptions));
                $('#annotator').multiselect($.extend({}, bootstrap3MultiselectOptions));
                // The dataset control still uses the plugin for rendering, but the grouped flyout behavior is added separately below.
                $('#dataset').multiselect({
                    buttonClass: 'btn btn-light border',
                    buttonTextAlignment: 'left',
                    buttonWidth: '100%',
                    nonSelectedText: 'Select Options',
                    buttonText: function (options) {
                        return datasetButtonText(options);
                    },
                    buttonTitle: function (options) {
                        return datasetButtonText(options);
                    },
                    onDropdownShown: function () {
                        initializeDatasetSubmenus();
                    },
                    onDropdownHidden: function () {
                        hideDatasetSubmenus();
                    }
                });

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
                $('#annotator').change(function () {
                    checkExperimentConfiguration();
                });
                $('#dataset').change(function () {
                    checkExperimentConfiguration();
                });
                $('#disclaimerCheckbox').change(function () {
                    checkExperimentConfiguration();
                });

                //if add button is clicked check whether there is a name and a uri
                $('#warningEmptyAnnotator').hide();
                $('#infoAnnotatorTest').hide();
                $('#dangerAnnotatorTestError').hide();
                $('#addAnnotator').click(defineNIFAnnotator);
                //if add button is clicked check whether there is a name and a uri
                $('#warningEmptyDataset').hide();
                $('#fileupload').click(function () {
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
                        function () {
                            //fetch list of selected and manually added annotators
                            var annotatorMultiselect = $('#annotator option:selected');
                            var annotator = [];
                            $(annotatorMultiselect)
                                .each(
                                    function (index,
                                              annotatorMultiselect) {
                                        annotator
                                            .push($(
                                                this)
                                                .val());
                                    });
                            $(
                                "#annotatorList li span.li_content")
                                .each(
                                    function () {
                                        annotator
                                            .push("NIFWS_"
                                                + $(
                                                    this)
                                                    .text());
                                    });
                            //fetch list of selected and manually added datasets
                            // Read from the native select so submission stays aligned with the bootstrap-multiselect state after rebuild/refresh calls.
                            var dataset = [];
                            $('#dataset option:selected').each(function () {
                                dataset.push($(this).val());
                            });
                            $(
                                "#datasetList li span.li_content")
                                .each(
                                    function () {
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
                                        data: {
                                            'experimentData': JSON
                                                .stringify(data)
                                        }
                                    })
                                .done(
                                    function (data) {
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
                                    function (res) {
                                        $('#submit').remove();
                                        res = res.responseJSON;
                                        var errorSpan = '';
                                        if (res && res.experimentId) {
                                            var origin = window.location.origin;
                                            var link = "<a href=\"/gerbil/experiment?id="
                                                + res.experimentId
                                                + "\">"
                                                + origin
                                                + "/gerbil/experiment?id="
                                                + res.experimentId
                                                + "</a>";
                                            var span = "<span>Find your experimental data here: </span>";
                                            $('#submitField').append(span);
                                            $('#submitField').append(link);
                                            errorSpan = "<br><div class=\"warning gerbil-execution-warning\">Warning: " + res.errorMessage + "</div>";
                                        } else {
                                            errorSpan = "<br><div class=\"error\">Error: " + res.errorMessage + "</div>";
                                        }
                                        $('#submitField').append(errorSpan);
                                        // alert("Error, insufficient parameters.");
                                    });
                        });
            });

    // define file upload
    $(function () {
        'use strict';
        // Change this to the location of your server-side upload handler:
        var url = '${upload}';
        $('#fileupload')
            .fileupload(
                {
                    url: url,
                    dataType: 'json',
                    done: function (e, data) {
                        var name = $('#nameDataset').val();
                        $
                            .each(
                                data.result.files,
                                function (index, file) {
                                    $('#datasetList')
                                        .append(
                                            "<li><span aria-hidden=\"true\">&times;</span>&nbsp<span class=\"li_content\">"
                                            + name
                                            + "("
                                            + file.name
                                            + ") : "
                                            + file.description
                                            + "</span></li>");
                                    var listItems = $('#datasetList > li > span');
                                    for (var i = 0; i < listItems.length; i++) {
                                        listItems[i].onclick = function () {
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
                    progressall: function (e, data) {
                        var progress = parseInt(data.loaded
                            / data.total * 100, 10);
                        $('#progress .progress-bar').css('width',
                            progress + '%');
                    },
                    fail: function (e, data) {
                        data = data.response().jqXHR.responseJSON;
                        var name = $('#nameDataset').val();
                        $
                            .each(
                                data.files,
                                function (index, file) {
                                    $('#datasetList')
                                        .append(
                                            "<li><span aria-hidden=\"true\">!</span>&nbsp<span class=\"li_content\">"
                                            + name
                                            + "("
                                            + file.name
                                            + ") :"
                                            + file.error
                                            + "</span></li>");
                                    $('#nameDataset').val(
                                        '');
                                    $('#URIDataset')
                                        .val('');
                                });
                    }
                }).prop('disabled', !$.support.fileInput).parent()
            .addClass($.support.fileInput ? undefined : 'disabled');
    });
</script>
</body>
