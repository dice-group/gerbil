package org.aksw.gerbil.ws4test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Annotation;
import org.aksw.gerbil.transfer.nif.data.DisambiguatedAnnotation;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class DBpediaSpotlightClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBpediaSpotlightClient.class);

    public static void main(String[] args) throws ClientProtocolException, IOException {
        DBpediaSpotlightClient client = new DBpediaSpotlightClient("spotlight.dbpedia.org:80");
        System.out.println(client.request("Barack Obama in Washington"));
        System.out.println(client.annotate("Barack Obama in Washington"));
    }

    private String endpoint;
    private HttpClient client = HttpClients.createDefault();
    private GsonBuilder gsonBuilder = new GsonBuilder();
    private Gson gson;

    public DBpediaSpotlightClient(String endpoint) {
        this.endpoint = endpoint;
        gsonBuilder.registerTypeAdapter(List.class, new AnnotationsDeserializer());
        gson = gsonBuilder.create();
    }

    protected String request(String text) throws ClientProtocolException, IOException {
        HttpGet request = new HttpGet("http://" + endpoint + "/rest/annotate?text=" + URLEncoder.encode(text, "utf-8"));
        request.setHeader("Accept", "application/json");

        CloseableHttpResponse response = (CloseableHttpResponse) client.execute(request);
        InputStream is = null;
        HttpEntity entity = null;
        try {
            StatusLine status = response.getStatusLine();
            if ((status.getStatusCode() < 200) || (status.getStatusCode() >= 300)) {
                LOGGER.error("The response had a wrong status: \"" + status.toString() + "\". Returning null");
                return null;
            }
            entity = response.getEntity();
            is = entity.getContent();
            return IOUtils.toString(is);
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

    protected List<Annotation> parseResponse(String response) {
        if (response == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        List<Annotation> annotations = gson.fromJson(response, List.class);
        return annotations;
    }

    public List<Annotation> annotate(String text) {
        if (text == null) {
            return null;
        }
        text = text.trim();

        if (text.length() == 0) {
            return new ArrayList<Annotation>(0);
        }
        try {
            return parseResponse(request(text));
        } catch (Exception e) {
            LOGGER.error("Got an exception while trying to annotate the text. Returning null", e);
            return null;
        }
    }

    private static class AnnotationsDeserializer implements JsonDeserializer<List<Annotation>> {
        private static final String NAMED_ENTITIES_ARRAY_KEY = "Resources";
        private static final String NAMED_ENTITY_URI_KEY = "@URI";
        private static final String NAMED_ENTITY_OFFSET_KEY = "@offset";
        private static final String NAMED_ENTITY_SURFACE_FORM_KEY = "@surfaceForm";

        public List<Annotation> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject responseAsObject = json.getAsJsonObject();
            List<Annotation> annotations = new ArrayList<Annotation>();
            if (responseAsObject.has(NAMED_ENTITIES_ARRAY_KEY)) {
                JsonArray resources = responseAsObject.get(NAMED_ENTITIES_ARRAY_KEY).getAsJsonArray();
                JsonObject annotation;
                for (JsonElement resource : resources) {
                    annotation = resource.getAsJsonObject();
                    if (annotation.has(NAMED_ENTITY_URI_KEY) && annotation.has(NAMED_ENTITY_OFFSET_KEY)
                            && annotation.has(NAMED_ENTITY_SURFACE_FORM_KEY)) {
                        annotations.add(new DisambiguatedAnnotation(annotation.get(NAMED_ENTITY_OFFSET_KEY).getAsInt(),
                                annotation.get(NAMED_ENTITY_SURFACE_FORM_KEY).getAsString().length(), annotation
                                        .get(NAMED_ENTITY_URI_KEY).getAsString()));
                    }
                }
            }
            return annotations;
        }
    }
}
