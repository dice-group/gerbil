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
ol li {
    /* Should have the same style as .jumbotron p */
    margin-bottom: 15px;
    font-size: 21px;
    font-weight: 200;
}
</style>
</head>
<body class="container">
	<%@include file="navbar.jsp"%>
	<div class="jumbotron">
		<div class="container">
			<h1>Semantic Web Challenge 2019</h1>
			<p>Welcome to the evaluation platform for the Semantic Web Challenge at <a href="https://iswc2019.semanticweb.org/">ISWC 2019</a>. </p>
            <p>This year’s challenge is centered on validation of factual information in a newly generated Knowledge graph. The core dataset consists of a graph of entities (drugs, diseases and products) and information linking these entities. The dataset is created by extracting information from a well known source and identifying links between entities. The identified links are used to create new properties and generate triple statements.
		</p>
            <p>More details on the challenge can be found <a href="https://dice-group.github.io/semantic-web-challenge.github.io/">here</a>. </p>
			<p>This evaluation platform is based on the <a href="http://gerbil.aksw.org">GERBIL</a> framework and is powered by the <a href="http://project-hobbit.eu">HOBBIT</a> project. <!-- It offers an easy-to-use web-based platform for the Semantic Web Challenge 2017 at the ISWC.</p>
					<b>Challenge Website:</b> <a href="https://iswc2017.semanticweb.org/calls/iswc-semantic-web-challenge-2017/">https://iswc2017.semanticweb.org/calls/iswc-semantic-web-challenge-2017/</a> -->
                    </p>
				
	<a href="https://project-hobbit.eu"><img style="height: 85px" src="/gerbil/webResources/Hobbit_Logo_Claim_2015_rgb.png" alt="Logo of Hobbit"></a>
	<a href="http://gerbil.aksw.org"><img style="height: 85px" src="/gerbil/webResources/gerbil_logo_large_transparent.png" alt="Logo of Gerbil"></a>
		</div>

		<div class="container">
<!-- 			<img src="/gerbil/webResources/GERBIL_QA_overview.png" -->
<!-- 				alt="gerbil overview"> -->
		</div>
	</div>
</body>
