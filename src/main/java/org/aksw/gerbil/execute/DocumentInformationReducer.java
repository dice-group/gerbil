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
package org.aksw.gerbil.execute;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.SpanImpl;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.aksw.gerbil.utils.filter.MarkingFilter;
import org.aksw.gerbil.utils.filter.TypeBasedMarkingFilter;

import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * This class reduces the information contained inside a given document. It is
 * needed to create tasks using the documents loaded from a dataset, e.i.,
 * removing the result from the documents before sending them to the annotator.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class DocumentInformationReducer {

    public static Document reduceToPlainText(Document document) {
        return new DocumentImpl(document.getText(), document.getDocumentURI());
    }

    public static Document reduceToTextAndSpans(Document document) {
        List<Span> spans = document.getMarkings(Span.class);
        List<Marking> markings = new ArrayList<Marking>(spans.size());
        for (Span s : spans) {
            markings.add(new SpanImpl(s));
        }
        return new DocumentImpl(document.getText(), document.getDocumentURI(), markings);
    }

    public static Document reduceToTextAndEntities(Document document) {
        MarkingFilter<TypedNamedEntity> filter = new TypeBasedMarkingFilter<TypedNamedEntity>(false,
                RDFS.Class.getURI(), OWL.Class.getURI());
        List<TypedNamedEntity> namedEntities = document.getMarkings(TypedNamedEntity.class);
        List<Marking> markings = new ArrayList<Marking>(namedEntities.size());
        for (TypedNamedEntity tne : namedEntities) {
            if (filter.isMarkingGood(tne)) {
                markings.add(new NamedEntity(tne.getStartPosition(), tne.getLength(), tne.getUris()));
            }
        }
        return new DocumentImpl(document.getText(), document.getDocumentURI(), markings);
    }
}
