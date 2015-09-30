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

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.datatypes.ExperimentTaskResult;

public class SimpleLoggingResultStoringDAO4Debugging extends SimpleLoggingDAO4Debugging {

    private Int2ObjectOpenHashMap<ExperimentTaskResult> results = new Int2ObjectOpenHashMap<ExperimentTaskResult>();
    private Int2IntOpenHashMap states = new Int2IntOpenHashMap();

    @Override
    public void setExperimentTaskResult(int experimentTaskId, ExperimentTaskResult result) {
        super.setExperimentTaskResult(experimentTaskId, result);
        results.put(experimentTaskId, result);
    }

    @Override
    public void setExperimentState(int experimentTaskId, int state) {
        super.setExperimentState(experimentTaskId, state);
        states.put(experimentTaskId, state);
    }

    public ExperimentTaskResult getTaskResult(int experimentTaskId) {
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
    protected ExperimentTaskResult getLatestExperimentTaskResult(String experimentType, String matching,
            String annotatorName, String datasetName) {
        return null;
    }

}
