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
import org.aksw.gerbil.datatypes.ChallengeDescr;
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
	private static final String GERBIL_PROPERTIES_CHALLENGE_START_KEY = "org.aksw.gerbil.challenge.startdate";
	private static final String GERBIL_PROPERTIES_CHALLENGE_NAME_KEY = "org.aksw.gerbil.challenge.name";
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

	private ChallengeDescr challenge;

	@RequestMapping("/experimentoverview")
	public @ResponseBody String experimentoverview(@RequestParam(value = "experimentType") String experimentType) {
		LOGGER.debug("Got request on /experimentoverview(experimentType={}", experimentType);
		ExperimentType eType = ExperimentType.valueOf(experimentType);

		String annotatorNames[] = loadAnnotators(eType);
		String datasetNames[] = loadDatasets(eType);
		challenge = null;
		if(GerbilConfiguration.getInstance().containsKey(GERBIL_PROPERTIES_CHALLENGE_END_KEY) && 
        		GerbilConfiguration.getInstance().containsKey(GERBIL_PROPERTIES_CHALLENGE_START_KEY) && 
        		GerbilConfiguration.getInstance().containsKey(GERBIL_PROPERTIES_CHALLENGE_NAME_KEY)) {
        	
        	String startDate = GerbilConfiguration.getInstance().getString(GERBIL_PROPERTIES_CHALLENGE_START_KEY);
        	String endDate = GerbilConfiguration.getInstance().getString(GERBIL_PROPERTIES_CHALLENGE_END_KEY);
        	String name = GerbilConfiguration.getInstance().getString(GERBIL_PROPERTIES_CHALLENGE_NAME_KEY);

        	Timestamp challengeEndDate = Timestamp.valueOf(endDate);
        	Timestamp challengeStartDate = Timestamp.valueOf(startDate);

        	DateFormat df = new SimpleDateFormat(ExperimentOverviewController.DATE_FORMAT_STRING);
				//challengeDate.setTime(df.parse(endDate));
				challenge = new ChallengeDescr(challengeStartDate, challengeEndDate, name);
				
			//check if challenge not already ended.
			if(this.challenge.getEndDate().before(Calendar.getInstance().getTime())) {
				challenge =null;
			}
        }
		return loadLatestResults(eType, annotatorNames, datasetNames, challenge);

	}
	
	
	/**
	 * 
	 * { archive : [
	 * 	{
	 * 		challenge: {name, start, end},
	 * 		results: [
	 * 			{
	 * 				type: "exp Type",
	 * 				results: {..see latest results}
	 * 			},
	 * 			...
	 * 		]
	 * 	},
	 *  ...
	 * ]}
	 * 
	 * 
	 * 
	 * 
	 * 
	 * @return
	 */
	@RequestMapping("/experimentarchive")
	public @ResponseBody String experimentarchive() {
		LOGGER.debug("Got request on /experimentarchive()");
		StringBuilder response = new StringBuilder("{ \"archive\": [");
		List<ExperimentType> types =  ExperimentType.getKBCTypes();
		List<ChallengeDescr> challenges = dao.getAllChallenges();
		//remove current challenge if challenged not ended yet
		if(this.challenge!=null && !this.challenge.getEndDate().before(Calendar.getInstance().getTime())) {
			challenges.remove(this.challenge);
		}
		
		for(int i=0;i<challenges.size();i++) {
			ChallengeDescr challenge = challenges.get(i);
			response.append("{ \"challenge\":{");
			response.append("\"name\": \"").append(challenge.getName()).append("\", ");
			response.append("\"startDate\": \"").append(challenge.getStartDate().toString()).append("\", ");
			response.append("\"endDate\": \"").append(challenge.getEndDate().toString()).append("\"}, ");
			response.append(loadArchive(types, challenge));
			response.append("}");
			if(i<challenges.size()-1) {
				response.append(", ");
			}
		}
		response.append("]}");
		return response.toString();

	}

	private String loadArchive(List<ExperimentType> types, ChallengeDescr challenge) {
		StringBuilder archive = new StringBuilder("\"results\": [");
		for(int i=0;i<types.size();i++) {
			ExperimentType eType = types.get(i);
			archive.append("{ \"type\": \"").append(eType.getName()).append("\" , \"results\": ");

			String annotatorNames[] = loadAnnotators(eType);
			String datasetNames[] = loadDatasets(eType);
			archive.append(loadLatestResults(eType, annotatorNames, datasetNames, challenge));
			archive.append("}");
			if(i<types.size()-1) {
				archive.append(", ");
			}
		}
		archive.append("]");
		return archive.toString();
	}

	private String loadLatestResults(ExperimentType experimentType,  String[] annotatorNames,
			String[] datasetNames, ChallengeDescr challenge) {

		StringBuilder listsAsJson = new StringBuilder("{ \"datasets\": [ ")  ;
	
		
		int count=0;
		for(String dataset : datasetNames){
			List<ExperimentTaskStatus> leaderList = new ArrayList<ExperimentTaskStatus>();
			StringBuilder rocs = new StringBuilder("");
			if(experimentType.equals(ExperimentType.SWC2) || experimentType.equals(ExperimentType.SWC_2019)) {
				rocs.append(", \"rocs\" : [");
			}
			int count2=0;
			List<ExperimentTaskStatus> results;
			if(challenge==null) {
				results = dao.getBestResults(experimentType.name(), dataset);
			}
			else {
				
				results = dao.getBestResults(experimentType.name(), dataset, challenge.getStartDate(), challenge.getEndDate());
			}
			if(results!=null) {
				leaderList.addAll(results);
				
			}
			if(results == null || leaderList.isEmpty()) {
				if(count>=datasetNames.length-1) {
					listsAsJson.setLength(listsAsJson.length()-1);
					listsAsJson.trimToSize();
				}	
				count++;
				continue;
			}
			listsAsJson.append("{\"datasetName\" : \"").append(dataset).append("\", ");

			listsAsJson.append(" \"leader\" : [ ");
			
			boolean firstRoc=true;
			for(ExperimentTaskStatus expResults : leaderList){
				String annotator = expResults.annotator.substring(0, expResults.annotator.lastIndexOf("(")).replace("\"", "\\\"");
				listsAsJson.append("{ \"annotatorName\" : \"").append(annotator).append("\", \"value\": \"");
				if(expResults.resultsMap.containsKey(ROCEvaluator.AUC_NAME)) {
					listsAsJson.append(expResults.resultsMap.get(ROCEvaluator.AUC_NAME).getResValue()).append("\", \"id\": \"").append(dao.getTaskId(expResults.idInDb)).append("\"}");					
				}
				else if(expResults.resultsMap.containsKey("F1 score")) {
					listsAsJson.append(expResults.resultsMap.get("F1 score").getResValue()).append("\", \"id\": \"").append(dao.getTaskId(expResults.idInDb)).append("\"}");
				}
				else {
					listsAsJson.append("undefined").append("\", \"id\": \"").append(dao.getTaskId(expResults.idInDb)).append("\"}");
				}

							
				if(count2<leaderList.size()-1) {
					listsAsJson.append(", ");
				}
				
				if(experimentType.equals(ExperimentType.SWC2) || experimentType.equals(ExperimentType.SWC_2019)) {
					if(firstRoc&&expResults.resultsMap.get(ROCEvaluator.NAME)!=null) {
						rocs.append("{\"label\" : \"").append(annotator).append("\", ");
						rocs.append(expResults.resultsMap.get(ROCEvaluator.NAME).getResValue()).append("}");
						firstRoc=false;
					}
					else if(expResults.resultsMap.get(ROCEvaluator.NAME)!=null){
						rocs.append(", {\"label\" : \"").append(annotator).append("\", ");
						rocs.append(expResults.resultsMap.get(ROCEvaluator.NAME).getResValue()).append("}");
					}
					
				}
				
				count2++;
			}
			if(experimentType.equals(ExperimentType.SWC2) || experimentType.equals(ExperimentType.SWC_2019)) {
				rocs.append("]");
			}
			listsAsJson.append("]").append(rocs).append("}");
			if(count<datasetNames.length-1)
				listsAsJson.append(",");
			count++;
		}
		//listsAsJson.setCharAt(datasetNames.length-1, ' ');
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