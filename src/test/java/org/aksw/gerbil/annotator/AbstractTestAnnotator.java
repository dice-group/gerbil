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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.gerbil.datatypes.AbstractAdapterConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.utils.ClosePermitionGranter;
import org.junit.Ignore;

@Ignore
public class AbstractTestAnnotator extends AbstractAdapterConfiguration implements Annotator, AnnotatorConfiguration {

    protected Map<String, Document> uriInstanceMapping;

    public AbstractTestAnnotator(String annotatorName, boolean couldBeCached, List<Document> instances,
            ExperimentType applicableForExperiment) {
        super(annotatorName, couldBeCached, applicableForExperiment);
        this.uriInstanceMapping = new HashMap<String, Document>(instances.size());
        for (Document document : instances) {
            uriInstanceMapping.put(document.getDocumentURI(), document);
        }
    }

    @Override
    public Annotator getAnnotator(ExperimentType experimentType) throws GerbilException {
        if (applicableForExperiment.equalsOrContainsType(experimentType)) {
            try {
                return loadAnnotator(experimentType);
            } catch (Exception e) {
                throw new GerbilException(e, ErrorTypes.ANNOTATOR_LOADING_ERROR);
            }
        }
        return null;
    }

    protected Annotator loadAnnotator(ExperimentType type) throws Exception {
        return this;
    }

    protected Document getDocument(String uri) {
        if (uriInstanceMapping.containsKey(uri)) {
            return uriInstanceMapping.get(uri);
        } else {
            return null;
        }
    }

    @Override
    public void close() throws IOException {
        // nothing to do
    }

    @Override
    public void setClosePermitionGranter(ClosePermitionGranter granter) {
        // nothing to do
    }

}
