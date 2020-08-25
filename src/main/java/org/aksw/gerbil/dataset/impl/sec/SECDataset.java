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
package org.aksw.gerbil.dataset.impl.sec;

import org.aksw.gerbil.dataset.impl.conll.CoNLLTypeRetriever;
import org.aksw.gerbil.dataset.impl.conll.GenericCoNLLDataset;

public class SECDataset extends GenericCoNLLDataset {

    private static final int ANNOTATION_COLUMN = 3;
    private static final int URI_COLUMN = -1;
    private static final String MARKING_START = "I-";
    private static final String DOCUMENT_START_LINE = "\\-DOCSTART\\-.*";
    private static final CoNLLTypeRetriever TYPE_TAGS = new CoNLLTypeRetriever("LOC", null, null, null, "MISC", "PER",
            null, null, null, "ORG");

    public SECDataset(String file) {
        super(file, ANNOTATION_COLUMN, URI_COLUMN, TYPE_TAGS);
        this.setMarkingStart(MARKING_START);
        this.setDocumentStartLine(DOCUMENT_START_LINE);
    }
}