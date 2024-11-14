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
import java.util.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.aksw.gerbil.Experimenter;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.dataid.DataIDGenerator;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskStatus;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.exceptions.FaultyConfigurationException;
import org.aksw.gerbil.execute.AnnotatorOutputWriter;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.utils.IDCreator;
import org.aksw.gerbil.web.config.AdapterManager;
import org.aksw.gerbil.web.config.RootConfig;
import org.aksw.gerbil.web.response.execution.ExperimentExecutionResponse;
import org.aksw.simba.topicmodeling.concurrent.overseers.Overseer;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
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

    private static final String RESNAME_PROP = "org.aksw.gerbil.database.ResultNameSequence";

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
     * expects a string like {"type":"A2KB","matching": "Mw - weak annotation match"
     * ,"annotator":["A2KB one","A2KB two" ],"dataset":["datasets"]}
     *
     * @param experimentData
     * @return
     */
    @GetMapping(value = "/execute")
    public ResponseEntity<String> execute(@RequestParam(value = "experimentData") String experimentData) throws JsonProcessingException {
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
        try{
            exp.run();
        }catch(FaultyConfigurationException e){
            ObjectMapper objectMapper = new ObjectMapper();
            if(e.getFaultyConfigs().size()==configs.length){
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).
                    body(objectMapper.writeValueAsString(new ExperimentExecutionResponse(e.getMessage())));
            }else{
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).
                    body(objectMapper.writeValueAsString(new ExperimentExecutionResponse(experimentId,e.getMessage())));
            }
        }
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(experimentId);
    }

    @RequestMapping("/experiment")
    public ModelAndView experiment(@RequestParam(value = "id") String id, HttpServletRequest request) {
        LOGGER.debug("Got request on /experiment with id={}", id);
        dataIdGenerator = new DataIDGenerator(getURLBase(request));
        List<ExperimentTaskStatus> results = dao.getResultsOfExperiment(id);

        ExperimentTaskStateHelper.setStatusLines(results);
        ModelAndView model = new ModelAndView();
        model.setViewName("experiment");
        model.addObject("tasks", results);
        int precedingTaskCount = 0;

        // Initializing set of resNames from DB
        Set<String> resNamesDb = new HashSet<>();

        // List<ExperimentTaskStatus> tasks = dao.getAllRunningExperimentTasks();
        boolean isRunning = false;
        for (ExperimentTaskStatus r : results) {
            resNamesDb.addAll(r.resultsMap.keySet());
            if (!isRunning && r.state == ExperimentDAO.TASK_STARTED_BUT_NOT_FINISHED_YET) {
                isRunning = true;
            }
        }
        if (isRunning) {
            // Fetch the Id of the last record
            int lastTaskId = results.get(results.size() - 1).idInDb;
            // Fetch the total number of running tasks before the last record of current
            // experiment
            precedingTaskCount = dao.countPrecedingRunningTasks(lastTaskId);
        }
        // Fetch Result Name sequence from property file
        String[] resNamesDlmtd = GerbilConfiguration.getInstance().getStringArray(RESNAME_PROP);
        List<String> fnlRsltNms = new ArrayList<>();
        boolean isRemoved = false;
        for (String seqEntry : resNamesDlmtd) {
            isRemoved = resNamesDb.remove(seqEntry);
            if (isRemoved) {
                fnlRsltNms.add(seqEntry);
            }
        }
        fnlRsltNms.addAll(resNamesDb);
        model.addObject("resultNames", fnlRsltNms);
        model.addObject("isRunning", isRunning);
        // Number of Preceding running experiment tasks
        model.addObject("precedingTaskCount", precedingTaskCount);
        model.addObject("workers", RootConfig.getNoOfWorkers());
        model.addObject("dataid", dataIdGenerator.createDataIDModel(results, id));
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
        case RE:
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
    public @ResponseBody Map<String, List<String>> datasets(@RequestParam(value = "experimentType") String experimentType) {
        ExperimentType type = null;
        Map<String, List<String>> response = new TreeMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            type = ExperimentType.valueOf(experimentType);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Got a request containing a wrong ExperimentType (\"{}\"). Ignoring it.", experimentType);
            return null;
        }
        List<String> adapterDetailsJsonList = adapterManager.getDatasetDetailsForExperiment(type);
        for (String adapterJson : adapterDetailsJsonList) {
            try {
                Map<String, String> adapterDetails = mapper.readValue(adapterJson, new TypeReference<Map<String, String>>() {});
                String name = adapterDetails.get("name");
                String group = adapterDetails.get("group");
                response.computeIfAbsent(group, k -> new ArrayList<>()).add(name);
                Collections.sort(response.get(group));
            } catch (IOException e) {
                LOGGER.error("Failed to parse adapter details JSON: {}", adapterJson, e);
            }
        }
        return response;
    }

    /**
     * This mapping is needed to authenticate us against Google Analytics. It reads
     * the google file and sends it as String.
     *
     * @return The google analytics file as String or an empty String if the file
     *         couldn't be loaded.
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
