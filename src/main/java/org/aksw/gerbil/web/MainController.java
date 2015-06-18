/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
import org.aksw.gerbil.annotators.AnnotatorConfiguration;
import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.dataid.DataIDGenerator;
import org.aksw.gerbil.datasets.DatasetConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.utils.DatasetMapping;
import org.aksw.gerbil.utils.IDCreator;
import org.aksw.gerbil.web.config.AdapterList;
import org.aksw.gerbil.web.config.AdapterManager;
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
//        DatasetMapping.getDatasetsForExperimentType(ExperimentType.EExt);
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
    private EvaluatorFactory evFactory;

    @Autowired
    @Qualifier("getAnnotators")
    private AdapterList<AnnotatorConfiguration> annotators;

    @Autowired
    @Qualifier("getDatasets")
    private AdapterList<DatasetConfiguration> datasets;
    
    @Autowired
    private AdapterManager adapterManager;

    // DataID URL is generated automatically in the experiment method?
    private DataIDGenerator dataIdGenerator;

    @Autowired
    HttpServletRequest request;

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
     * expects a string like
     * {"type":"A2KB","matching":"Mw - weak annotation match"
     * ,"annotator":["A2KB one","A2KB two"],"dataset":["datasets"]}
     * 
     * @param experimentData
     * @return
     */

    @RequestMapping("/execute")
    public @ResponseBody String execute(@RequestParam(value = "experimentData") String experimentData) {
        LOGGER.debug("Got request on /execute with experimentData=" + experimentData);
        Object obj = JSONValue.parse(experimentData);
        JSONObject configuration = (JSONObject) obj;
        String type = (String) configuration.get("type");
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
        ExperimentType expType = ExperimentType.valueOf(type);
        for (String annotator : annotators) {
            for (String dataset : datasets) {
                configs[count] = new ExperimentTaskConfiguration(getAdapterConfig(annotator, expType),
                        getDatasetConfig(dataset, expType), expType, getMatching(matching));
                LOGGER.debug("Created config: " + configs[count]);
                ++count;
            }
        }
        String experimentId = IDCreator.getInstance().createID();
        Experimenter exp = new Experimenter(overseer, dao, evFactory, configs, experimentId);
        exp.run();

        return experimentId;
    }

    private DatasetConfiguration getDatasetConfig(String dataset, ExperimentType expType) {
        // TODO Auto-generated method stub
        return null;
    }

    private AnnotatorConfiguration getAdapterConfig(String annotator, ExperimentType expType) {
        // TODO Auto-generated method stub
        return null;
    }

    @RequestMapping("/experiment")
    public ModelAndView experiment(@RequestParam(value = "id") String id) {
        LOGGER.debug("Got request on /experiment with id=" + id);
        dataIdGenerator = new DataIDGenerator(getURLBase(), getFullURL());
        List<ExperimentTaskResult> results = dao.getResultsOfExperiment(id);
        ExperimentTaskStateHelper.setStatusLines(results);
        ModelAndView model = new ModelAndView();
        model.setViewName("experiment");
        model.addObject("tasks", results);
        model.addObject("dataid", dataIdGenerator.createDataIDModel(results, id));
        return model;
    }

    @RequestMapping("/exptypes")
    public @ResponseBody ModelMap expTypes() {
        List<ExperimentType> names = Lists.newArrayList();
        for (ExperimentType type : ExperimentType.values()) {
            try {
                if (ExperimentType.class.getDeclaredField(type.name()).getAnnotation(Deprecated.class) == null)
                    names.add(type);
            } catch (Exception e) {
                LOGGER.error("Couldn't check availability of ExperimentType " + type.toString(), e);
            }
        }
        Collections.sort(names);
        return new ModelMap("ExperimentType", names.toArray(new ExperimentType[names.size()]));
    }

    // @RequestMapping("/exptypes")
    // public @ResponseBody ModelMap expTypes() {
    // ModelMap model = new ModelMap("ExperimentType", ExperimentType.values());
    // return model;
    // }

    @SuppressWarnings("deprecation")
    @RequestMapping("/matchings")
    public @ResponseBody ModelMap matchingsForExpType(@RequestParam(value = "experimentType") String experimentType) {
        ExperimentType type = ExperimentType.valueOf(experimentType);
        switch (type) {
        case C2KB:
            return new ModelMap("Matching", Lists.newArrayList(Matching.STRONG_ENTITY_MATCH));
        case D2KB:
        case ELink:
        case ETyping:
            // Mw will not be shown since the positions are always exact and
            // thus it works like Ma
            return new ModelMap("Matching", Lists.newArrayList(Matching.STRONG_ANNOTATION_MATCH));
        case Rc2KB:
        case Sc2KB:
            return new ModelMap("Matching", Lists.newArrayList(Matching.STRONG_ENTITY_MATCH));
        case OKE_Task1:
        case OKE_Task2:
        case EExt:
        case ERec:
        case Sa2KB:
        case A2KB:
            return new ModelMap("Matching", Lists.newArrayList(Matching.WEAK_ANNOTATION_MATCH,
                    Matching.STRONG_ANNOTATION_MATCH));
        default:
            return new ModelMap("Matching", Lists.newArrayList("none"));
        }
    }

    @RequestMapping("/annotators")
    public @ResponseBody List<String> annotatorsForExpType(@RequestParam(value = "experimentType") String experimentType) {
        Set<String> annotatorsForExperimentType = annotators.getAdapterNamesForExperiment(ExperimentType
                .valueOf(experimentType));
        List<String> list = Lists.newArrayList(annotatorsForExperimentType);
        Collections.sort(list);
        return list;
    }

    @RequestMapping("/datasets")
    public @ResponseBody List<String> datasets(@RequestParam(value = "experimentType") String experimentType) {
        Set<String> datasets = DatasetMapping.getDatasetsForExperimentType(ExperimentType.valueOf(experimentType));
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

    private String getURLBase() {
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

    private String getFullURL() {
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
