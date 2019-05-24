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
package org.aksw.gerbil.web;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.annotator.AnnotatorConfiguration;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskStatus;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.impl.ROCEvaluator;
import org.aksw.gerbil.web.config.AdapterList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ExperimentOverviewController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentOverviewController.class);

	private static final String GERBIL_PROPERTIES_CHALLENGE_END_KEY = "org.aksw.gerbil.challenge.enddate";
	public static final String DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss XXX";

	@Autowired
	@Qualifier("experimentDAO")
	private ExperimentDAO dao;

	@Autowired
	@Qualifier("annotators")
	private AdapterList<AnnotatorConfiguration> annotators;

	@Autowired
	@Qualifier("datasets")
	private AdapterList<DatasetConfiguration> datasets;

	@RequestMapping("/experimentoverview")
	public @ResponseBody String experimentoverview(@RequestParam(value = "experimentType") String experimentType) {
		LOGGER.debug("Got request on /experimentoverview(experimentType={}", experimentType);
		ExperimentType eType = ExperimentType.valueOf(experimentType);

		String annotatorNames[] = loadAnnotators(eType);
		String datasetNames[] = loadDatasets(eType);
		Calendar challengeDate = null;
		if(GerbilConfiguration.getInstance().containsKey(GERBIL_PROPERTIES_CHALLENGE_END_KEY)) {
        	String tmpDate = GerbilConfiguration.getInstance().getString(GERBIL_PROPERTIES_CHALLENGE_END_KEY);
        	challengeDate = Calendar.getInstance();
        	DateFormat df = new SimpleDateFormat(DATE_FORMAT_STRING);
        	try {
				challengeDate.setTime(df.parse(tmpDate));
			} catch (ParseException e) {
				challengeDate = null;
				LOGGER.error("Could not set challenge end time!", e);
			}
        }
		
		return loadLatestResults(eType, annotatorNames, datasetNames, challengeDate);

	}

	private String loadLatestResults(ExperimentType experimentType,  String[] annotatorNames,
			String[] datasetNames, Calendar challengeDate) {

		StringBuilder listsAsJson = new StringBuilder("{ \"datasets\": [ {");
	
		
		int count=0;
		for(String dataset : datasetNames){
			List<ExperimentTaskStatus> leaderList = new ArrayList<ExperimentTaskStatus>();
			StringBuilder rocs = new StringBuilder("");
			if(experimentType.equals(ExperimentType.SWC2) || experimentType.equals(ExperimentType.SWC_2019)) {
				rocs.append(", \"rocs\" : [");
			}
			listsAsJson.append("\"datasetName\" : \"").append(dataset).append("\", ");
			
			List<ExperimentTaskStatus> results;
			if(challengeDate==null) {
				results = dao.getBestResults(experimentType.name(), dataset);
			}
			else {
				
				results = dao.getBestResults(experimentType.name(), dataset, new Timestamp(challengeDate.getTimeInMillis()));
			}
			if(results!=null) {
				leaderList.addAll(results);
				
			}
			listsAsJson.append(" \"leader\" : [");
			int count2=0;
			boolean firstRoc=true;
			for(ExperimentTaskStatus expResults : leaderList){
				String annotator = expResults.annotator.substring(0, expResults.annotator.lastIndexOf("(")).replace("\"", "\\\"");
				listsAsJson.append("{ \"annotatorName\" : \"").append(annotator).append("\", \"value\": \"");
				listsAsJson.append(expResults.resultsMap.get(ROCEvaluator.AUC_NAME).getResValue()).append("\", \"id\": \"").append(dao.getTaskId(expResults.idInDb)).append("\"}");
							
				if(count2<leaderList.size()-1) {
					listsAsJson.append(", ");
				}
				
				if(experimentType.equals(ExperimentType.SWC2) || experimentType.equals(ExperimentType.SWC_2019)) {
					if(firstRoc&&expResults.resultsMap.get(ROCEvaluator.ROC_NAME)!=null) {
						rocs.append("{\"label\" : \"").append(annotator).append("\", ");
						rocs.append(expResults.resultsMap.get(ROCEvaluator.ROC_NAME).getResValue()).append("}");
						firstRoc=false;
					}
					else if(expResults.resultsMap.get(ROCEvaluator.ROC_NAME)!=null){
						rocs.append(", {\"label\" : \"").append(annotator).append("\", ");
						rocs.append(expResults.resultsMap.get(ROCEvaluator.ROC_NAME).getResValue()).append("}");
					}
					
				}
				
				count2++;
			}
			if(experimentType.equals(ExperimentType.SWC2) || experimentType.equals(ExperimentType.SWC_2019)) {
				rocs.append("]");
			}
			listsAsJson.append("]").append(rocs).append("}");
			if(count<datasetNames.length-1)
				listsAsJson.append(", {");
			count++;
		}

		listsAsJson.append("]}");
		
		return listsAsJson.toString();
	}

	private String[] loadAnnotators(ExperimentType eType) {
		
		Set<String> annotatorNames = dao.getAnnotators();
		String annotatorNameArray[] = annotatorNames.toArray(new String[annotatorNames.size()]);
		Arrays.sort(annotatorNameArray);
		return annotatorNameArray;
	}

	private String[] loadDatasets(ExperimentType eType) {
		Set<String> datasetNames = datasets.getAdapterNamesForExperiment(eType);
		String datasetNameArray[] = datasetNames.toArray(new String[datasetNames.size()]);
		Arrays.sort(datasetNameArray);
		return datasetNameArray;
	}
	
}
