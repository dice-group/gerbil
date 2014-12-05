/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
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
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.aksw.gerbil.Experimenter;
import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.dataid.DataIDGenerator;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.utils.AnnotatorMapping;
import org.aksw.gerbil.utils.DatasetMapping;
import org.aksw.gerbil.utils.IDCreator;
import org.aksw.gerbil.utils.SingletonWikipediaApi;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;

@Controller
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private static final String GERBIL_VOCABULARY_JSON_FILE = "vocab/gerbil.json";
    private static final String GERBIL_VOCABULARY_RDF_FILE = "vocab/gerbil.rdf";
    private static final String GERBIL_VOCABULARY_TTL_FILE = "vocab/gerbil.ttl";
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
        DatasetMapping.getDatasetsForExperimentType(ExperimentType.Sa2KB);
    }

    @PostConstruct
    public void init() {
        initialize(dao);
    }

    @Autowired
    @Qualifier("experimentDAO")
    private ExperimentDAO dao;

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

    @RequestMapping("/experimentoverview")
    public @ResponseBody
    String experimentoverview(@RequestParam(value = "experimentType") String experimentType,
            @RequestParam(value = "matching") String matchingString) {
        LOGGER.debug("Got request on /experimentoverview(experimentType={}, matching={}", experimentType,
                matchingString);
        Matching matching = getMatching(matchingString);
        ExperimentType eType = ExperimentType.valueOf(experimentType);
        Set<String> annotators = AnnotatorMapping.getAnnotatorsForExperimentType(eType);
        Set<String> datasets = DatasetMapping.getDatasetsForExperimentType(eType);
        String results[][] = new String[annotators.size() + 1][datasets.size() + 1];
        results[0][0] = "Micro F1-measure";
        Map<String, Integer> annotator2Index = new HashMap<String, Integer>();
        int count = 1;
        for (String annotator : annotators) {
            annotator2Index.put(annotator, count);
            results[count][0] = annotator;
            ++count;
        }
        Map<String, Integer> dataset2Index = new HashMap<String, Integer>();
        count = 1;
        for (String dataset : datasets) {
            dataset2Index.put(dataset, count);
            results[0][count] = dataset;
            ++count;
        }

        List<ExperimentTaskResult> expResults = dao.getLatestResultsOfExperiments(experimentType, matching.name());
        int row, col;
        for (ExperimentTaskResult result : expResults) {
            if (annotator2Index.containsKey(result.annotator) && dataset2Index.containsKey(result.dataset)) {
                row = annotator2Index.get(result.annotator);
                col = dataset2Index.get(result.dataset);
                if (result.state == ExperimentDAO.TASK_FINISHED) {
                    results[row][col] = String.format(Locale.US, "%.3f", result.getMicroF1Measure());
                } else {
                    results[row][col] = "error (" + result.state + ")";
                }
            }
        }
        StringBuilder dataBuilder = new StringBuilder();
        for (int i = 0; i < results.length; ++i) {
            dataBuilder.append(i == 0 ? '[' : ',');
            for (int j = 0; j < results[i].length; ++j) {
                dataBuilder.append(j == 0 ? "[\"" : "\",\"");
                if (results[i][j] != null) {
                    dataBuilder.append(results[i][j]);
                } else {
                    dataBuilder.append("n.a.");
                }
            }
            dataBuilder.append("\"]");
        }
        dataBuilder.append(']');
        return dataBuilder.toString();
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
    public @ResponseBody
    String execute(@RequestParam(value = "experimentData") String experimentData) {
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
        for (String annotator : annotators) {
            for (String dataset : datasets) {
                configs[count] = new ExperimentTaskConfiguration(AnnotatorMapping.getAnnotatorConfig(annotator),
                        DatasetMapping.getDatasetConfig(dataset), ExperimentType.valueOf(type), getMatching(matching));
                LOGGER.debug("Created config: " + configs[count]);
                ++count;
            }
        }
        String experimentId = IDCreator.getInstance().createID();
        Experimenter exp = new Experimenter(SingletonWikipediaApi.getInstance(), dao, configs, experimentId);
        exp.run();

        return experimentId;
    }

    private Matching getMatching(String matching) {
        String matchingName = matching.substring(matching.indexOf('-') + 1).trim().toUpperCase().replace(' ', '_');
        Matching m = Matching.valueOf(matchingName);
        return m;
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
    public @ResponseBody
    List<String> expTypes() {
        List<ExperimentType> list = Arrays.asList(ExperimentType.values());
        List<String> names = Lists.newArrayList();
        for (ExperimentType name : list) {
            names.add(name.toString());
        }
        Collections.sort(names);
        return names;
    }

    @RequestMapping("/matchings")
    public @ResponseBody
    List<String> matchingsForExpType(@RequestParam(value = "experimentType") String experimentType) {
        ExperimentType type = ExperimentType.valueOf(experimentType);
        switch (type) {
        case C2KB:
            return Lists.newArrayList("Me - strong entity match");
        case D2KB:
            // Mw will not be shown since the positions are always exact and
            // thus it works like Ma
            return Lists.newArrayList("Ma - strong annotation match");
        case A2KB:
            return Lists.newArrayList("Mw - weak annotation match", "Ma - strong annotation match");
        case Rc2KB:
            return Lists.newArrayList("Me - strong entity match");
        case Sc2KB:
            return Lists.newArrayList("Me - strong entity match");
        case Sa2KB:
            return Lists.newArrayList("Mw - weak annotation match", "Ma - strong annotation match");
        default:
            return Lists.newArrayList("None");
        }
    }

    @RequestMapping("/annotators")
    public @ResponseBody
    List<String> annotatorsForExpType(@RequestParam(value = "experimentType") String experimentType) {
        Set<String> annotatorsForExperimentType = AnnotatorMapping.getAnnotatorsForExperimentType(ExperimentType
                .valueOf(experimentType));
        List<String> list = Lists.newArrayList(annotatorsForExperimentType);
        Collections.sort(list);
        return list;
    }

    @RequestMapping("/datasets")
    public @ResponseBody
    List<String> datasets(@RequestParam(value = "experimentType") String experimentType) {
        Set<String> datasets = DatasetMapping.getDatasetsForExperimentType(ExperimentType.valueOf(experimentType));
        List<String> list = Lists.newArrayList(datasets);
        Collections.sort(list);
        return list;
    }

    @RequestMapping("/running")
    public @ResponseBody
    String running() {
        List<ExperimentTaskResult> runningTasks = dao.getAllRunningExperimentTasks();
        StringBuilder resultBuilder = new StringBuilder();
        for (ExperimentTaskResult runningTask : runningTasks) {
            resultBuilder.append("<p>");
            resultBuilder.append(runningTask.type);
            resultBuilder.append(' ');
            resultBuilder.append(runningTask.matching);
            resultBuilder.append(' ');
            resultBuilder.append(runningTask.annotator);
            resultBuilder.append(' ');
            resultBuilder.append(runningTask.dataset);
            resultBuilder.append("</p>\n");
        }
        return resultBuilder.toString();
    }

    @RequestMapping(value = "/vocab*", produces = { "application/json+ld", "application/json" })
    public @ResponseBody
    String vocabularyAsJSON() {
        return getResourceAsString(GERBIL_VOCABULARY_JSON_FILE);
    }

    @RequestMapping(value = "/vocab*", produces = "application/rdf+xml")
    public @ResponseBody
    String vocabularyAsRDFXML() {
        return getResourceAsString(GERBIL_VOCABULARY_RDF_FILE);
    }

    @RequestMapping(value = "/vocab*", produces = { "text/turtle", "text/plain" })
    public @ResponseBody
    String vocabularyAsTTL() {
        return getResourceAsString(GERBIL_VOCABULARY_TTL_FILE);
    }

    /**
     * This mapping is needed to authenticate us against Google Analytics. It
     * reads the google file and sends it as String.
     * 
     * @return The google analytics file as String or an empty String if the
     *         file couldn't be loaded.
     */
    @RequestMapping(value = "/google*")
    public @ResponseBody
    String googleAnalyticsFile() {
        try {
            return FileUtils.readFileToString(new File(GOOGLE_ANALYTICS_FILE_NAME));
        } catch (IOException e) {
            LOGGER.error("Couldn't read googel analytisc file.", e);
        }
        return "";
    }

    private String getResourceAsString(String resource) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(resource);
        if (is != null) {
            try {
                return IOUtils.toString(is, "utf-8");
            } catch (IOException e) {
                LOGGER.error("Exception while loading vocabulary resource. Returning null.", e);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
        return null;
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
}
