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
		int compared =  ((Double)arg1.getResults()[ExperimentTaskResult.MACRO_F1_MEASURE_INDEX]).compareTo(arg0.getResults()[ExperimentTaskResult.MACRO_F1_MEASURE_INDEX]);
		if(compared == 0) {
			compared =  ((Double)arg1.getResults()[ExperimentTaskResult.MICRO_F1_MEASURE_INDEX]).compareTo(arg0.getResults()[ExperimentTaskResult.MICRO_F1_MEASURE_INDEX]);
		}
		return compared;
	}

}