package org.aksw.gerbil.evaluate.impl;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class ROCCurve {

	public List<Point> points = new ArrayList<Point>();
	
	public int trueStmts=0;
	public int falseStmts=0;
	
	private DIRECTION lastDir=null;
	
	private enum DIRECTION {
		UP, RIGHT
	};
	
	public ROCCurve(int trueStmts, int falseStmts){
		this.trueStmts=trueStmts;
		this.falseStmts=falseStmts;
	}
	
	public void addPoint(double x, double y){
		Point p = new Point();
		p.setLocation(x, y);
		addPoint(p);
	}
	
	public void addPoint(Point p){
		points.add(p);
	}
	
	public void addUp(){
		Point last = points.get(points.size()-1);
		Point newP = new Point();
		newP.setLocation(last.getX(), last.getY()+1.0/trueStmts);
		if(DIRECTION.UP.equals(lastDir))
			points.remove(last);
		points.add(newP);
		lastDir=DIRECTION.UP;
	}
	
	public void addRight(){
		Point last = points.get(points.size()-1);
		Point newP = new Point();
		newP.setLocation(last.getX()+1.0/falseStmts, last.getY());
		if(DIRECTION.RIGHT.equals(lastDir))
			points.remove(last);
		points.add(newP);
		lastDir=DIRECTION.RIGHT;
	}
	
	public double calcualteAUC(){
		double auc = 0.0;
		for(int i=0; i<points.size()-2;i++){
			Point pointA = points.get(i);
			Point pointB = points.get(i+1);
			//calculate area under the points (rectangle)
			double aup = pointA.y*(pointB.x-pointA.x);
			auc += aup;
			
		}
		return auc;
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("{ roc : [");
		for(Point p : points){
			builder.append("{");
			builder.append("x : \"").append(p.x).append("\"");
			builder.append("y : \"").append(p.y).append("\"");
			builder.append("}");
		}
		builder.append("]}");
		return builder.toString();
	}
	
}
