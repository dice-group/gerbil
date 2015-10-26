package org.aksw.gerbil.annotator.impl.agdistis;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.annotator.EntityLinker;
import org.aksw.gerbil.annotator.http.AbstractHttpBasedAnnotator;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.StartPosBasedComparator;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgdistisAnnotator extends AbstractHttpBasedAnnotator implements EntityLinker {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgdistisAnnotator.class);

    private static final String AGDISTIS_HOST_PROPERTY_NAME = "org.aksw.gerbil.annotators.AgdistisAnnotatorConfig.Host";
    private static final String AGDISTIS_PORT_PROPERTY_NAME = "org.aksw.gerbil.annotators.AgdistisAnnotatorConfig.Port";

    protected String host;
    protected int port;
    protected JSONParser jsonParser = new JSONParser();
    protected CloseableHttpClient client = HttpClients.createDefault();

    public AgdistisAnnotator() throws GerbilException {
        name = "AGDISTIS";
        String host = GerbilConfiguration.getInstance().getString(AGDISTIS_HOST_PROPERTY_NAME);
        if (host == null) {
            throw new GerbilException("Couldn't load needed property \"" + AGDISTIS_HOST_PROPERTY_NAME + "\".",
                    ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
        String portString = GerbilConfiguration.getInstance().getString(AGDISTIS_PORT_PROPERTY_NAME);
        if (portString == null) {
            throw new GerbilException("Couldn't load needed property \"" + AGDISTIS_PORT_PROPERTY_NAME + "\".",
                    ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
        int port;
        try {
            port = Integer.parseInt(portString);
        } catch (Exception e) {
            throw new GerbilException(
                    "Couldn't parse the integer of the property \"" + AGDISTIS_PORT_PROPERTY_NAME + "\".", e,
                    ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
        this.host = host;
        this.port = port;
    }

    public AgdistisAnnotator(String host, String portString) throws GerbilException {
        this.host = host;
        int port;
        try {
            port = Integer.parseInt(portString);
        } catch (Exception e) {
            throw new GerbilException(
                    "Couldn't parse the integer of the property \"" + AGDISTIS_PORT_PROPERTY_NAME + "\".", e,
                    ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
        this.port = port;
    }

    @Override
    public List<MeaningSpan> performLinking(Document document) throws GerbilException {
        String textWithMentions = createTextWithMentions(document.getText(), document.getMarkings(Span.class));
        return getAnnotations(textWithMentions);
    }

    static String createTextWithMentions(String text, List<Span> mentions) {
        // Example: 'The <entity>University of Leipzig</entity> in
        // <entity>Barack Obama</entity>.'

        Collections.sort(mentions, new StartPosBasedComparator());

        StringBuilder textBuilder = new StringBuilder();
        int lastPos = 0;
        for (int i = 0; i < mentions.size(); i++) {
            Span span = mentions.get(i);

            int begin = span.getStartPosition();
            int end = begin + span.getLength();

            if (begin < lastPos) {
                // we have two overlapping mentions --> take the larger one
                Span prev = mentions.get(i - 1);
                LOGGER.warn("\"%s\" at pos %d overlaps with \"%s\" at pos %d%n",
                        text.substring(span.getStartPosition(), span.getStartPosition() + span.getLength()),
                        span.getStartPosition(),
                        text.substring(prev.getStartPosition(), prev.getStartPosition() + prev.getLength()),
                        prev.getStartPosition());
                if (span.getLength() > prev.getLength()) {
                    // current is larger --> replace previous with current
                    textBuilder.delete(textBuilder.length() - prev.getLength(), textBuilder.length());
                    lastPos -= prev.getLength();
                } else
                    // previous is larger or equal --> skip current
                    continue;
            }
            String before = text.substring(lastPos, begin);
            String label = text.substring(begin, end);
            lastPos = end;
            textBuilder.append(before).append("<entity>" + label + "</entity>");
        }

        String lastSnippet = text.substring(lastPos, text.length());
        textBuilder.append(lastSnippet);

        return textBuilder.toString();
    }

    public List<MeaningSpan> getAnnotations(String textWithMentions) throws GerbilException {
        String agdistisUrl = "http://" + host + ":" + port + "/AGDISTIS";
        String parameters = null;
        try {
            parameters = "type=agdistis&text=" + URLEncoder.encode(textWithMentions, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Couldn't encode request data.", e);
            throw new GerbilException("Couldn't encode request data.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
        }

        HttpEntity entity = new StringEntity(parameters, ContentType.APPLICATION_FORM_URLENCODED);
        HttpPost request = createPostRequest(agdistisUrl);
        request.addHeader("charset", "utf-8");
        request.addHeader("Accept-Charset", "UTF-8");
        request.setEntity(entity);

        entity = null;
        CloseableHttpResponse response = null;
        List<MeaningSpan> annotations = null;
        try {
            try {
                response = client.execute(request);
            } catch (java.net.SocketException e) {
                if (e.getMessage().contains(CONNECTION_ABORT_INDICATING_EXCPETION_MSG)) {
                    LOGGER.error("It seems like the annotator has needed too much time and has been interrupted.");
                    throw new GerbilException(
                            "It seems like the annotator has needed too much time and has been interrupted.", e,
                            ErrorTypes.ANNOTATOR_NEEDED_TOO_MUCH_TIME);
                } else {
                    LOGGER.error("Exception while sending request.", e);
                    throw new GerbilException("Exception while sending request.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
                }
            } catch (Exception e) {
                LOGGER.error("Exception while sending request.", e);
                throw new GerbilException("Exception while sending request.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
            }
            StatusLine status = response.getStatusLine();
            if ((status.getStatusCode() < 200) || (status.getStatusCode() >= 300)) {
                LOGGER.error("Response has the wrong status: " + status.toString());
                throw new GerbilException("Response has the wrong status: " + status.toString(),
                        ErrorTypes.UNEXPECTED_EXCEPTION);
            }

            entity = response.getEntity();
            try {
                annotations = parseJsonStream(entity.getContent());
            } catch (Exception e) {
                LOGGER.error("Couldn't parse the response.", e);
                throw new GerbilException("Couldn't parse the response.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
            }
        } finally {
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                } catch (IOException e1) {
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
            closeRequest(request);
        }
        return annotations;
    }

    private List<MeaningSpan> parseJsonStream(InputStream in) throws IOException, ParseException {
        List<MeaningSpan> annotations = new ArrayList<>();

        JSONArray namedEntities = (JSONArray) this.jsonParser.parse(new InputStreamReader(in, "UTF-8"));
        JSONObject namedEntity;
        String url;
        long start, length;
        for (Object obj : namedEntities) {
            namedEntity = (JSONObject) obj;

            start = (long) namedEntity.get("start");
            length = (long) namedEntity.get("offset");

            url = (String) namedEntity.get("disambiguatedURL");
            if (url == null) {
                annotations.add(new NamedEntity((int) start, (int) length, new HashSet<String>()));
            } else {
                annotations.add(new NamedEntity((int) start, (int) length, URLDecoder.decode(url, "UTF-8")));
            }
        }

        return annotations;
    }

}
