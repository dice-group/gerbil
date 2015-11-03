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
import org.aksw.gerbil.transfer.nif.Meaning;
import org.junit.Ignore;

@Ignore
public class TestC2KBAnnotator extends AbstractTestAnnotator implements C2KBAnnotator {

    public TestC2KBAnnotator(List<Document> instances) {
        super("TestC2KBAnnotator", false, instances, ExperimentType.C2KB);
    }

    @Override
    public List<Meaning> performC2KB(Document document) throws GerbilException {
        Document result = this.getDocument(document.getDocumentURI());
        if (result == null) {
            return new ArrayList<Meaning>(0);
        }
        return result.getMarkings(Meaning.class);
    }

}
