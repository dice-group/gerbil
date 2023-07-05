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

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.aksw.gerbil.Experimenter;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.dataid.DataIDGenerator;
import org.aksw.gerbil.datatypes.ChallengeDescr;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskStatus;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.datatypes.TaskResult;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.evaluate.impl.PREvaluator;
import org.aksw.gerbil.evaluate.impl.ROCEvaluator;
import org.aksw.gerbil.execute.AnnotatorOutputWriter;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.utils.IDCreator;
import org.aksw.gerbil.web.config.AdapterManager;
import org.aksw.gerbil.web.config.RootConfig;
import org.aksw.simba.topicmodeling.concurrent.overseers.Overseer;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;

@Controller
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private static final String GOOGLE_ANALYTICS_FILE_NAME = "google1d91bc68c8a56517.html";

	private static final String GERBIL_PROPERTIES_CHALLENGE_END_KEY = "org.aksw.gerbil.challenge.enddate";
	private static final String GERBIL_PROPERTIES_CHALLENGE_START_KEY = "org.aksw.gerbil.challenge.startdate";
	private static final String GERBIL_PROPERTIES_CHALLENGE_NAME_KEY = "org.aksw.gerbil.challenge.name";

	private static final String RESNAME_PROP = "org.aksw.gerbil.database.ResultNameSequence";
	// Fetch Result Name sequence from property file
	private static final String[] RESNAMES_DELIMITED = GerbilConfiguration.getInstance().getStringArray(RESNAME_PROP);

    private static boolean isInitialized = false;
    
    private static ChallengeDescr challenge;

    private static synchronized void initialize(ExperimentDAO dao) {
        if (!isInitialized) {
            String id = dao.getHighestExperimentId();
            if (id != null) {
                IDCreator.getInstance().setLastCreatedID(id);
            }
            isInitialized = true;
        }
        // Simply call the dataset mapping so that it has to be instantiated
        // DatasetMapping.getDatasetsForExperimentType(ExperimentType.EExt);
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
			if(!dao.isChallengeInDB(challenge)) {
				dao.addChallenge(challenge);
			}
        }
    }

    @PostConstruct
    public void init() {
        initialize(dao);
    }

    @Autowired
    @Qualifier("experimentDAO")
    private ExperimentDAO dao;

    @Autowired
    private Overseer overseer;

    @Autowired
    private SameAsRetriever globalRetriever;

    @Autowired
    private EvaluatorFactory evFactory;

    @Autowired
    private AdapterManager adapterManager;

    private ExperimentType[] availableExperimentTypes = RootConfig.getAvailableExperimentTypes();

    private AnnotatorOutputWriter annotatorOutputWriter = RootConfig.getAnnotatorOutputWriter();

    @RequestMapping("/config")
    public ModelAndView config() {
        ModelAndView model = new ModelAndView();
        model.setViewName("config");
        model.addObject("challengeStart", challenge.getStartDate().toString());
        model.addObject("challengeName", challenge.getName());
        model.addObject("challengeEnd", challenge.getEndDate().toString());
        //DateFormat df = new SimpleDateFormat(ExperimentOverviewController.DATE_FORMAT_STRING);
        model.addObject("challengeTooLate", challenge.getEndDate().before(Calendar.getInstance().getTime()));
        model.addObject("challengeTooEarly", challenge.getStartDate().after(Calendar.getInstance().getTime()));

        return model;
    }

    @RequestMapping("/overview")
    public ModelAndView overview() {
        ModelAndView model = new ModelAndView();
        model.setViewName("overview");
        return model;
    }

    @RequestMapping("/about")
    public ModelAndView about() {
        ModelAndView model = new ModelAndView();
        model.setViewName("about");
        return model;
    }

    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    /**
     * expects a string like {"type":"A2KB","matching":
     * "Mw - weak annotation match" ,"annotator":["A2KB one","A2KB two"
     * ],"dataset":["datasets"]}
     * 
     * @param experimentData
     * @return
     */
    @RequestMapping("/execute")
    public @ResponseBody String execute(@RequestParam(value = "experimentData") String experimentData) {
        LOGGER.debug("Got request on /execute with experimentData={}", experimentData);
        Object obj = JSONValue.parse(experimentData);
        JSONObject configuration = (JSONObject) obj;
        String typeString = (String) configuration.get("type");
        ExperimentType type = null;
        try {
            type = ExperimentType.valueOf(typeString);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Got a request containing a wrong ExperimentType (\"{}\"). Ignoring it.", typeString);
            return null;
        }
        String matching = (String) configuration.get("matching");
        JSONArray jsonAnnotators = (JSONArray) configuration.get("annotator");
        String[] annotators = new String[jsonAnnotators.size()];
        for (int i = 0; i < jsonAnnotators.size(); i++) {
            annotators[i] = (String) jsonAnnotators.get(i);
        }
        JSONArray jsonDataset = (JSONArray) configuration.get("dataset");
        String[] datasets = new String[jsonDataset.size()];
        for (int i = 0; i < jsonDataset.size(); i++) {
            datasets[i] = (String) jsonDataset.get(i);
        }
        
        JSONArray jsonAnswerFiles = (JSONArray) configuration.get("answerFiles");
        String[] answerFiles = new String[jsonAnswerFiles.size()];
        for (int i = 0; i < jsonAnswerFiles.size(); i++) {
            answerFiles[i] = (String) jsonAnswerFiles.get(i);
        }
        boolean publish = (boolean) configuration.get("publish");
        ExperimentTaskConfiguration[] configs = new ExperimentTaskConfiguration[((answerFiles.length + annotators.length) * datasets.length)];
        int count = 0;
        // create all annotator - dataset combinations
        for (String annotator : annotators) {
            for (String dataset : datasets) {
                configs[count] = new ExperimentTaskConfiguration(adapterManager.getAnnotatorConfig(annotator, type),
                        adapterManager.getDatasetConfig(dataset, type), type, getMatching(matching));
                LOGGER.debug("Created config: {}", configs[count]);
                ++count;
            }
        }
        // create the answer file based experiment tasks
        for (String answerFile : answerFiles) {
        	for (String dataset : datasets) {
        		configs[count] = new ExperimentTaskConfiguration(adapterManager.getAnnotatorConfig(answerFile, type),
        				adapterManager.getDatasetConfig(dataset, type), type, getMatching(matching));
        		LOGGER.debug("Created config: {}", configs[count]);
        		++count;
        	}
        }
        for(ExperimentTaskConfiguration config : configs) {
        	config.setPublish(publish);
        }

        String experimentId = IDCreator.getInstance().createID();
        Experimenter exp = new Experimenter(overseer, dao, globalRetriever, evFactory, configs, experimentId);
        exp.setAnnotatorOutputWriter(annotatorOutputWriter);
        exp.run();

        return experimentId;
    }

    @RequestMapping("/experiment")
    public ModelAndView experiment(@RequestParam(value = "id") String id, HttpServletRequest request) {
        LOGGER.debug("Got request on /experiment with id={}", id);
        Set<String> resNamesDb = new HashSet<>();
        List<ExperimentTaskStatus> results = dao.getResultsOfExperiment(id);
        //Boolean hasRoc = false;
        for(ExperimentTaskStatus result : results){
        	resNamesDb.addAll(result.getResultsMap().keySet());
        	String annotator = result.getAnnotator();
        	annotator = annotator.substring(0, annotator.lastIndexOf("("));
        	result.setAnnotator(annotator);
        	Map<String, TaskResult> resMap = result.getResultsMap();
        	
        	if(resMap.get(ROCEvaluator.NAME) != null) {
        		TaskResult taskRes = resMap.get(ROCEvaluator.NAME);
        		String rocVal = (String) taskRes.getResValue();
        		// rocVal = rocVal.replace("roc", "data"); // it's already data
        		taskRes.setResValue(rocVal);
        		//hasRoc = true;
        	}
        	
        	if(resMap.get(PREvaluator.NAME) != null) {
        		TaskResult taskRes = resMap.get(PREvaluator.NAME);
        		String resVal = (String) taskRes.getResValue();
        		taskRes.setResValue(resVal);
        		//hasRoc = true;
        	}
        }
        ExperimentTaskStateHelper.setStatusLines(results);
        ModelAndView model = new ModelAndView();
        model.setViewName("experiment-new");
        model.addObject("tasks", results);
        // model.addObject("dataid", dataIdGenerator.createDataIDModel(results, id));
       
        List<String> fnlRsltNms = new ArrayList<>();
        boolean isRemoved = false;
        for(String seqEntry : RESNAMES_DELIMITED) {
        	isRemoved = resNamesDb.remove(seqEntry);
        	if(isRemoved) {
        		fnlRsltNms.add(seqEntry);
        	}	        	
        }
        fnlRsltNms.addAll(resNamesDb);
        model.addObject("resultNames",fnlRsltNms);
        //model.addObject("hasRoc", hasRoc);
        
        return model;
    }

    @RequestMapping("/exptypes")
    public @ResponseBody ModelMap expTypes() {
        return new ModelMap("ExperimentType", availableExperimentTypes);
    }

    @SuppressWarnings("deprecation")
    @RequestMapping("/matchings")
    public @ResponseBody ModelMap matchingsForExpType(@RequestParam(value = "experimentType") String experimentType) {
        ExperimentType type = null;
        try {
            type = ExperimentType.valueOf(experimentType);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Got a request containing a wrong ExperimentType (\"{}\"). Ignoring it.", experimentType);
            return null;
        }
        switch (type) {
        case D2KB:
        case ETyping:
            // Mw will not be shown since the positions are always exact and
            // thus it works like Ma
            return new ModelMap("Matching", Lists.newArrayList(Matching.STRONG_ANNOTATION_MATCH));
        case C2KB:
        case Rc2KB:
        case Sc2KB:
        case QA:
        case AIT2KB:
        case AType:
        case P2KB:
        case RE2KB:
            return new ModelMap("Matching", Lists.newArrayList(Matching.STRONG_ENTITY_MATCH));
        case OKE_Task1:
        case OKE_Task2:
        case A2KB:
        case ERec:
        case Sa2KB:
            return new ModelMap("Matching",
                    Lists.newArrayList(Matching.WEAK_ANNOTATION_MATCH, Matching.STRONG_ANNOTATION_MATCH));
        default:
            return new ModelMap("Matching", Lists.newArrayList("none"));
        }
    }

    @RequestMapping("/annotators")
    public @ResponseBody List<String> annotatorsForExpType(
            @RequestParam(value = "experimentType") String experimentType) {
        Set<String> annotatorsForExperimentType = adapterManager
                .getAnnotatorNamesForExperiment(ExperimentType.valueOf(experimentType));
        List<String> list = Lists.newArrayList(annotatorsForExperimentType);
        Collections.sort(list);
        return list;
    }

    @RequestMapping("/datasets")
    public @ResponseBody List<String> datasets(@RequestParam(value = "experimentType") String experimentType) {
        ExperimentType type = null;
        try {
            type = ExperimentType.valueOf(experimentType);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Got a request containing a wrong ExperimentType (\"{}\"). Ignoring it.", experimentType);
            return null;
        }
        Set<String> datasets = adapterManager.getDatasetNamesForExperiment(type);
        List<String> list = Lists.newArrayList(datasets);
        Collections.sort(list);
        return list;
    }

    /**
     * This mapping is needed to authenticate us against Google Analytics. It
     * reads the google file and sends it as String.
     * 
     * @return The google analytics file as String or an empty String if the
     *         file couldn't be loaded.
     */
    @RequestMapping(value = "/google*")
    public @ResponseBody String googleAnalyticsFile() {
        try {
            return FileUtils.readFileToString(new File(GOOGLE_ANALYTICS_FILE_NAME));
        } catch (IOException e) {
            LOGGER.error("Couldn't read googel analytisc file.", e);
        }
        return "";
    }

    private String getURLBase(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        StringBuffer url = new StringBuffer();
        url.append(scheme).append("://").append(serverName);
        if ((serverPort != 80) && (serverPort != 443)) {
            url.append(":").append(serverPort);
        }
        url.append("/gerbil/");
        return url.toString();
    }

    @SuppressWarnings("unused")
    @Deprecated
    private String getFullURL(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    protected static Matching getMatching(String matching) {
        String matchingName = matching.substring(matching.indexOf('-') + 1).trim().toUpperCase().replace(' ', '_');
        Matching m = Matching.valueOf(matchingName);
        return m;
    }

    @RequestMapping("/count")
    public @ResponseBody String count() {
        return Integer.toString(dao.countExperiments());
    }
}
