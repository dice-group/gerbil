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

import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;

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
    
    public static Document reduceToTextAndTypedEntities(Document document) {
    	 MarkingFilter<TypedNamedEntity> filter = new TypeBasedMarkingFilter<TypedNamedEntity>(false,
                 RDFS.Class.getURI(), OWL.Class.getURI());
         List<TypedNamedEntity> namedEntities = document.getMarkings(TypedNamedEntity.class);
         List<Marking> markings = new ArrayList<Marking>(namedEntities.size());
         for (TypedNamedEntity tne : namedEntities) {
             if (filter.isMarkingGood(tne)) {
                 markings.add(new TypedNamedEntity(tne.getStartPosition(), tne.getLength(), tne.getUris(), tne.getTypes()));
             }
         }
         return new DocumentImpl(document.getText(), document.getDocumentURI(), markings);
    }
}
