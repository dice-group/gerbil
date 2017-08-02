package org.aksw.gerbil.evaluate.impl;

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
		double lastX=0.0, lastY=0.0;
		if(!points.isEmpty()){
			Point last = points.get(points.size()-1);
			lastX=last.x;
			lastY=last.y;
			if(DIRECTION.UP.equals(lastDir))
				points.remove(last);
		}
		
		Point newP = new Point();
		lastY = lastY+1.0/trueStmts;
		newP.setLocation(lastX, lastY);
		
		points.add(newP);
		lastDir=DIRECTION.UP;
	}
	
	public void addRight(){
		double lastX=0.0, lastY=0.0;
		if(!points.isEmpty()){
			Point last = points.get(points.size()-1);
			lastX=last.x;
			lastY=last.y;
			if(DIRECTION.RIGHT.equals(lastDir))
				points.remove(last);
		}
		Point newP = new Point();
		newP.setLocation(lastX+1.0/falseStmts, lastY);
		
		points.add(newP);
		lastDir=DIRECTION.RIGHT;
	}
	
	public double calcualteAUC(){
		double auc = 0.0;
		for(int i=0; i<points.size()-1;i++){
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
		for(int i=0; i<points.size()-1; i++){
			Point p = points.get(i);
			builder.append("{");
			builder.append("x : \"").append(p.x).append("\",");
			builder.append("y : \"").append(p.y).append("\"");
			builder.append("},");
		}
		if(points.size()>0){
			Point p = points.get(points.size()-1);
			builder.append("{");
			builder.append("x : \"").append(p.x).append("\",");
			builder.append("y : \"").append(p.y).append("\"");
			builder.append("}");
		}
		builder.append("]}");
		return builder.toString();
	}
	
}
