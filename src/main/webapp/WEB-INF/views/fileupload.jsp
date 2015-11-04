<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<head>
<meta charset="utf-8">
<title>jQuery File Upload Example</title>
<link rel="icon" type="image/png" href="/gerbil/webResources/gerbilicon_transparent.png">

<head>
<c:url var="bootstrapmincss" value="/webjars/bootstrap/3.2.0/css/bootstrap.min.css"/>
<link rel="stylesheet"
	href="${bootstrapmincss}">
	
<c:url var="jquery" value="/webjars/jquery/2.1.1/jquery.min.js"/>	
<script	src="${jquery}"></script>
<c:url var="bootstrapjs" value="/webjars/bootstrap/3.2.0/js/bootstrap.min.js"/>
<script src="${bootstrapjs}"></script>
<c:url var="jquerywidget" value="/webResources/js/vendor/jquery.ui.widget.js"/>
<script	src="${jquerywidget}"></script>
<c:url var="jqueryiframe" value="/webResources/js/jquery.iframe-transport.js"/>
<script src="${jqueryiframe}"></script>
<c:url var="jqueryfileupload" value="/webResources/js/jquery.fileupload.js"/>
<script	src="${jqueryfileupload}"></script>

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
    filter: alpha(opacity=0);
    font-size: 100%;
    height: 100%;
  }
}
</style>
     <c:url value="/file/upload" var="upload"/>
<body>
   <br>
    <!-- The fileinput-button span is used to style the file input field as button -->
    <span class="btn btn-success fileinput-button">
        <i class="glyphicon glyphicon-plus"></i>
        <span>Select file...</span>
        <!-- The file input field used as target for the file upload widget -->
        <input id="fileupload" type="file" name="files[]" multiple>
    </span>
    <br>
    <br>
    <!-- The global progress bar -->
    <div id="progress" class="progress">
        <div class="progress-bar progress-bar-success"></div>
    </div>
    <!-- The container for the uploaded files -->
    <div id="files" class="files"></div>
    <br>
<script type="text/javascript">
/*jslint unparam: true */
/*global window, $ */
$(function () {
    'use strict';
    // Change this to the location of your server-side upload handler:
    var url = window.location.hostname === '${upload}';
    $('#fileupload').fileupload({
        url: url,
        dataType: 'json',
        done: function (e, data) {
            $.each(data.result.files, function (index, file) {
                $('<p/>').text(file.name).appendTo('#files');
            });
        },
        progressall: function (e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            $('#progress .progress-bar').css(
                'width',
                progress + '%'
            );
        }
    }).prop('disabled', !$.support.fileInput)
        .parent().addClass($.support.fileInput ? undefined : 'disabled');
});
</script>
</body> 