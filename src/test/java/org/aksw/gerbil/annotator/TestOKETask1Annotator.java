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
package org.aksw.gerbil.annotator;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TypedSpan;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.junit.Ignore;

@Ignore
public class TestOKETask1Annotator extends AbstractTestAnnotator implements OKETask1Annotator {

    public TestOKETask1Annotator(List<Document> instances) {
        super("TestOKETask1Annotator", false, instances, ExperimentType.OKE_Task1);
    }

    public <T extends Marking> List<T> performAnnotation(Document document, Class<T> markingClass) {
        Document result = this.getDocument(document.getDocumentURI());
        if (result == null) {
            return new ArrayList<T>(0);
        }
        return result.getMarkings(markingClass);
    }

    @Override
    public List<Span> performRecognition(Document document) {
        return performAnnotation(document, Span.class);
    }

    @Override
    public List<MeaningSpan> performExtraction(Document document) throws GerbilException {
        return performAnnotation(document, MeaningSpan.class);
    }

    @Override
    public List<MeaningSpan> performLinking(Document document) throws GerbilException {
        return performAnnotation(document, MeaningSpan.class);
    }

    @Override
    public List<TypedSpan> performTyping(Document document) throws GerbilException {
        return performAnnotation(document, TypedSpan.class);
    }

    @Override
    public List<TypedNamedEntity> performTask1(Document document) throws GerbilException {
        return performAnnotation(document, TypedNamedEntity.class);
    }

}
