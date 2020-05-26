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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.gerbil.database.ExperimentDAO;

public class ExperimentTaskStatus {

	public int state;
	public String version;
	public long timestamp;
	public String annotator;
	public String dataset;
	public ExperimentType type;
	public int idInDb;
	public String gerbilVersion;
	public Map<String, TaskResult> resultsMap;

	public List<ExperimentTaskStatus> subTasks;

	/**
	 * Contains the error message if {@link #state} !=
	 * {@link ExperimentDAO#TASK_FINISHED}, else this should be null.
	 */
	public String stateMsg = null;

	public ExperimentTaskStatus(String annotator, String dataset, ExperimentType type,  int state,
			String version, long timestamp, int idInDb) {
		this(annotator, dataset, type, state, version, timestamp, idInDb, null);
	}

	public ExperimentTaskStatus(String annotator, String dataset, ExperimentType type, int state,
			String version, long timestamp, int idInDb, String gerbilVersion) {
		this.annotator = annotator;
		this.dataset = dataset;
		this.type = type;
		this.state = state;
		this.version = version;
		this.timestamp = timestamp;
		this.idInDb = idInDb;
		this.gerbilVersion = gerbilVersion;
	}
	
	public ExperimentTaskStatus(ExperimentTaskConfiguration configuration, int state,
            int errorCount) {
        this(configuration.annotatorConfig.getName(), configuration.datasetConfig.getName(), configuration.type,
                 state, null, (new java.util.Date()).getTime(), -1);
    }
	
	public ExperimentTaskStatus(String annotator, String dataset, ExperimentType type,  int state) {
        this(annotator, dataset, type, state, null, (new java.util.Date()).getTime(), -1,
                null);
    }

	public void setState(int state) {
		this.state = state;
	}

	public int getState() {
		return state;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
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

	public boolean hasSubTasks() {
		return (subTasks != null) && (subTasks.size() > 0);
	}

	public List<ExperimentTaskStatus> getSubTasks() {
		return subTasks;
	}

	public void addSubTask(ExperimentTaskStatus subTaskResult) {
		if (subTasks == null) {
			subTasks = new ArrayList<ExperimentTaskStatus>();
		}
		subTasks.add(subTaskResult);
	}

	public void setSubTasks(List<ExperimentTaskStatus> subTasks) {
		this.subTasks = subTasks;
	}

	public int getNumberOfSubTasks() {
		if (subTasks == null) {
			return 0;
		} else {
			return subTasks.size();
		}
	}

	public Map<String, TaskResult> getResultsMap() {
		if (resultsMap == null) {
			this.resultsMap = new HashMap<String, TaskResult>();
		}
		return resultsMap;
	}

	public void setResultsMap(Map<String, TaskResult> resultsMap) {
		this.resultsMap = resultsMap;
	}
	
	 @Override
	    public String toString() {
	        StringBuilder builder = new StringBuilder();
	        builder.append("ExperimentTaskResult(state=");
	        builder.append(state);
	        builder.append(",taskId=");
	        builder.append(idInDb);
	        for(String key : resultsMap.keySet()) {
                builder.append(',');
	            builder.append(key);
	            builder.append('=');
	            builder.append(resultsMap.get(key).toString());
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
	        ExperimentTaskStatus other = (ExperimentTaskStatus) obj;
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
	        if (state != other.state)
	            return false;
	        if (timestamp != other.timestamp)
	            return false;
	        if (type != other.type)
	            return false;
	        return true;
	    }
}
