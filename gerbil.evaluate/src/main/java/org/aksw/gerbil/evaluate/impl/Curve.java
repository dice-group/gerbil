package org.aksw.gerbil.evaluate.impl;

import java.util.ArrayList;
import java.util.List;

public class Curve {

	private List<Point> points = new ArrayList<Point>();
	protected Point first;
	protected Point last;

	public Curve(Point start, Point last) {
		this.first = start;
		this.last = last;
	}

	public void addPoint(double x, double y) {
		Point p = new Point();
		p.setLocation(x, y);
		addPoint(p);
	}

	public void addPoints(List<Point> points) {
		this.points.addAll(points);
	}

	public void addPoint(Point p) {
		points.add(p);
	}

	public void finishCurve() {
		if(!points.isEmpty())
			points.add(last);
	}
	
	public double calculateAUC() {
        double auc = 0.0;
        double aup;
        Point pointA;
        Point pointB = points.get(0);
        for (int i = 1; i < points.size(); i++) {
            pointA = pointB;
            pointB = points.get(i);
            // calculate area under the points (rectangle)
            if (pointB.x != pointA.x) {
                // if the two points are a step to the right
                if (pointB.y == pointA.y) {
                    aup = pointA.y * (pointB.x - pointA.x);
                } else {
                    // this is a diagonal
                    // rectangle "under B"
                    aup = pointA.y * (pointB.x - pointA.x);
                    // triangle from A to B
                    aup += 0.5 * (pointB.y - pointA.y) * (pointB.x - pointA.x);
                }
                auc += aup;
            }
        }
        return auc;
    }
	
	@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\"data\" : [");
        for (int i = 0; i < points.size() - 1; i++) {
            Point p = points.get(i);
            builder.append("{");
            builder.append("\"x\" : ").append(p.x).append(",");
            builder.append("\"y\" : ").append(p.y).append("");
            builder.append("},");
        }
        if (points.size() > 0) {
            Point p = points.get(points.size() - 1);
            builder.append("{");
            builder.append("\"x\" : ").append(p.x).append(",");
            builder.append("\"y\" : ").append(p.y).append("");
            builder.append("}");
        }
        builder.append("] ");
        return builder.toString();
    }

}
