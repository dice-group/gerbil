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
			<h1>Semantic Web Challenge 2017</h1>
			<p>Welcome to the evaluation platform for the Semantic Web Challenge at <a href="">ISWC 2017</a>. </p>
            <p> This year's Semantic Web Challenge is centred around two important tasks for building large-scale knowledge graphs:
            <ol>
            <li> <b>Knowledge graph population</b>. Given the name and type of a subject entity, (e.g., a company) and a relation, (e.g., CEO) participants are expected to provide the value(s) for the relation. Submit your solution <a href="">here</a>. The leaderboard for this task can be found <a href="">here</a>.</li> 
            <li> <b>Knowledge graph validation</b>. Given a statement about an entity, e.g., the CEO of a company, participants are expected to provide an assessment about the correctness of the statement. Submit your solution <a href="">here</a>. The leaderboard for this task can be found <a href="">here</a>.</li>
            </ol>
            More details on the challenge can be found <a href="https://iswc2017.semanticweb.org/calls/iswc-semantic-web-challenge-2017/">here</a>. </p>
			<p>This evaluation platform is based on the <a
					href="http://gerbil.aksw.org">
					GERBIL</a> framework and is powered the <a href="http://project-hobbit.eu">HOBBIT</a>. It offers an easy-to-use web-based platform for the Semantic Web
					Challenge 2017 at the ISWC.
				<!-- To add a tool to
				GERBIL, all the end user has to do is to provide a URL to a REST
				interface to its tool which abides by a given specification. The
				integration and benchmarking of the tool against user-specified
				datasets is then carried out automatically by the GERBIL platform.
				Currently, our platform provides results for 9 annotators and 11
				datasets with more coming. Internally, GERBIL is based on the
				Natural Language Programming Interchange Format (NIF) and provide
				Java classes for implementing APIs for datasets and annotators to
				NIF. -->
		</div>

		<div class="container">
<!-- 			<img src="/gerbil/webResources/GERBIL_QA_overview.png" -->
<!-- 				alt="gerbil overview"> -->
		</div>
	</div>
</body>