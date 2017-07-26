package org.aksw.gerbil.evaluate.impl;

public class Point {

	public double x;
	public double y;

	public void setLocation(double x, double y) {
		this.x=x;
		this.y=y;
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("[ x : ").append(x).append(", y : ").append(y).append(" ]");
		return builder.toString();
	}

}
