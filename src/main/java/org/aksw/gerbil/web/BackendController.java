package org.aksw.gerbil.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/backend")
public class BackendController {

    private static final Logger LOG                         = LoggerFactory.getLogger(BackendController.class);

    private static boolean      isInitialized               = false;
    private static final String GERBIL_VOCABULARY_JSON_FILE = "vocab/gerbil.json";
    private static final String GERBIL_VOCABULARY_RDF_FILE  = "vocab/gerbil.rdf";
    private static final String GERBIL_VOCABULARY_TTL_FILE  = "vocab/gerbil.ttl";

    private static final String GOOGLE_ANALYTICS_FILE_NAME  = "google1d91bc68c8a56517.html";

    @Autowired
    @Qualifier("experimentDAO")
    private ExperimentDAO       dao;

    // DataID URL is generated automatically in the experiment method?
    private DataIDGenerator     dataIdGenerator;

    @Autowired
    HttpServletRequest          request;

    @PostConstruct
    public void init() {
        synchronized (this) {
            if (!isInitialized) {
                String id = dao.getHighestExperimentId();
                if (id != null)
                    IDCreator.getInstance().setLastCreatedID(id);
                isInitialized = true;
            }
            // Simply call the dataset mapping so that it has to be instantiated
            DatasetMapping.getDatasetsForExperimentType(ExperimentType.Sa2W);
        }
    }

    /**
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(
            value = "/execute",
            headers = "Accept=application/json",
            produces = "application/json",
            method = RequestMethod.POST
            )
            public @ResponseBody
            String execute(HttpServletRequest request, HttpServletResponse response) {

        String experimentId = "-1";
        try {
            // check content
            if (!request.getContentType().contains("application/json")) {
                response.sendError(HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE, "application/json support only");
                response.flushBuffer();
            } else {
                // read content
                String line = "";
                String content = "";
                while (line != null) {
                    line = request.getReader().readLine();
                    content += line;
                }
                // parse content
                JSONObject data = new JSONObject(content);
                if (data.has("type") && data.has("matching") && data.has("annotator") && data.has("dataset")) {

                    JSONArray annotators = data.getJSONArray("annotator");
                    JSONArray datasets = data.getJSONArray("dataset");

                    List<ExperimentTaskConfiguration> cfg = new ArrayList<>();
                    for (int i = 0; i < annotators.length(); i++) {
                        for (int ii = 0; ii < datasets.length(); ii++) {
                            cfg.add(
                                    new ExperimentTaskConfiguration(
                                            AnnotatorMapping.getAnnotatorConfig(annotators.getString(i)),
                                            DatasetMapping.getDatasetConfig(datasets.getString(ii)),
                                            ExperimentType.valueOf(data.getString("type")),
                                            getMatching(data.getString("matching"))
                                    ));
                        }
                    }

                    experimentId = IDCreator.getInstance().createID();
                    Experimenter exp = new Experimenter(
                            SingletonWikipediaApi.getInstance(),
                            dao,
                            cfg.toArray(new ExperimentTaskConfiguration[annotators.length() * datasets.length()]),
                            experimentId);
                    exp.run();
                }
            }
        } catch (Exception e) {
            LOG.error("\n", e);
        }
        return new JSONObject().put("id", experimentId).toString();
    }

    // http://localhost:1234/gerbil/backend/experimentoverview?experimentType=D2W&matching=Mw%20-%20weak%20annotation%20match
    /**
     * 
     * @param experimentType
     * @param matchingString
     * @return
     */
    @RequestMapping(
            value = "/experimentoverview",
            method = RequestMethod.GET,
            produces = "application/json"
            )
            public @ResponseBody
            String experimentoverview(
                    @RequestParam(value = "experimentType") String experimentType,
                    @RequestParam(value = "matching") String matchingString) {

        LOG.debug("Got request on /experimentoverview(experimentType={}, matching={}", experimentType, matchingString);

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

        JSONArray a = new JSONArray();
        for (int i = 0; i < results.length; ++i) {
            JSONArray aa = new JSONArray();
            for (int j = 0; j < results[i].length; ++j)
                aa.put((results[i][j] != null) ? results[i][j] : "n.a.");
            a.put(aa);
        }
        return a.toString();
    }

    /**
     * 
     * @param id
     * @return
     */
    @RequestMapping(
            value = "/experiment",
            method = RequestMethod.GET,
            produces = "application/json"
            )
            public @ResponseBody
            String experiment(@RequestParam(value = "id") String id) {

        dataIdGenerator = new DataIDGenerator(getURLBase(), getFullURL());
        List<ExperimentTaskResult> results = dao.getResultsOfExperiment(id);
        ExperimentTaskStateHelper.setStatusLines(results);

        if (results.size() < 1)
            return new JSONObject().toString();
        else
            return new JSONObject()
                    .put("dataid", dataIdGenerator.createDataIDModel(results, id))
                    .put("tasks", results)
                    .toString();
    }

    /**
     * 
     * @return
     */
    @RequestMapping(
            value = "/exptypes",
            method = RequestMethod.GET,
            produces = "application/json"
            )
            public @ResponseBody
            String exptypes() {

        String me = "Me - strong entity match", mw = "Mw - weak annotation match", ma = "Ma - strong annotation match";
        return new JSONObject()
                .put(ExperimentType.Rc2W.name(),
                        new JSONObject()
                                .put("name", ExperimentType.Rc2W.name())
                                .put("matching", new JSONArray().put(me))
                                .put("annotator", AnnotatorMapping.getAnnotatorsForExperimentType(ExperimentType.Rc2W))
                                .put("datasets", DatasetMapping.getDatasetsForExperimentType(ExperimentType.Rc2W))
                )
                .put(ExperimentType.D2W.name(),
                        new JSONObject()
                                .put("name", ExperimentType.D2W.name())
                                .put("matching", new JSONArray().put(ma))
                                .put("annotator", AnnotatorMapping.getAnnotatorsForExperimentType(ExperimentType.D2W))
                                .put("datasets", DatasetMapping.getDatasetsForExperimentType(ExperimentType.D2W))
                )
                .put(ExperimentType.C2W.name(),
                        new JSONObject()
                                .put("name", ExperimentType.C2W.name())
                                .put("matching", new JSONArray().put(me))
                                .put("annotator", AnnotatorMapping.getAnnotatorsForExperimentType(ExperimentType.C2W))
                                .put("datasets", DatasetMapping.getDatasetsForExperimentType(ExperimentType.C2W))
                )
                .put(ExperimentType.A2W.name(),
                        new JSONObject()
                                .put("name", ExperimentType.A2W.name())
                                .put("matching", new JSONArray().put(mw))
                                .put("annotator", AnnotatorMapping.getAnnotatorsForExperimentType(ExperimentType.A2W))
                                .put("datasets", DatasetMapping.getDatasetsForExperimentType(ExperimentType.A2W))
                )
                .put(ExperimentType.Rc2W.name(),
                        new JSONObject()
                                .put("name", ExperimentType.Rc2W.name())
                                .put("matching", new JSONArray().put(me))
                                .put("annotator", AnnotatorMapping.getAnnotatorsForExperimentType(ExperimentType.Rc2W))
                                .put("datasets", DatasetMapping.getDatasetsForExperimentType(ExperimentType.Rc2W))
                )
                .put(ExperimentType.Sc2W.name(),
                        new JSONObject()
                                .put("name", ExperimentType.Sc2W.name())
                                .put("matching", new JSONArray().put(me))
                                .put("annotator", AnnotatorMapping.getAnnotatorsForExperimentType(ExperimentType.Sc2W))
                                .put("datasets", DatasetMapping.getDatasetsForExperimentType(ExperimentType.Sc2W))
                )
                .put(ExperimentType.Sa2W.name(),
                        new JSONObject()
                                .put("name", ExperimentType.Sa2W.name())
                                .put("matching", new JSONArray().put(mw).put(ma))
                                .put("annotator", AnnotatorMapping.getAnnotatorsForExperimentType(ExperimentType.Sa2W))
                                .put("datasets", DatasetMapping.getDatasetsForExperimentType(ExperimentType.Sa2W))
                ).toString();
    }

    /**
     * 
     * @return
     */
    @RequestMapping(
            value = "/running",
            method = RequestMethod.GET,
            produces = "application/json"
            )
            public @ResponseBody
            String running() {
        JSONArray jsonarray = new JSONArray();
        for (ExperimentTaskResult runningTask : dao.getAllRunningExperimentTasks())
            jsonarray.put(new JSONObject()
                    .put("type", runningTask.type)
                    .put("matching", runningTask.matching)
                    .put("annotator", runningTask.annotator)
                    .put("dataset", runningTask.dataset));
        return jsonarray.toString();
    }

    /**
     * 
     * @return
     */
    @RequestMapping(
            value = "/vocab*",
            produces = { "application/json+ld", "application/json" },
            method = RequestMethod.GET
            )
            public @ResponseBody
            String vocabularyAsJSON() {
        return getResourceAsString(GERBIL_VOCABULARY_JSON_FILE);
    }

    /**
     * 
     * @return
     */
    @RequestMapping(
            value = "/vocab*",
            produces = "application/rdf+xml",
            method = RequestMethod.GET
            )
            public @ResponseBody
            String vocabularyAsRDFXML() {
        return getResourceAsString(GERBIL_VOCABULARY_RDF_FILE);
    }

    /**
     * 
     * @return
     */
    @RequestMapping(
            value = "/vocab*",
            produces = { "text/turtle", "text/plain" },
            method = RequestMethod.GET
            )
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
    @RequestMapping(
            value = "/google*",
            produces = "text/html",
            method = RequestMethod.GET
            )
            public @ResponseBody
            String googleAnalyticsFile() {
        try {
            return FileUtils.readFileToString(new File(GOOGLE_ANALYTICS_FILE_NAME));
        } catch (IOException e) {
            LOG.error("Couldn't read googel analytisc file.", e);
        }
        return "";
    }

    // private
    private String getResourceAsString(String resource) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(resource);
        if (is != null) {
            try {
                return IOUtils.toString(is, "utf-8");
            } catch (IOException e) {
                LOG.error("Exception while loading vocabulary resource. Returning null.", e);
            } finally {
                IOUtils.closeQuietly(is);
            }
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

    private Matching getMatching(String matching) {
        return Matching.valueOf(
                matching.substring(matching.indexOf('-') + 1).trim().toUpperCase().replace(' ', '_')
                );
    }
}
