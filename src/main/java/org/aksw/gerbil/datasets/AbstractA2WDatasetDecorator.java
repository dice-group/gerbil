/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
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
package org.aksw.gerbil.datasets;

import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.data.Mention;
import it.acubelab.batframework.data.Tag;
import it.acubelab.batframework.problems.A2WDataset;

import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.exceptions.GerbilException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractA2WDatasetDecorator implements DatasetDecorator<A2WDataset>, A2WDataset {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractA2WDatasetDecorator.class);

    protected A2WDataset dataset;

    @Override
    public A2WDataset getDataset() throws GerbilException {
        return dataset;
    }

    protected void setDataset(A2WDataset dataset) {
        this.dataset = dataset;
    }

    @Override
    public String getName() {
        try {
            return getDataset().getName();
        } catch (GerbilException e) {
            LOGGER.error("Exception while trying to get annotator.", e);
            return "";
        }
    }

    @Override
    public int getSize() {
        try {
            return getDataset().getSize();
        } catch (GerbilException e) {
            LOGGER.error("Exception while trying to get dataset.", e);
            return 0;
        }
    }

    @Override
    public List<String> getTextInstanceList() {
        try {
            return getDataset().getTextInstanceList();
        } catch (GerbilException e) {
            LOGGER.error("Exception while trying to get dataset.", e);
            return null;
        }
    }

    @Override
    public int getTagsCount() {
        try {
            return getDataset().getTagsCount();
        } catch (GerbilException e) {
            LOGGER.error("Exception while trying to get dataset.", e);
            return 0;
        }
    }

    @Override
    public List<HashSet<Tag>> getC2WGoldStandardList() {
        try {
            return getDataset().getC2WGoldStandardList();
        } catch (GerbilException e) {
            LOGGER.error("Exception while trying to get dataset.", e);
            return null;
        }
    }

    @Override
    public List<HashSet<Mention>> getMentionsInstanceList() {
        try {
            return getDataset().getMentionsInstanceList();
        } catch (GerbilException e) {
            LOGGER.error("Exception while trying to get dataset.", e);
            return null;
        }
    }

    @Override
    public List<HashSet<Annotation>> getD2WGoldStandardList() {
        try {
            return getDataset().getD2WGoldStandardList();
        } catch (GerbilException e) {
            LOGGER.error("Exception while trying to get dataset.", e);
            return null;
        }
    }

    @Override
    public List<HashSet<Annotation>> getA2WGoldStandardList() {
        try {
            return getDataset().getA2WGoldStandardList();
        } catch (GerbilException e) {
            LOGGER.error("Exception while trying to get dataset.", e);
            return null;
        }
    }

}
