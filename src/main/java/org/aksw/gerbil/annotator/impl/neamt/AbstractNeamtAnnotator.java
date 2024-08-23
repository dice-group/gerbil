package org.aksw.gerbil.annotator.impl.neamt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.aksw.gerbil.annotator.http.AbstractHttpBasedAnnotator;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.SpanImpl;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Abstract annotator class for annotation systems that are hosted by <a href=
 * "https://github.com/dice-group/LFQA/tree/main/naive-eamt#na%C3%AFve-eamt-na%C3%AFve-entity-aware-machine-translation-framework">NEAMT</a>.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public abstract class AbstractNeamtAnnotator extends AbstractHttpBasedAnnotator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNeamtAnnotator.class);

    private static final String MEDIA_TYPE_STRING = ContentType.create("application/json", StandardCharsets.UTF_8)
            .toString();

    /**
     * Service URL.
     */
    protected String serviceUrl;
    /**
     * Component name as defined in the NEAMT service documentation.
     */
    protected String components;
    /**
     * Language tag.
     */
    protected String lang;

    public AbstractNeamtAnnotator(String serviceUrl, String components, String lang) {
        super();
        this.serviceUrl = serviceUrl;
        this.components = components;
        this.lang = lang;
    }

    protected Document request(Document document) throws GerbilException {
        String text = document.getText();
        String documentUri = document.getDocumentURI();
        LOGGER.info("Started request for {}", documentUri);
        HttpPost request = null;
        try {
            request = createPostRequest(serviceUrl);
        } catch (Exception e) {
            throw new GerbilException("Couldn't create HTTP request.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
        }

        JsonObject requestBody = createRequestBody(document);
        request.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));

        request.addHeader(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE_STRING);
        request.addHeader(HttpHeaders.ACCEPT, MEDIA_TYPE_STRING);

        HttpEntity entity = null;
        CloseableHttpResponse response = null;
        Document resultDoc = null;
        try {
            response = sendRequest(request);
            entity = response.getEntity();
            try {
                resultDoc = new DocumentImpl(text, documentUri);
                String content = IOUtils.toString(entity.getContent());
                JsonObject outJson = new JsonParser().parse(content).getAsJsonObject();
                parseMarkings(outJson, resultDoc);
            } catch (Exception e) {
                LOGGER.error("Couldn't parse the response.", e);
                throw new GerbilException("Couldn't parse the response.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
            }
        } finally {
            closeRequest(request);
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                } catch (IOException e1) {
                }
            }
            IOUtils.closeQuietly(response);
        }
        LOGGER.info("Finished request for {}", resultDoc.getDocumentURI());
        return resultDoc;
    }

    protected JsonObject createRequestBody(Document document) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("query", document.getText());
        requestBody.addProperty("components", components);
        requestBody.addProperty("full_json", true);
        requestBody.addProperty("lang", lang);
        return requestBody;
    }

    protected void parseMarkings(JsonObject outJson, Document resultDoc) {
        if (outJson.has("ent_mentions")) {
            JsonElement element = outJson.get("ent_mentions");
            if (element.isJsonArray()) {
                JsonArray mentions = element.getAsJsonArray();
                mentions.forEach(m -> parseMarking(m, resultDoc));
                return;
            }
        }
        LOGGER.warn("Couldn't find any mentions in the result \"{}\". It will be ignored.", outJson.toString());
    }

    protected void parseMarking(JsonElement mentionElement, Document resultDoc) {
        if (mentionElement.isJsonObject()) {
            JsonObject mentionObj = mentionElement.getAsJsonObject();
            // The marking should have start and end
            if (mentionObj.has("start") && mentionObj.has("end")) {
                int start = mentionObj.get("start").getAsInt();
                int end = mentionObj.get("end").getAsInt();
                String iri = null;
                // It may have a link
                if (mentionObj.has("link")) {
                    iri = mentionObj.get("link").getAsString();
                    if (iri.isEmpty()) {
                        iri = null;
                    } else {
                        // It is just the Wikidata ID, so we have to add the namespace
                        iri = "http://www.wikidata.org/entity/" + iri;
                    }
                }
                // If we have found no IRI, we have a Span, otherwise a NamedEntity
                if (iri == null) {
                    resultDoc.addMarking(new SpanImpl(start, end - start));
                } else {
                    resultDoc.addMarking(new NamedEntity(start, end - start, iri));
                }
                return; // We can return without problems
            }
        }
        // Something went wrong
        LOGGER.warn("Couldn't parse mention \"{}\". It will be ignored.", mentionElement.toString());
    }

}
