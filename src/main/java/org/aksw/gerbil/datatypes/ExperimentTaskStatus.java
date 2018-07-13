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
import org.aksw.gerbil.matching.Matching;

public class ExperimentTaskStatus {

	public int state;
	public String version;
	public long timestamp;
	public String annotator;
	public String dataset;
	public ExperimentType type;
	public Matching matching;
	public int idInDb;
	public String gerbilVersion;
	public Map<String, TaskResult> resultsMap;

	public List<ExperimentTaskStatus> subTasks;

	/**
	 * Contains the error message if {@link #state} !=
	 * {@link ExperimentDAO#TASK_FINISHED}, else this should be null.
	 */
	public String stateMsg = null;

	public ExperimentTaskStatus(String annotator, String dataset, ExperimentType type, Matching matching, int state,
			String version, long timestamp, int idInDb) {
		this(annotator, dataset, type, matching, state, version, timestamp, idInDb, null);
	}

	public ExperimentTaskStatus(String annotator, String dataset, ExperimentType type, Matching matching, int state,
			String version, long timestamp, int idInDb, String gerbilVersion) {
		this.annotator = annotator;
		this.dataset = dataset;
		this.type = type;
		this.matching = matching;
		this.state = state;
		this.version = version;
		this.timestamp = timestamp;
		this.idInDb = idInDb;
		this.gerbilVersion = gerbilVersion;
	}
	
	public ExperimentTaskStatus(ExperimentTaskConfiguration configuration, int state,
            int errorCount) {
        this(configuration.annotatorConfig.getName(), configuration.datasetConfig.getName(), configuration.type,
                configuration.matching, state, null, (new java.util.Date()).getTime(), -1);
    }
	
	public ExperimentTaskStatus(String annotator, String dataset, ExperimentType type, Matching matching, int state) {
        this(annotator, dataset, type, matching, state, null, (new java.util.Date()).getTime(), -1,
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
}
