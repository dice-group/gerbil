/*
 *
 */
function plot(data) {

    var chartData = [];
    var LegendOptions = [];
    var tbl_body = "";
    var tbl_hd = "";

    // table
    $.each(data, function(i) {
        var tbl_row = "";
        if (i > 0) {
            $.each(this, function(k, v) {
                tbl_row += "<td>" + v + "</td>";
            });
            tbl_body += "<tr>" + tbl_row + "</tr>";
        } else {
            $.each(this, function(k, v) {
                tbl_row += "<th class=\"rotated_cell\">" + v + "</th>";
            });
            tbl_hd += "<tr>" + tbl_row + "</tr>";
        }
    });
    $("#outputTable thead").html(tbl_hd);
    $("#outputTable tbody").html(tbl_body);

    // plot
    $.each(data, function(i) {
        //iterate over rows
        if (i > 0) {
            var annotatorResults = [];
            $.each(this, function(k, v) {
                if (k === 0) {
                    LegendOptions.push(v);
                } else {
                    var tmp = {};
                    tmp.axis = data[0][k];
                    if (v == "n.a." || v.indexOf("error") > -1) {
                        tmp.value = 0;
                    } else {
                        tmp.value = v;
                    }
                    annotatorResults.push(tmp);
                }
            });
            chartData.push(annotatorResults);
        }
    });
    drawChart(chartData, LegendOptions);
}

/*
 *
 *
 *
 */
function drawChart(data, LegendOptions) {
    
    var w = 500,
        h = 500;

    var colorscale = function(i) {
        return colorForString(LegendOptions[i].sub());
    };

    var mycfg = {
        w: w,
        h: h,
        maxValue: 0.6,
        levels: 6,
        ExtraWidthX: 300,
        colorscale: colorscale
    };

    //Call function to draw the Radar chart
    //Will expect that data is in %'s
    RadarChart.draw("#chart", data, mycfg);

    ////////////////////////////////////////////
    /////////// Initiate legend ////////////////
    ////////////////////////////////////////////

    var svg = d3.select('#body')
        .selectAll('svg')
        .append('svg')
        .attr("width", w + 300)
        .attr("height", h);

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
        .attr('transform', 'translate(250,20)');
    //Create colour squares
    legend.selectAll('rect')
        .data(LegendOptions)
        .enter()
        .append("rect")
        .attr("x", w - 65)
        .attr("y", function(d, i) {
            return i * 20;
        })
        .attr("width", 10)
        .attr("height", 10)
        .style("fill", function(d, i) {
            return colorscale(i);
        });
    //Create text next to squares
    legend.selectAll('text')
        .data(LegendOptions)
        .enter()
        .append("text")
        .attr("x", w - 52)
        .attr("y", function(d, i) {
            return i * 20 + 9;
        })
        .attr("font-size", "11px")
        .attr("fill", "#737373")
        .text(function(d) {
            return d;
        });
}
