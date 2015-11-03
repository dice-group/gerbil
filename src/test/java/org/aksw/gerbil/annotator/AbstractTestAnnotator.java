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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.gerbil.datatypes.AbstractAdapterConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
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
