package org.aksw.gerbil.web;

import java.util.Comparator;

import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.aksw.gerbil.datatypes.ExperimentType;

public class LeaderBoardComparator implements Comparator<ExperimentTaskResult> {

	private ExperimentType expType;
	
	public LeaderBoardComparator(ExperimentType expType){
		this.expType=expType;
	}
	
	@Override
	public int compare(ExperimentTaskResult arg0, ExperimentTaskResult arg1) {
//		switch(expType){
//		case SWC1:
//			return Double.valueOf(arg1.getAdditionalResult(0).toString()).compareTo(Double.valueOf(arg0.getAdditionalResult(0).toString()));
//		case SWC2:
//			return Double.valueOf(arg1.getAdditionalResult(3).toString()).compareTo(Double.valueOf(arg0.getAdditionalResult(3).toString()));
//		default:
			return ((Double)arg1.getResults()[0]).compareTo(arg0.getResults()[0]);
//		}
	}

}
