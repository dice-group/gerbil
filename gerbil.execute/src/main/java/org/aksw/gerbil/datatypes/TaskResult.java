package org.aksw.gerbil.datatypes;

public class TaskResult {
	private Object resValue;
	private String resType;
	
	public TaskResult(Object resValue, String resType) {
		super();
		this.resValue = resValue;
		this.resType = resType;
	}
	public Object getResValue() {
		return resValue;
	}
	public void setResValue(Object resValue) {
		this.resValue = resValue;
	}
	public String getResType() {
		return resType;
	}
	public void setResType(String resType) {
		this.resType = resType;
	}
	@Override
	public String toString() {
	    return resType + ":" + resValue;
	}
}
