<nav class="navbar navbar-default" role="navigation">
	<div class="container-fluid">
		<!-- Brand and toggle get grouped for better mobile display -->
		<!--<div class="navbar-header">
			<a class="navbar-brand" style="padding-top: 0px" href="/gerbil/"> <img style="height: 50px" src="/gerbil/webResources/gerbil_logo_transparent.png" alt="Logo of Gerbil">
			</a>
		</div>-->
		<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
			<ul id="nav-list" class="nav navbar-nav">
				<li><a href="/gerbil/">Home</a></li>
				<li><a href="/gerbil/config">Submission</a></li>
				<li><a href="/gerbil/overview">Leaderboards</a></li>
				<li><a href="/gerbil/about">About</a></li>
			</ul>
		</div>
	</div>
</nav>
<script>
    $(document).ready(function() {
        //++++++++++++
        // Set the navbar to the current page
        //++++++++++++
        var path = document.location.pathname;
        var bestFittingElement;
        var lengthOfOverlap = 0;
        // search for the longest navigation element overlapping the current path
        $("#nav-list li").each(function(index) {
            var reference = $("a:first", this).attr("href");
            if(path.startsWith(reference) && ((lengthOfOverlap == 0) || (reference.length > lengthOfOverlap))) {
                bestFittingElement = $(this);
                lengthOfOverlap = reference.length;
            }
        });
        bestFittingElement.addClass("active");
	});
</script>
