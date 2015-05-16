package org.aksw.gerbil.ws4test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.SpanImpl;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.ObjectObjectOpenHashMap;

/**
 * Client of DBpedia Spotlight. This implementation is mainly based on the
 * implementations of the BAT-Framework and the HAWK project.
 * 
 * @author Michael R&ouml;der <roeder@informatik.uni-leipzig.de>
 * 
 */
public class SpotlightClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotlightClient.class);

    private static final String TYPE_PREFIX_URI_MAPPING[][] = new String[][] {
            { "freebase", "http://rdf.freebase.com/ns/" }, { "dbpedia", "http://dbpedia.org/ontology/" } };

    private static final String DEFAULT_REQUEST_URL = "http://spotlight.dbpedia.org:80/rest";
    // private static final double DEFAULT_MIN_CONFIDENCE = -1;
    // private static final int DEFAULT_MIN_SUPPORT = -1;

    private static final String ANNOTATE_RESOURCE = "annotate";
    private static final String SPOT_RESOURCE = "spot";
    private static final String DISAMBIGUATE_RESOURCE = "disambiguate";

    private String serviceURL;
    // private double minConfidence = 0.2;
    // private int minSupport = 20;
    private ObjectObjectOpenHashMap<String, String> typePrefixToUriMapping;

    public SpotlightClient() {
        this(DEFAULT_REQUEST_URL/* , DEFAULT_MIN_CONFIDENCE, DEFAULT_MIN_SUPPORT */);
    }

    // public SpotlightClient(String serviceURL, double minConfidence, int
    // minSupport) {
    // super();
    // this.serviceURL = serviceURL.endsWith("/") ? serviceURL : (serviceURL +
    // "/");
    // this.minConfidence = minConfidence;
    // this.minSupport = minSupport;
    //
    // typePrefixToUriMapping = new ObjectObjectOpenHashMap<String, String>();
    // for (int i = 0; i < TYPE_PREFIX_URI_MAPPING.length; ++i) {
    // typePrefixToUriMapping.put(TYPE_PREFIX_URI_MAPPING[i][0],
    // TYPE_PREFIX_URI_MAPPING[i][1]);
    // }
    // }

    public SpotlightClient(String serviceURL) {
        this.serviceURL = serviceURL.endsWith("/") ? serviceURL : (serviceURL + "/");

        typePrefixToUriMapping = new ObjectObjectOpenHashMap<String, String>();
        for (int i = 0; i < TYPE_PREFIX_URI_MAPPING.length; ++i) {
            typePrefixToUriMapping.put(TYPE_PREFIX_URI_MAPPING[i][0], TYPE_PREFIX_URI_MAPPING[i][1]);
        }
    }

    protected String request(String inputText, String requestUrl) throws IOException {
        String parameters = "text=" + URLEncoder.encode(inputText, "UTF-8");
        // parameters += "&confidence=" + minConfidence;
        // parameters += "&support=" + minSupport;

        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        connection.setRequestProperty("Content-Length", String.valueOf(parameters.length()));

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(parameters);
        wr.flush();

        StringBuilder sb = new StringBuilder();
        InputStream is = null;
        Reader ir = null;
        BufferedReader br = null;
        try {
            is = connection.getInputStream();
            ir = new InputStreamReader(is);
            br = new BufferedReader(ir);

            while (br.ready()) {
                sb.append(br.readLine());
            }
        } finally {
            IOUtils.closeQuietly(br);
            IOUtils.closeQuietly(ir);
            IOUtils.closeQuietly(is);
            connection.disconnect();
        }
        return sb.toString();
    }

    public List<TypedNamedEntity> annotateSavely(Document document) {
        try {
            return annotate(document);
        } catch (IOException e) {
            LOGGER.error("Error while requesting DBpedia Spotlight to annotate text. Returning null.", e);
            return null;
        }
    }

    public List<TypedNamedEntity> annotate(Document document) throws IOException {
        String response = request(document.getText(), serviceURL + ANNOTATE_RESOURCE);
        return parseAnnotationResponse(response);
    }

    protected List<TypedNamedEntity> parseAnnotationResponse(String response) {
        List<TypedNamedEntity> markings = new ArrayList<TypedNamedEntity>();

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) parser.parse(response);
        } catch (ParseException e) {
            LOGGER.error("Error while parsing DBpedia Spotlight response. Returning null.", e);
            return null;
        }

        JSONArray resources = (JSONArray) jsonObject.get("Resources");
        JSONObject resource;
        int start;
        int length;
        String uri = null;
        Set<String> types;
        String typeStrings[], uriParts[];
        if (resources != null) {
            for (Object res : resources.toArray()) {
                resource = (JSONObject) res;
                start = Integer.parseInt((String) resource.get("@offset"));
                length = ((String) resource.get("@surfaceForm")).length();
                try {
                    uri = URLDecoder.decode((String) resource.get("@URI"), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    LOGGER.error("Error while parsing DBpedia Spotlight response. Returning null.", e);
                    return null;
                }
                // create Types set
                typeStrings = ((String) resource.get("@types")).split(",");
                types = new HashSet<String>(typeStrings.length);
                for (int i = 0; i < typeStrings.length; ++i) {
                    uriParts = typeStrings[i].split(":");
                    uriParts[0] = uriParts[0].toLowerCase();
                    if (typePrefixToUriMapping.containsKey(uriParts[0])) {
                        types.add(typePrefixToUriMapping.get(uriParts[0]) + uriParts[1]);
                    } else {
                        types.add(typeStrings[i]);
                    }
                }
                markings.add(new TypedNamedEntity(start, length, uri, types));
            }
        }

        return markings;
    }

    public List<Span> spotSavely(Document document) {
        try {
            return spot(document);
        } catch (IOException e) {
            LOGGER.error("Error while requesting DBpedia Spotlight to spot text. Returning null.", e);
            return null;
        }
    }

    public List<Span> spot(Document document) throws IOException {
        String response = request(document.getText(), serviceURL + SPOT_RESOURCE);
        return parseSpottingResponse(response);
    }

    protected List<Span> parseSpottingResponse(String response) {
        List<Span> markings = new ArrayList<Span>();

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) parser.parse(response);
        } catch (ParseException e) {
            LOGGER.error("Error while parsing DBpedia Spotlight response. Returning null.", e);
            return null;
        }

        jsonObject = (JSONObject) jsonObject.get("annotation");
        JSONArray resources = (JSONArray) jsonObject.get("surfaceForm");
        JSONObject resource;
        int start;
        int length;
        if (resources != null) {
            for (Object res : resources.toArray()) {
                resource = (JSONObject) res;
                start = Integer.parseInt((String) resource.get("@offset"));
                length = ((String) resource.get("@name")).length();
                markings.add(new SpanImpl(start, length));
            }
        }

        return markings;
    }

    public List<TypedNamedEntity> disambiguate(Document document) throws IOException {
        String text = document.getText();
        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><annotation text=\"");
        requestBuilder.append(text.replace('"', '\''));
        requestBuilder.append("\">");

        List<Span> spans = document.getMarkings(Span.class);
        int start;
        for (Span span : spans) {
            start = span.getStartPosition();
            requestBuilder.append("<surfaceForm name=\"");
            requestBuilder.append(text.substring(start, start + span.getLength()));
            requestBuilder.append("\" offset=\"");
            requestBuilder.append(start);
            requestBuilder.append("\" />");
        }
        requestBuilder.append("</annotation>");

        String response = request(requestBuilder.toString(), serviceURL + DISAMBIGUATE_RESOURCE);
        LOGGER.error(response);
        return parseAnnotationResponse(response);
    }

    public List<TypedNamedEntity> disambiguateSavely(Document document) {
        try {
            return disambiguate(document);
        } catch (IOException e) {
            LOGGER.error("Error while requesting DBpedia Spotlight to spot text. Returning null.", e);
            return null;
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void main(String args[]) {
        SpotlightClient client = new SpotlightClient();
        List list;

        list = client
                .annotateSavely(new DocumentImpl(
                        "President Obama called Wednesday on Congress to extend a tax break for students included in last year's economic stimulus package, arguing that the policy provides more generous assistance."));
        for (int i = 0; i < list.size(); ++i) {
            System.out.println(list.get(i));
        }
        list = client
                .spotSavely(new DocumentImpl(
                        "President Obama called Wednesday on Congress to extend a tax break for students included in last year's economic stimulus package, arguing that the policy provides more generous assistance."));
        for (int i = 0; i < list.size(); ++i) {
            System.out.println(list.get(i));
        }
        list = client
                .disambiguateSavely(new DocumentImpl(
                        "President Obama called Wednesday on Congress to extend a tax break for students included in last year's economic stimulus package, arguing that the policy provides more generous assistance.",
                        list));
        for (int i = 0; i < list.size(); ++i) {
            System.out.println(list.get(i));
        }
    }
}
