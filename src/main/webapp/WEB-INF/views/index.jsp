<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<head>
<link rel='stylesheet'
	href='/gerbil/webjars/bootstrap/3.2.0/css/bootstrap.min.css'>
<script src="/gerbil/webjars/jquery/2.1.1/jquery.min.js"></script>
<script src="/gerbil/webjars/bootstrap/3.2.0/js/bootstrap.min.js"></script>
<link rel="icon" type="image/png"
	href="/gerbil/webResources/gerbilicon_transparent.png">
<style>
div.picture {
	background-color: #fff;
	border-radius: 6px;
}
</style>
</head>
<body class="container">
	<%@include file="navbar.jsp"%>
	<div class="jumbotron">
		<div class="container">
			<h1>GERBIL - General Entity Annotator Benchmark</h1>
			<p>This is GERBIL. All your peanuts belong to me!</p>
			<p>We present GERBIL, a general entity annotation system. GERBIL
				offers an easy-to-use web-based platform for the agile comparison of
				annotators using multiple datasets and uniform measuring approaches.
				To add a tool to GERBIL, all the end user has to do is to provide a
				URL to a REST interface to its tool which abides by a given
				specification. The integration and benchmarking of the tool against
				user-specified datasets is then carried out automatically by the
				GERBIL platform. Currently, our platform provides results for 9
				annotators and 11 datasets with more coming. Internally, GERBIL is
				based on the Natural Language Programming Interchange Format (NIF)
				and provide Java classes for implementing APIs for datasets and
				annotators to NIF.
		</div>

		<div class="container picture">
			<img src="/gerbil/webResources/gerbiloverview.png"
				alt="gerbil overview">
		</div>
	</div>
</body>