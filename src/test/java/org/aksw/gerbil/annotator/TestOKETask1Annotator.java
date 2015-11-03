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
package org.aksw.gerbil.annotator;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
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
    public List<MeaningSpan> performA2KBTask(Document document) throws GerbilException {
        return performAnnotation(document, MeaningSpan.class);
    }

    @Override
    public List<MeaningSpan> performD2KBTask(Document document) throws GerbilException {
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

    @Override
    public List<Meaning> performC2KB(Document document) throws GerbilException {
        return performAnnotation(document, Meaning.class);
    }

}
