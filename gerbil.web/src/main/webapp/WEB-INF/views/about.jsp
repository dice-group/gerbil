<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<head>
<link rel="stylesheet"
	href="/gerbil/webjars/bootstrap/3.2.0/css/bootstrap.min.css">
<link rel="stylesheet"
	href="/gerbil/webjars/bootstrap-multiselect/0.9.8/css/bootstrap-multiselect.css" />
<link rel="icon" type="image/png" href="/gerbil/webResources/gerbilicon_transparent.png">
<script src="/gerbil/webjars/jquery/2.1.1/jquery.min.js"></script>
</head>
<body class="container">
	<%@include file="navbar.jsp"%>
	GERBIL SWC is the evaluation platform for the Semantic Web Challenge at ISWC 2018.<br>
	Challenge Website: <a href="http://iswc2018.semanticweb.org/calls/iswc-semantic-web-challenge-2018/">http://iswc2018.semanticweb.org/calls/iswc-semantic-web-challenge-2018/</a>
	
	<h5>Challenge organizers</h5>
	<ul>
		<li>Dan Bennett (Thomson Reuters, Eagan, MN, USA)</li>
	    <li>Axel Ngonga (University of Paderborn, Germany)</li>
	    <li>Heiko Paulheim (University of Mannheim, Germany)</li>
	</ul>
	<h5>Developers</h5>
	<ul>
		<li>Felix Conrads (University of Leipzig and University of Paderborn, Germany)</li>
		<li>Michael Röder (University of Paderborn and InfAI Leipzig, Germany) </li>
	</ul>
	<p>GERBIL is a community effort. Join us! For more information visit <a href="http://gerbil.aksw.org">http://gerbil.aksw.org</a>.</p>
	<p>This work has been supported by the H2020 project HOBBIT (GA no. 688227)</p>
	<a href="https://project-hobbit.eu"><img style="height: 70px" src="/gerbil/webResources/Hobbit_Logo_Claim_2015_rgb.png" alt="Logo of Hobbit"></a>
	<a href="http://ec.europa.eu/"><img style="height: 70px" src="/gerbil/webResources/logo_ec.png" alt="Logo of European Comission"></a>
	<a href="http://europa.eu/"><img style="height: 70px" src="/gerbil/webResources/Europe.png" alt="Logo of Europe"></a>
</body>
