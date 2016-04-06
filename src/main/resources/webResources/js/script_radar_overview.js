function drawChart(data, LegendOptions, chartElementId){
	var w = 500,
	h = 500;

	//var colorscale = d3.scale.category10();
	var colorscale = function(i) {
		return colorForString(LegendOptions[i].sub());
	}
	
	//Options for the Radar chart, other than default
	var mycfg = {
	  w: w,
	  h: h,
	  maxValue: 0.6,
	  levels: 6,
	  ExtraWidthX: 300,
	  colorscale: colorscale
	}
	
	//Call function to draw the Radar chart
	//Will expect that data is in %'s
	RadarChart.draw("#" + chartElementId, data, mycfg);
	
	////////////////////////////////////////////
	/////////// Initiate legend ////////////////
	////////////////////////////////////////////
	
	var svg = d3.select('#' + chartElementId + 'body')
		.selectAll('svg')
		.append('svg')
		.attr("xmlns:svg","http://www.w3.org/2000/svg")
		.attr("xmlns","http://www.w3.org/2000/svg")
		.attr("width", w+300)
		.attr("height", h)
	
	//Create the title for the legend
	var text = svg.append("text")
		.attr("class", "title")
		.attr('transform', 'translate(250,0)') 
		.attr("x", w - 70)
		.attr("y", 10)
		.attr("font-size", "12px")
		.attr("fill", "#404040")
		.text("Annotator");
			
	//Initiate Legend	
	var legend = svg.append("g")
		.attr("class", "legend")
		.attr("height", 100)
		.attr("width", 200)
		.attr('transform', 'translate(250,20)') 
		;
		//Create colour squares
		legend.selectAll('rect')
		  .data(LegendOptions)
		  .enter()
		  .append("rect")
		  .attr("x", w - 65)
		  .attr("y", function(d, i){ return i * 20;})
		  .attr("width", 10)
		  .attr("height", 10)
		  .style("fill", function(d, i){ return colorscale(i);})
		  ;
		//Create text next to squares
		legend.selectAll('text')
		  .data(LegendOptions)
		  .enter()
		  .append("text")
		  .attr("x", w - 52)
		  .attr("y", function(d, i){ return i * 20 + 9;})
		  .attr("font-size", "11px")
		  .attr("fill", "#737373")
		  .text(function(d) { return d; })
		  ;	
}