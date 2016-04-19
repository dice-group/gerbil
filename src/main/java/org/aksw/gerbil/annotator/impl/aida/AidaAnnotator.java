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
package org.aksw.gerbil.annotator.impl.aida;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.annotator.A2KBAnnotator;
import org.aksw.gerbil.annotator.http.AbstractHttpBasedAnnotator;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.ScoredNamedEntity;
import org.aksw.gerbil.transfer.nif.data.StartPosBasedComparator;
import org.aksw.gerbil.transfer.nif.vocabulary.ITSRDF;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

/**
 * Client implementation of the web service of the AIDA annotator. The API is
 * described here: <a href=
 * "http://www.mpi-inf.mpg.de/departments/databases-and-information-systems/research/yago-naga/aida/webservice/">
 * http://www.mpi-inf.mpg.de/departments/databases-and-information-systems/
 * research/yago-naga/aida/webservice/</a>
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class AidaAnnotator extends AbstractHttpBasedAnnotator implements A2KBAnnotator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AidaAnnotator.class);

    private static final Charset CHARSET = Charset.forName("utf-8");
    private static final ContentType REQUEST_CONTENT_TYPE = ContentType.create("application/x-www-form-urlencoded",
            Consts.UTF_8);
    private static final String FRED_DENOTES_URI = "http://ontologydesignpatterns.org/cp/owl/semiotics.owl#denotes";

    private String serviceUrl;
    private Comparator<Span> spanComparator = new StartPosBasedComparator();

    public AidaAnnotator(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    protected Document requestAnnotations(String documentUri, String text, boolean containsMentions)
            throws GerbilException {
        LOGGER.info("Started request for {}", documentUri);
        HttpPost request = null;
        try {
            request = createPostRequest(serviceUrl);
        } catch (Exception e) {
            throw new GerbilException("Couldn't create HTTP request.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
        }
        StringBuilder requestContent = new StringBuilder();
        requestContent.append("text=");
        try {
            requestContent.append(URLEncoder.encode(text, Consts.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            throw new GerbilException("Couldn't encode text.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
        }
        requestContent.append('\n');
        if (containsMentions) {
            requestContent.append("tag_mode=manual");
        }

        request.setEntity(new StringEntity(requestContent.toString(), REQUEST_CONTENT_TYPE));
        // AIDA returns JSON, but it returns an error if we request it
        // request.addHeader(HttpHeaders.ACCEPT, "application/json");
        // We can ask for UTF-8 but it is not clear whether the service really
        // returns UTF-8
        request.addHeader(HttpHeaders.ACCEPT_ENCODING, Consts.UTF_8.name());

        HttpEntity entity = null;
        CloseableHttpResponse response = null;
        Document document;
        try {
            response = sendRequest(request);
            // receive NIF document
            entity = response.getEntity();
            // read and parse response
            try {
                String content = IOUtils.toString(entity.getContent(), Consts.UTF_8.name());
                // parse results
                JSONObject outObj = new JSONObject(content);
                document = new DocumentImpl(text, documentUri);
                parseMarkings(outObj, document);
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
        LOGGER.info("Finished request for {}", document.getDocumentURI());
        return document;
    }

    protected void parseMarkings(JSONObject outObj, Document resultDoc) throws GerbilException {
        try {
            if (outObj != null && outObj.has("mentions")) {
                JSONArray mentions = outObj.getJSONArray("mentions");
                if (mentions != null) {
                    JSONObject mention, bestEntity;
                    int offset, length;
                    Set<String> uris;
                    double confidence;
                    for (int i = 0; i < mentions.length(); ++i) {
                        mention = mentions.getJSONObject(i);
                        if (mention != null && mention.has("bestEntity") && mention.has("offset")
                                && mention.has("length")) {
                            offset = mention.getInt("offset");
                            length = mention.getInt("length");
                            bestEntity = mention.getJSONObject("bestEntity");
                            uris = null;
                            confidence = -1;
                            if (bestEntity != null && bestEntity.has("kbIdentifier")) {
                                uris = generateUriSet(bestEntity.getString("kbIdentifier"));
                                if (bestEntity.has("disambiguationScore")) {
                                    confidence = bestEntity.getDouble("disambiguationScore");
                                }
                                if (uris != null) {
                                    if (confidence > -1) {
                                        resultDoc.addMarking(new ScoredNamedEntity(offset, length, uris, confidence));
                                    } else {
                                        resultDoc.addMarking(new NamedEntity(offset, length, uris));
                                    }
                                }
                            } else {
                                LOGGER.warn("Got an incomplete mention from AIDA: {}. It will be ignored",
                                        bestEntity.toString());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new GerbilException("Got an Exception while parsing the response of AIDA.", e,
                    ErrorTypes.UNEXPECTED_EXCEPTION);
        }
    }

    protected Set<String> generateUriSet(String uri) {
        if (uri == null) {
            return null;
        }
        Set<String> uris = new HashSet<String>();
        String title;
        if (uri.startsWith("YAGO:")) {
            // We have to replace occurrences of \\uXXXX
            title = StringEscapeUtils.unescapeJava(uri.substring(5, uri.length())).replace(' ', '_');
            // Let's use a Wikipedia URI
            uris.add("http://en.wikipedia.org/wiki/" + title);
            // TODO we could add the YAGO URI as well
        } else {
            uris.add(uri);
        }
        return uris;
    }

    protected Reader replaceDenotesUri(InputStream content) throws IOException {
        String response = StreamUtils.copyToString(content, CHARSET);
        response = response.replaceAll(FRED_DENOTES_URI, ITSRDF.taIdentRef.getURI());
        return new StringReader(response);
    }

    @Override
    public List<Meaning> performC2KB(Document document) throws GerbilException {
        return requestAnnotations(document.getDocumentURI(), document.getText(), false).getMarkings(Meaning.class);
    }

    @Override
    public List<MeaningSpan> performA2KBTask(Document document) throws GerbilException {
        return requestAnnotations(document.getDocumentURI(), document.getText(), false).getMarkings(MeaningSpan.class);
    }

    @Override
    public List<MeaningSpan> performD2KBTask(Document document) throws GerbilException {
        return requestAnnotations(document.getDocumentURI(), createTextWithSpans(document), true)
                .getMarkings(MeaningSpan.class);
    }

    @Override
    public List<Span> performRecognition(Document document) throws GerbilException {
        return requestAnnotations(document.getDocumentURI(), document.getText(), false).getMarkings(Span.class);
    }

    protected String createTextWithSpans(Document document) {
        String text = document.getText();
        List<Span> spans = document.getMarkings(Span.class);
        Collections.sort(spans, spanComparator);
        int pos = 0;
        StringBuilder textBuilder = new StringBuilder();
        for (Span span : spans) {
            textBuilder.append(text.substring(pos, span.getStartPosition()));
            textBuilder.append("[[");
            pos = span.getStartPosition() + span.getLength();
            textBuilder.append(text.substring(span.getStartPosition(), pos));
            textBuilder.append("]]");
        }
        if (pos < text.length()) {
            textBuilder.append(text.substring(pos, text.length()));
        }
        return textBuilder.toString();
    }

}
