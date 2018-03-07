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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.aksw.gerbil.Experimenter;
import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.database.ResultNameToIdMapping;
import org.aksw.gerbil.dataid.DataIDGenerator;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
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

    private static boolean isInitialized = false;

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

    // DataID URL is generated automatically in the experiment method?
    private DataIDGenerator dataIdGenerator;

    private ExperimentType[] availableExperimentTypes = RootConfig.getAvailableExperimentTypes();

    private AnnotatorOutputWriter annotatorOutputWriter = RootConfig.getAnnotatorOutputWriter();

    @RequestMapping("/config")
    public ModelAndView config() {
        ModelAndView model = new ModelAndView();
        model.setViewName("config");
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
        ExperimentTaskConfiguration[] configs = new ExperimentTaskConfiguration[annotators.length * datasets.length];
        int count = 0;
        for (String annotator : annotators) {
            for (String dataset : datasets) {
                configs[count] = new ExperimentTaskConfiguration(adapterManager.getAnnotatorConfig(annotator, type),
                        adapterManager.getDatasetConfig(dataset, type), type, getMatching(matching));
                LOGGER.debug("Created config: {}", configs[count]);
                ++count;
            }
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
        dataIdGenerator = new DataIDGenerator(getURLBase(request));
        List<ExperimentTaskResult> results = dao.getResultsOfExperiment(id);
        ExperimentTaskStateHelper.setStatusLines(results);
        ModelAndView model = new ModelAndView();
        model.setViewName("experiment");
        model.addObject("tasks", results);
        int currentExperimentID=-1;
        int currentState = 0;
        List<ExperimentTaskResult> tasks = dao.getAllRunningExperimentTasks();
        for(ExperimentTaskResult r : results){
        	if(r.state==0){
        		continue;
        	}
        	if(tasks.contains(r)){
        		currentState = r.state;
        		currentExperimentID = tasks.indexOf(r);
        		break;
        	}
        }
        model.addObject("currentState", currentState);
        model.addObject("currentExperimentID", currentExperimentID);
        model.addObject("workers", RootConfig.getNoOfWorkers());
        model.addObject("dataid", dataIdGenerator.createDataIDModel(results, id));
        int additionalResultIds[] = ResultNameToIdMapping.getInstance().listAdditionalResultIds(results);
        // we need Double objects to make sure that they can be null
        Double additionalResults[][] = new Double[results.size()][additionalResultIds.length];
        ExperimentTaskResult result;
        for (int i = 0; i < additionalResults.length; ++i) {
            result = results.get(i);
            for (int j = 0; j < additionalResultIds.length; ++j) {
                if (result.hasAdditionalResult(additionalResultIds[j])) {
                    additionalResults[i][j] = result.getAdditionalResult(additionalResultIds[j]);
                }
            }
        }
        model.addObject("additionalResultNames",
                ResultNameToIdMapping.getInstance().getNamesOfResultIds(additionalResultIds));
        model.addObject("additionalResults", additionalResults);
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
        case C2KB:
            return new ModelMap("Matching", Lists.newArrayList(Matching.STRONG_ENTITY_MATCH));
        case D2KB:
        case ETyping:
            // Mw will not be shown since the positions are always exact and
            // thus it works like Ma
            return new ModelMap("Matching", Lists.newArrayList(Matching.STRONG_ANNOTATION_MATCH));
        case Rc2KB:
        case Sc2KB:
            return new ModelMap("Matching", Lists.newArrayList(Matching.STRONG_ENTITY_MATCH));
        case OKE_Task1:
        case OKE_Task2:
        case A2KB:
        case ERec:
        case OKE2018Task4:
        case RE:
        case RT2KB:
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
}
