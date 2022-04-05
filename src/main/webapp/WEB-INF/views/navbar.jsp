<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="java.util.ArrayList" %>
<%@page import="java.util.List" %>
<c:set var="uriPrefix" value="/gerbil" />
<%
	List<String[]> navItems = new ArrayList();
	navItems.add(new String[]{"/", "Home"});
	navItems.add(new String[]{"/config", "Configure Experiment"});
	navItems.add(new String[]{"/config-qald", "QALD10"});
	navItems.add(new String[]{"/overview", "Experiment Overview"});
	navItems.add(new String[]{"/about", "About Us"});
	pageContext.setAttribute("navItems", navItems);
%>
<nav class="navbar navbar-default" role="navigation">
	<div class="container-fluid">
		<!-- Brand and toggle get grouped for better mobile display -->
		<div class="navbar-header">
			<a class="navbar-brand" style="padding-top: 0px" href="/gerbil/"> <img style="height: 50px" src="/gerbil/webResources/gerbil_logo_transparent.png" alt="Logo of Gerbil">
			</a>
		</div>
		<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
			<ul class="nav navbar-nav">
				<c:forEach var="item" items="${navItems}">
					<li class="${requestScope['javax.servlet.forward.request_uri'].equals(uriPrefix.concat(item[0])) ? 'active' : ''}"><a href="${uriPrefix}${item[0]}">${item[1]}</a></li>
				</c:forEach>
			</ul>
		</div>
	</div>
</nav>
