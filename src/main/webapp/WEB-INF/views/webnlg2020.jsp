<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<head>
<link rel="stylesheet"
	href="/gerbil/webjars/bootstrap/3.2.0/css/bootstrap.min.css">
<link rel="stylesheet"
	href="/gerbil/webjars/bootstrap-multiselect/0.9.8/css/bootstrap-multiselect.css" />
<link rel="icon" type="image/png" href="/gerbil/webResources/gerbilicon_transparent.png">

</head>
<body class="container">
	<%@include file="navbar.jsp"%>
	GERBIL-NLG will be used as a leaderbooard for the WebNLG2020 challenge.
	<h5>How to use GERBIL-NLG</h5>
	<ul>
		<li>NLG task
			<p> Here you simple upload your hypothesis and upload your reference file for your own experiments.
		<li>WebNLG RDF2Text
			<p> Here you evaluate your hypothesis on the WebNLG 2020 development set and this evaluation goes to the leaderboard.
				You simply upload your txt file.
		<li>WebNLG Text2RDF
			<p> Here you need to provide your annotation following the format provided by Chris at <a href="https://github.com/WebNLG/WebNLG-Text-to-triples">Parsing script</a>.
				Afterward, you will upload your results, this experiment is similar to NLG task in the sense of no leaderboard.
	</ul>
</body>
