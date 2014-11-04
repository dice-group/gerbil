package org.aksw.gerbil.bat.annotator;

import com.google.gson.*;
import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.data.Mention;
import it.acubelab.batframework.data.ScoredAnnotation;
import it.acubelab.batframework.data.ScoredTag;
import it.acubelab.batframework.data.Tag;
import it.acubelab.batframework.problems.Sa2WSystem;
import it.acubelab.batframework.utils.AnnotationException;
import it.acubelab.batframework.utils.ProblemReduction;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class WATAnnotator implements Sa2WSystem {
    private long lastTime = 0;
    private final String endpoint;
    private final String urlParameters;
    private final String urlTag;
    private final String urlD2W;

    private Gson gson;
    private GsonBuilder gsonBuilder = new GsonBuilder();

    private HttpClient client = HttpClients.createDefault();
    private static final Logger LOGGER = LoggerFactory.getLogger(WATAnnotator.class);

    public WATAnnotator(String endpoint, String urlParameters) {
        this.endpoint = endpoint;
        this.urlTag = String.format("%s/tag/tag", endpoint);
        this.urlD2W = String.format("%s/tag/disambiguate", endpoint);
        this.urlParameters = urlParameters;
        this.gson = gsonBuilder.create();
    }

    @Override
    public HashSet<Annotation> solveA2W(String text) throws AnnotationException {
        return ProblemReduction.Sa2WToA2W(solveSa2W(text));
    }

    @Override
    public HashSet<Tag> solveC2W(String text) throws AnnotationException {
        return ProblemReduction.A2WToC2W(solveA2W(text));
    }

    @Override
    public String getName() {
        return String.format("WAT (endpoint=%s, ulrParameters=%s)", endpoint, urlParameters);
    }

    @Override
    public long getLastAnnotationTime() {
        return lastTime;
    }

    public HashSet<Annotation> solveD2WParams(String text, HashSet<Mention> mentions) throws AnnotationException {
        HashSet<Annotation> res = new HashSet<>();
        JsonObject obj;

        try {
            obj = queryJson(text, mentions, urlD2W);
        } catch (Exception e) {
            throw new AnnotationException("An error occurred while querying WAT API. Message: " + e.getMessage());
        }

        JsonArray jsAnnotations = obj.getAsJsonArray("annotations");
        for (int i = 0; i < jsAnnotations.size(); i++) {
            JsonObject js_ann = jsAnnotations.get(i).getAsJsonObject();

            int start = js_ann.get("start").getAsInt();
            int end = js_ann.get("end").getAsInt();
            int id = js_ann.get("id").getAsInt();

            Mention m = new Mention(start, end - start);
            if (mentions.contains(m))
                res.add(new Annotation(m.getPosition(), m.getLength(), id));
        }
        return res;
    }

    @Override
    public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions) throws AnnotationException {
        return solveD2WParams(text, mentions);
    }

    @Override
    public HashSet<ScoredTag> solveSc2W(String text) throws AnnotationException {
        HashSet<ScoredTag> res = new HashSet<>();
        JsonObject obj;

        try {
            obj = queryJson(text, null, urlTag);
        } catch (Exception e) {
            throw new AnnotationException("An error occurred while querying WAT API. Message: " + e.getMessage());
        }

        try {
            JsonArray jsAnnotations = obj.getAsJsonArray("annotations");
            for (int i = 0; i < jsAnnotations.size(); i++) {
                JsonObject js_ann = jsAnnotations.get(i).getAsJsonObject();
                JsonArray jsRanking = js_ann.getAsJsonArray("ranking");
                for (int j = 0; j < jsRanking.size(); j++) {
                    JsonObject jsCand = jsRanking.get(j).getAsJsonObject();
                    int id = jsCand.get("id").getAsInt();
                    double rho = jsCand.get("score").getAsDouble();
                    res.add(new ScoredTag(id, (float) rho));
                }
            }
        } catch (Exception e) {
            throw new AnnotationException(e.getMessage());
        }

        return res;
    }

    @Override
    public HashSet<ScoredAnnotation> solveSa2W(String text) throws AnnotationException {
        HashSet<ScoredAnnotation> res = new HashSet<>();
        JsonObject obj;

        try {
            obj = queryJson(text, null, urlTag);
        } catch (Exception e) {
            throw new AnnotationException("An error occurred while querying WAT API. Message: " + e.getMessage());
        }

        try {
            JsonArray jsAnnotations = obj.getAsJsonArray("annotations");
            for (int i = 0; i < jsAnnotations.size(); i++) {
                JsonObject js_ann = jsAnnotations.get(i).getAsJsonObject();

                int start = js_ann.get("start").getAsInt();
                int end = js_ann.get("end").getAsInt();
                int id = js_ann.get("id").getAsInt();
                double rho = js_ann.get("rho").getAsDouble();

                // Mention m = new Mention(start, end - start);
                res.add(new ScoredAnnotation(start, end - start, id, (float) rho));
            }
        } catch (Exception e) {
            throw new AnnotationException(e.getMessage());
        }
        return res;
    }

    private JsonObject queryJson(String text, Set<Mention> mentions, String url) throws IOException {

        JsonObject parameters = new JsonObject();

        if (mentions != null) {
            JsonArray mentionsJson = new JsonArray();
            for (Mention m : mentions) {
                JsonObject mentionJson = new JsonObject();
                mentionJson.addProperty("start", m.getPosition());
                mentionJson.addProperty("end", m.getPosition() + m.getLength());
                mentionsJson.add(mentionJson);
            }
            parameters.add("spans", mentionsJson);
        }
        parameters.addProperty("text", text);

        HttpPost request = new HttpPost(String.format("%s?%s", url, urlParameters));
        request.addHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(gson.toJson(parameters), "UTF8"));
        request.addHeader("Accept", "application/json");

        CloseableHttpResponse response = (CloseableHttpResponse) client.execute(request);
        InputStream is = null;
        HttpEntity entity = null;
        try {
            StatusLine status = response.getStatusLine();
            if ((status.getStatusCode() < 200) || (status.getStatusCode() >= 300)) {
                entity = response.getEntity();
                LOGGER.error("The response had a wrong status: \"" + status.toString() + "\". Content of response: \""
                        + IOUtils.toString(entity.getContent()) + "\". Returning null.");
                return null;
            }
            entity = response.getEntity();
            is = entity.getContent();
            return new JsonParser().parse(IOUtils.toString(is)).getAsJsonObject();
        } catch (Exception e) {
            LOGGER.error("Couldn't request annotation for given text. Returning null.", e);
        } finally {
            IOUtils.closeQuietly(is);
            if (entity != null) {
                EntityUtils.consume(entity);
            }
            if (response != null) {
                response.close();
            }
        }
        return null;
    }
}
