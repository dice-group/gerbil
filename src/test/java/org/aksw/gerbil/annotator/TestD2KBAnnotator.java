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
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.junit.Ignore;

@Ignore
public class TestD2KBAnnotator extends AbstractTestAnnotator implements D2KBAnnotator {

    public TestD2KBAnnotator(List<Document> instances) {
        super("TestEntityLinker", false, instances, ExperimentType.D2KB);
    }

    @Override
    public List<MeaningSpan> performD2KBTask(Document document) throws GerbilException {
        Document result = this.getDocument(document.getDocumentURI());
        if (result == null) {
            return new ArrayList<MeaningSpan>(0);
        }
        return result.getMarkings(MeaningSpan.class);
    }

}
