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
package org.aksw.gerbil.annotator.impl.fox;

import java.util.List;

import org.aksw.gerbil.annotator.OKETask1Annotator;
import org.aksw.gerbil.annotator.http.AbstractHttpBasedAnnotator;
import org.aksw.gerbil.annotator.impl.nif.NIFBasedAnnotatorWebservice;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TypedSpan;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.aksw.gerbil.utils.DocumentTextEditRevoker;

public class FOXAnnotator extends AbstractHttpBasedAnnotator implements OKETask1Annotator {

    private static final String FOX_SERVICE_URL_PARAMETER_KEY = "org.aksw.gerbil.annotators.FOXAnnotatorConfig.serviceUrl";

    public static final String NAME = "FOX";

    private NIFBasedAnnotatorWebservice annotator;

    public FOXAnnotator() throws GerbilException {
        String serviceUrl = GerbilConfiguration.getInstance().getString(FOX_SERVICE_URL_PARAMETER_KEY);
        if (serviceUrl == null) {
            throw new GerbilException("Couldn't load the needed property \"" + FOX_SERVICE_URL_PARAMETER_KEY + "\".",
                    ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
        annotator = new NIFBasedAnnotatorWebservice(serviceUrl, NAME);
    }

    public FOXAnnotator(String serviceUrl) {
        annotator = new NIFBasedAnnotatorWebservice(serviceUrl, NAME);
    }

    @Override
    public List<Meaning> performC2KB(Document document) throws GerbilException {
        return annotator.performC2KB(document);
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

    protected Document requestAnnotations(Document document) throws GerbilException {
        Document resultDoc = annotator.request(document);
        return DocumentTextEditRevoker.revokeTextEdits(resultDoc, document.getText());
    }

}
