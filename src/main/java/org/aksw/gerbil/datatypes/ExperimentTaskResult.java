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
package org.aksw.gerbil.datatypes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.matching.Matching;

import com.carrotsearch.hppc.IntDoubleOpenHashMap;
import com.carrotsearch.hppc.IntIntOpenHashMap;
import com.carrotsearch.hppc.IntObjectOpenHashMap;

public class ExperimentTaskResult {

    public static final int MICRO_F1_MEASURE_INDEX = 0;
    public static final int MICRO_PRECISION_INDEX = 1;
    public static final int MICRO_RECALL_INDEX = 2;
    public static final int MACRO_F1_MEASURE_INDEX = 3;
    public static final int MACRO_PRECISION_INDEX = 4;
    public static final int MACRO_RECALL_INDEX = 5;
    @Deprecated
    public Object results[];
    public int state;
    @Deprecated
    public int errorCount;
    public long timestamp;
    public String annotator;
    public String dataset;
    public ExperimentType type;
    public Matching matching;
    public int idInDb;
    public String gerbilVersion;
    public IntObjectOpenHashMap<Object> expResults = null;
    public List<ExperimentTaskResult> subTasks;

    /**
     * Contains the error message if {@link #state} !=
     * {@link ExperimentDAO#TASK_FINISHED}, else this should be null.
     */
    public String stateMsg = null;

    public ExperimentTaskResult(String annotator, String dataset, ExperimentType type, Matching matching,
            Object results[], int state, int errorCount, long timestamp) {
        this(annotator, dataset, type, matching, results, state, errorCount, timestamp, -1, null);
    }

    public ExperimentTaskResult(String annotator, String dataset, ExperimentType type, Matching matching,
            Object results[], int state, int errorCount, long timestamp, int idInDb) {
        this(annotator, dataset, type, matching, results, state, errorCount, timestamp, idInDb, null);
    }
    
    public ExperimentTaskResult(String annotator, String dataset, ExperimentType type, Matching matching,
             int state,  long timestamp, String gerbilVersion, int idInDb) {
    	this.annotator = annotator;
        this.dataset = dataset;
        this.type = type;
        this.matching = matching;
        this.state = state;
        this.timestamp = timestamp;
        this.gerbilVersion = gerbilVersion;
        this.idInDb = idInDb; 
    }
 @Deprecated
    public ExperimentTaskResult(String annotator, String dataset, ExperimentType type, Matching matching,
            Object results[], int state, int errorCount, long timestamp, int idInDb, String gerbilVersion) {
        this.annotator = annotator;
        this.dataset = dataset;
        this.type = type;
        this.matching = matching;
        this.results = results;
        this.state = state;
        this.errorCount = errorCount;
        this.timestamp = timestamp;
        this.idInDb = idInDb;
        this.gerbilVersion = gerbilVersion;
    }

    public ExperimentTaskResult(String annotator, String dataset, ExperimentType type, Matching matching,
            Object results[], int state, int errorCount) {
        this(annotator, dataset, type, matching, results, state, errorCount, (new java.util.Date()).getTime(), -1,
                null);
    }

    
    public ExperimentTaskResult(ExperimentTaskConfiguration configuration, Object results[], int state,
            int errorCount) {
        this(configuration.annotatorConfig.getName(), configuration.datasetConfig.getName(), configuration.type,
                configuration.matching, results, state, errorCount, (new java.util.Date()).getTime());
    }
    
    public ExperimentTaskResult(ExperimentTaskConfiguration configuration, int state
            ) {
        this(configuration.annotatorConfig.getName(), configuration.datasetConfig.getName(), configuration.type,
                configuration.matching, state, (new java.util.Date()).getTime(), GerbilConfiguration.getGerbilVersion(), -1);
    }

    @Deprecated
    public Object[] getResults() {
        return results;
    }

    @Deprecated
    public void setResults(Object results[]) {
        this.results = results;
    }

    @Deprecated
    public Object getMicroF1Measure() {
        return results[MICRO_F1_MEASURE_INDEX];
    }

    @Deprecated
    public Object getMicroPrecision() {
        return results[MICRO_PRECISION_INDEX];
    }

    @Deprecated
    public Object getMicroRecall() {
        return results[MICRO_RECALL_INDEX];
    }
    @Deprecated
    public Object getMacroF1Measure() {
        return results[MACRO_F1_MEASURE_INDEX];
    }
    @Deprecated
    public Object getMacroPrecision() {
        return results[MACRO_PRECISION_INDEX];
    }
    @Deprecated
    public Object getMacroRecall() {
        return results[MACRO_RECALL_INDEX];
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
    @Deprecated
    public int getErrorCount() {
        return errorCount;
    }
    @Deprecated
    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTimestampstring() {
        Date date = new Date(timestamp);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAnnotator() {
        return annotator;
    }

    public void setAnnotator(String annotator) {
        this.annotator = annotator;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public ExperimentType getType() {
        return type;
    }

    public void setType(ExperimentType type) {
        this.type = type;
    }

    public Matching getMatching() {
        return matching;
    }

    public void setMatching(Matching matching) {
        this.matching = matching;
    }

    public String getStateMsg() {
        return stateMsg;
    }

    public void setStateMsg(String stateMsg) {
        this.stateMsg = stateMsg;
    }

    public String getGerbilVersion() {
        return gerbilVersion;
    }

    public void setGerbilVersion(String gerbilVersion) {
        this.gerbilVersion = gerbilVersion;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ExperimentTaskResult(state=");
        builder.append(state);
        if (hasAdditionalResults()) {
            for (int i = 0; i < expResults.allocated.length; ++i) {
                if (expResults.allocated[i]) {
                    builder.append(',');
                    builder.append(expResults.keys[i]);
                    builder.append('=');
                    builder.append(expResults.values[i]);
                }
            }
        }
        builder.append(")");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((annotator == null) ? 0 : annotator.hashCode());
        result = prime * result + ((dataset == null) ? 0 : dataset.hashCode());
      //  result = prime * result + errorCount;
        result = prime * result + ((matching == null) ? 0 : matching.hashCode());
      //  result = prime * result + Arrays.hashCode(results);
        result = prime * result + state;
        result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ExperimentTaskResult other = (ExperimentTaskResult) obj;
        if (annotator == null) {
            if (other.annotator != null)
                return false;
        } else if (!annotator.equals(other.annotator))
            return false;
        if (dataset == null) {
            if (other.dataset != null)
                return false;
        } else if (!dataset.equals(other.dataset))
            return false;
//        if (errorCount != other.errorCount)
//            return false;
        if (matching != other.matching)
            return false;
//        if (!Arrays.equals(results, other.results))
//            return false;
        if (state != other.state)
            return false;
        if (timestamp != other.timestamp)
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    public boolean hasAdditionalResults() {
        return (expResults != null) && (expResults.size() > 0);
    }

    public boolean hasAdditionalResult(int id) {
        return (expResults != null) && (expResults.containsKey(id));
    }

    public IntObjectOpenHashMap<Object> getExpResults() {
        return expResults;
    }

    public int[] getExpResultIds() {
        if (hasAdditionalResults()) {
            return expResults.keys().toArray();
        } else {
            return new int[0];
        }
    }

    public Object getExpResults(int id) {
        if (expResults != null) {
            return expResults.get(id);
        } else {
            return 0;
        }
    }
    
    public void addExpResults(int resultId, Object value) {
        if (expResults == null) {
            expResults = new IntObjectOpenHashMap<Object>();
        }
        expResults.put(resultId, value);
    }
    
    public void setExpResults(IntObjectOpenHashMap<Object> experimentResults) {
        this.expResults = experimentResults;
    }

    public boolean hasSubTasks() {
        return (subTasks != null) && (subTasks.size() > 0);
    }

    public List<ExperimentTaskResult> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(ExperimentTaskResult subTaskResult) {
        if (subTasks == null) {
            subTasks = new ArrayList<ExperimentTaskResult>();
        }
        subTasks.add(subTaskResult);
    }

    public void setSubTasks(List<ExperimentTaskResult> subTasks) {
        this.subTasks = subTasks;
    }

    public int getNumberOfSubTasks() {
        if (subTasks == null) {
            return 0;
        } else {
            return subTasks.size();
        }
    }
}