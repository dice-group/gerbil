package org.aksw.gerbil.bat.annotator;

import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.data.Mention;
import it.acubelab.batframework.data.Tag;
import it.acubelab.batframework.problems.A2WSystem;
import it.acubelab.batframework.systemPlugins.TimingCalibrator;
import it.acubelab.batframework.utils.AnnotationException;
import it.acubelab.batframework.utils.ProblemReduction;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.net.URLDecoder;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.aksw.fox.binding.java.FoxApi;
import org.aksw.fox.binding.java.FoxParameter;
import org.aksw.fox.binding.java.FoxResponse;
import org.aksw.fox.binding.java.IFoxApi;
import org.aksw.gerbil.utils.SingletonWikipediaApi;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONObject;

public class FOXAnnotator implements A2WSystem {
    static {
        PropertyConfigurator.configure(FOXAnnotator.class.getResourceAsStream("log4jFOXAnnotator.properties"));
    }

    public static final String    NAME     = "FOX";
    public static final Logger    LOG      = LogManager.getLogger(FOXAnnotator.class);
    IFoxApi                       fox      = new FoxApi();
    private WikipediaApiInterface wikiApi;
    private long                  calib    = -1;
    private long                  lastTime = -1;

    public static void main(String[] a) {
        String test = "The philosopher and mathematician Gottfried Wilhelm Leibniz was born in Leipzig.";
        HashSet<Annotation> set = new FOXAnnotator(SingletonWikipediaApi.getInstance()).solveA2W(test);
        LOG.info(set.size());
    }

    public FOXAnnotator(WikipediaApiInterface wikiApi) {
        this.wikiApi = wikiApi;
    }

    @Override
    public HashSet<Annotation> solveA2W(String text) throws AnnotationException {
        return (HashSet<Annotation>) fox(text);
    }

    @Override
    public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions) throws AnnotationException {
        return solveA2W(text);
    }

    @Override
    public HashSet<Tag> solveC2W(String text) throws AnnotationException {
        return ProblemReduction.A2WToC2W(solveA2W(text));
    }

    protected Set<Annotation> fox(String text) {

        Set<Annotation> set = new HashSet<>();
        lastTime = Calendar.getInstance().getTimeInMillis();
        try {
            // request FOX
            FoxResponse response = fox
                    .setInput(text)
                    .setLightVersion(FoxParameter.FOXLIGHT.OFF)
                    .setOutputFormat(FoxParameter.OUTPUT.JSONLD)
                    .setTask(FoxParameter.TASK.NER)
                    .send();

            // parse results
            if (response != null && response.getOutput() != null) {
                JSONObject outObj = new JSONObject(response.getOutput());
                if (outObj.has("@graph")) {
                    JSONArray graph = outObj.getJSONArray("@graph");
                    for (int i = 0; i < graph.length(); i++) {
                        JSONObject entity = graph.getJSONObject(i);
                        if (entity != null && entity.has("means") && entity.has("beginIndex") && entity.has("ann:body")) {

                            String uri = entity.getString("means");
                            String body = entity.getString("ann:body");
                            Object begin = entity.get("beginIndex");

                            // wiki id
                            String urlDecoded = URLDecoder.decode(uri, "UTF-8");
                            String title = extractLabel(urlDecoded);
                            int id = wikiApi.getIdByTitle(title);

                            if (id > -1) {
                                if (begin instanceof JSONArray) {
                                    // for all occurring indices
                                    for (int ii = 0; ii < ((JSONArray) begin).length(); ii++) {
                                        int b = Integer.valueOf(((JSONArray) begin).getString(ii));
                                        set.add(new Annotation(b, b + body.length(), id));
                                    }
                                } else if (begin instanceof String) {
                                    int b = Integer.valueOf((String) begin);
                                    set.add(new Annotation(b, b + body.length(), id));
                                } else if (LOG.isDebugEnabled())
                                    LOG.debug("Couldn't find index");
                            } else if (LOG.isDebugEnabled())
                                LOG.debug("Couldn't find ".concat(uri));
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("\n", e);
        }
        if (LOG.isDebugEnabled())
            LOG.debug("Found " + set.size());
        return set;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public long getLastAnnotationTime() {
        if (calib == -1)
            calib = TimingCalibrator.getOffset(this);
        return lastTime - calib > 0 ? lastTime - calib : 0;
    }

    private static String extractLabel(String namedEntityUri) {
        int posSlash = namedEntityUri.lastIndexOf('/');
        int posPoints = namedEntityUri.lastIndexOf(':');
        if (posSlash > posPoints) {
            return namedEntityUri.substring(posSlash + 1);
        } else if (posPoints < posSlash) {
            return namedEntityUri.substring(posPoints + 1);
        } else {
            return namedEntityUri;
        }
    }
}
