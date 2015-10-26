/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
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
package org.aksw.gerbil.annotator.impl.spotlight;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.annotator.EntityExtractor;
import org.aksw.gerbil.annotator.EntityLinker;
import org.aksw.gerbil.annotator.EntityRecognizer;
import org.aksw.gerbil.annotator.EntityTyper;
import org.aksw.gerbil.annotator.OKETask1Annotator;
import org.aksw.gerbil.annotator.http.AbstractHttpBasedAnnotator;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TypedSpan;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;

public class SpotlightAnnotator extends AbstractHttpBasedAnnotator
        implements OKETask1Annotator, EntityRecognizer, EntityLinker, EntityExtractor, EntityTyper {

    private static final String SERVICE_URL_PARAM_KEY = "org.aksw.gerbil.annotator.impl.spotlight.SpotlightAnnotator.ServieURL";

    @Deprecated
    public static final String ANNOTATOR_NAME = "DBpedia Spotlight";

    private SpotlightClient client;

    public SpotlightAnnotator() {
        String url = GerbilConfiguration.getInstance().getString(SERVICE_URL_PARAM_KEY);
        if (url != null) {
            client = new SpotlightClient(url, this);
        } else {
            client = new SpotlightClient(this);
        }
    }

    public SpotlightAnnotator(String url) {
        client = new SpotlightClient(url, this);
    }

    @Override
    public List<TypedSpan> performTyping(Document document) throws GerbilException {
        return new ArrayList<TypedSpan>(client.disambiguate(document));
    }

    @Override
    public List<MeaningSpan> performExtraction(Document document) throws GerbilException {
        return new ArrayList<MeaningSpan>(client.annotate(document));
    }

    @Override
    public List<MeaningSpan> performLinking(Document document) throws GerbilException {
        return new ArrayList<MeaningSpan>(client.disambiguate(document));
    }

    @Override
    public List<Span> performRecognition(Document document) throws GerbilException {
        return new ArrayList<Span>(client.spot(document));
    }

    @Override
    public List<TypedNamedEntity> performTask1(Document document) throws GerbilException {
        return client.annotate(document);
    }

    protected HttpPost createPostRequest(String url) {
        return super.createPostRequest(url);
    }

    @Override
    protected void closeRequest(HttpUriRequest request) {
        super.closeRequest(request);
    }
    
    @Override
    public CloseableHttpClient getClient() {
        return super.getClient();
    }
}
