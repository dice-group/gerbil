/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aksw.gerbil.datatypes.ExperimentTaskStatus;
import org.aksw.gerbil.datatypes.TaskResult;

import com.carrotsearch.hppc.IntIntOpenHashMap;
import com.carrotsearch.hppc.IntObjectOpenHashMap;

public class SimpleLoggingResultStoringDAO4Debugging extends SimpleLoggingDAO4Debugging {

    private IntObjectOpenHashMap<ExperimentTaskStatus> results = new IntObjectOpenHashMap<ExperimentTaskStatus>();
    private IntIntOpenHashMap states = new IntIntOpenHashMap();
    public static final String[] RES_NAME_ARR = {"Micro F1 score", "Micro Precision", "Micro Recall", "Macro F1 score"
    		, "Macro Precision", "Macro Recall"};
    public static final String ERROR_COUNT_NAME = "Error Count";

    @Override
    public void setExperimentTaskResult(int experimentTaskId, ExperimentTaskStatus result) {
        super.setExperimentTaskResult(experimentTaskId, result);
        results.put(experimentTaskId, result);
    }

    @Override
    public void setExperimentState(int experimentTaskId, int state) {
        super.setExperimentState(experimentTaskId, state);
        states.put(experimentTaskId, state);
    }

    public ExperimentTaskStatus getTaskResult(int experimentTaskId) {
        if (results.containsKey(experimentTaskId)) {
            return results.get(experimentTaskId);
        } else {
            return null;
        }
    }

    @Override
    public int getExperimentState(int experimentTaskId) {
        if (states.containsKey(experimentTaskId)) {
            return states.get(experimentTaskId);
        } else {
            return 0;
        }
    }

    @Override
    protected List<String[]> getAnnotatorDatasetCombinations(String experimentType, String matching) {
        return new ArrayList<String[]>(0);
    }

    @Override
    public List<ExperimentTaskStatus> getResultsOfExperiment(String experimentId) {
        List<ExperimentTaskStatus> resultsOfExperiment = new ArrayList<ExperimentTaskStatus>(
                results.size() + states.size());
        ExperimentTaskStatus tempExTask;
        for (int i = 0; i < results.allocated.length; ++i) {
            if (results.allocated[i]) {
            	tempExTask = (ExperimentTaskStatus) ((Object[]) results.values)[i];
            	setResultMap(tempExTask);
                resultsOfExperiment.add(tempExTask);
            }
        }
        
        return resultsOfExperiment;
    }
    
    public void setResultMap(ExperimentTaskStatus result) {
    	Map<String, TaskResult> resMap = result.getResultsMap();
    	TaskResult tempRes;
    	for(String dResName: RES_NAME_ARR) {
    		//Add a default double entry
    		if(resMap.get(dResName)==null) {
    			tempRes = new TaskResult(0d, "DOUBLE");
    			resMap.put(dResName, tempRes);
    		}
    	}
    	
    	if(resMap.get(ERROR_COUNT_NAME)==null) {
    		tempRes = new TaskResult(0, "INT");
        	resMap.put(ERROR_COUNT_NAME, tempRes);
    	}
    }

}
