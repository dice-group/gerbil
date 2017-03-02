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
package org.aksw.gerbil.annotator.impl.fred;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.annotator.OKETask1Annotator;
import org.aksw.gerbil.annotator.http.AbstractHttpBasedAnnotator;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.NIFDocumentParser;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TurtleNIFDocumentParser;
import org.aksw.gerbil.transfer.nif.TypedSpan;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.aksw.gerbil.transfer.nif.vocabulary.ITSRDF;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

/**
 * Client implementation of the web service of the FRED annotator. The API is
 * described here: http://wit.istc.cnr.it/stlab-tools/fred/api
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class FredAnnotator extends AbstractHttpBasedAnnotator implements OKETask1Annotator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FredAnnotator.class);

    private static final Charset CHARSET = Charset.forName("utf-8");
    private static final String TEXT_PARAMETER_NAME = "text";
    private static final String TEXT_ANNOTATION_PARAMETER = "textannotation=nif";
    private static final String FRED_DENOTES_URI = "http://ontologydesignpatterns.org/cp/owl/semiotics.owl#denotes";

    private String serviceUrl;
    private NIFDocumentParser nifParser = new TurtleNIFDocumentParser();

    public FredAnnotator(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    protected Document requestAnnotations(Document document) throws GerbilException {
        LOGGER.info("Started request for {}", document.getDocumentURI());
        HttpGet request = null;
        try {
            String url = createRequestUrl(document.getText());
            request = createGetRequest(url);
        } catch (Exception e) {
            throw new GerbilException("Couldn't create HTTP request.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
        }
        // Accept header; Note that nifParser.getHttpContentType() does not fit
        // the API
        request.addHeader(HttpHeaders.ACCEPT, "text/turtle");
        request.addHeader(HttpHeaders.ACCEPT_CHARSET, "UTF-8");

        HttpEntity entity = null;
        CloseableHttpResponse response = null;
        try {
            response = sendRequest(request);
            // receive NIF document
            entity = response.getEntity();
            // read response and parse NIF
            try {
                document = nifParser.getDocumentFromNIFReader(replaceDenotesUri(entity.getContent()));
            } catch (Exception e) {
                LOGGER.error("Couldn't parse the response.", e);
                throw new GerbilException("Couldn't parse the response.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
            }
            if (document == null) {
                throw new GerbilException("The response didn't contain a document.", ErrorTypes.UNEXPECTED_EXCEPTION);
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

    protected String createRequestUrl(String text) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        builder.append(serviceUrl);
        builder.append('?');
        builder.append(TEXT_PARAMETER_NAME);
        builder.append('=');
        builder.append(URLEncoder.encode(text, "utf-8"));
        builder.append('&');
        builder.append(TEXT_ANNOTATION_PARAMETER);
        return builder.toString();
    }

    protected Reader replaceDenotesUri(InputStream content) throws IOException {
        String response = StreamUtils.copyToString(content, CHARSET);
        response = response.replaceAll(FRED_DENOTES_URI, ITSRDF.taIdentRef.getURI());
        return new StringReader(response);
    }

    @Override
    public List<Meaning> performC2KB(Document document) throws GerbilException {
        return requestAnnotations(document).getMarkings(Meaning.class);
    }

    @Override
    public List<MeaningSpan> performA2KBTask(Document document) throws GerbilException {
        return requestAnnotations(document).getMarkings(MeaningSpan.class);
    }

    @Override
    public List<MeaningSpan> performD2KBTask(Document document) throws GerbilException {
        return requestAnnotations(document).getMarkings(MeaningSpan.class);
    }

    @Override
    public List<Span> performRecognition(Document document) throws GerbilException {
        return requestAnnotations(document).getMarkings(Span.class);
    }

    @Override
    public List<TypedSpan> performTyping(Document document) throws GerbilException {
        return requestAnnotations(document).getMarkings(TypedSpan.class);
    }

    @Override
    public List<TypedNamedEntity> performTask1(Document document) throws GerbilException {
        return requestAnnotations(document).getMarkings(TypedNamedEntity.class);
    }

    @Override
    public List<TypedSpan> performRT2KBTask(Document document) throws GerbilException {
        return requestAnnotations(document).getMarkings(TypedSpan.class);
    }

    @SuppressWarnings("unchecked")
    protected static <T extends Marking> List<T> transformToClass(List<Marking> markings, Class<T> clazz) {
        List<T> markingsWithClass = new ArrayList<T>();
        for (Marking marking : markings) {
            if (clazz.isInstance(marking)) {
                markingsWithClass.add((T) marking);
            }
        }
        return markingsWithClass;
    }

}
