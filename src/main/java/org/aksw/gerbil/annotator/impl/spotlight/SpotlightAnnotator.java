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
package org.aksw.gerbil.annotator.impl.spotlight;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.annotator.A2KBAnnotator;
import org.aksw.gerbil.annotator.D2KBAnnotator;
import org.aksw.gerbil.annotator.EntityRecognizer;
import org.aksw.gerbil.annotator.EntityTyper;
import org.aksw.gerbil.annotator.OKETask1Annotator;
import org.aksw.gerbil.annotator.http.AbstractHttpBasedAnnotator;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TypedSpan;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.apache.commons.collections.ListUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;

public class SpotlightAnnotator extends AbstractHttpBasedAnnotator implements OKETask1Annotator, EntityRecognizer,
        D2KBAnnotator, A2KBAnnotator, EntityTyper {

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
    public List<Meaning> performC2KB(Document document) throws GerbilException {
        return new ArrayList<Meaning>(client.annotate(document));
    }

    @Override
    public List<TypedSpan> performTyping(Document document) throws GerbilException {
        return new ArrayList<TypedSpan>(client.disambiguate(document));
    }

    @Override
    public List<MeaningSpan> performA2KBTask(Document document) throws GerbilException {
        return new ArrayList<MeaningSpan>(client.annotate(document));
    }

    @Override
    public List<MeaningSpan> performD2KBTask(Document document) throws GerbilException {
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

    @SuppressWarnings("unchecked")
    @Override
    public List<TypedSpan> performRT2KBTask(Document document) throws GerbilException {
        List<TypedNamedEntity> list = client.annotate(document);
        if (list != null) {
            return (List<TypedSpan>) ListUtils.typedList(list, TypedSpan.class);
        } else {
            return null;
        }
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
