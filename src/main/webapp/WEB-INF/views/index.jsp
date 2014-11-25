<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<head>
<link rel='stylesheet' href='/gerbil/webjars/bootstrap/3.2.0/css/bootstrap.min.css'>
<script	src="/gerbil/webjars/jquery/2.1.1/jquery.min.js"></script>
<script src="/gerbil/webjars/bootstrap/3.2.0/js/bootstrap.min.js"></script>
<link rel="icon" type="image/png" href="/gerbil/webResources/gerbilicon.png">
</head>
<body class="container">
	<%@include file="navbar.jsp"%>
	<div class="jumbotron">
		<div class="container">
			<h1>GERBIL - General Entity Annotator Benchmark</h1>
			<p>This is GERBIL. All your peanuts belong to me!</p>
		</div>
		
		<div class="container">
			<img src="/gerbil/webResources/gerbiloverview.png" alt="gerbil overview">
		</div>
	</div>
</body>