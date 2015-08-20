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
package org.aksw.gerbil.datatypes;

import org.aksw.gerbil.annotators.AnnotatorConfiguration;
import org.aksw.gerbil.datasets.DatasetConfiguration;
import org.aksw.gerbil.matching.Matching;

public class ExperimentTaskConfiguration {

    public AnnotatorConfiguration annotatorConfig;
    public DatasetConfiguration datasetConfig;
    public ExperimentType type;
    public Matching matching;

    public ExperimentTaskConfiguration(AnnotatorConfiguration annotatorConfig, DatasetConfiguration datasetConfig,
            ExperimentType type, Matching matching) {
        super();
        this.annotatorConfig = annotatorConfig;
        this.datasetConfig = datasetConfig;
        this.type = type;
        this.matching = matching;
    }

    public AnnotatorConfiguration getAnnotatorConfig() {
        return annotatorConfig;
    }

    public void setAnnotatorConfig(AnnotatorConfiguration annotatorConfig) {
        this.annotatorConfig = annotatorConfig;
    }

    public DatasetConfiguration getDatasetConfig() {
        return datasetConfig;
    }

    public void setDatasetConfig(DatasetConfiguration datasetConfig) {
        this.datasetConfig = datasetConfig;
    }

    public ExperimentType getType() {
        return type;
    }

    public void setType(ExperimentType type) {
        this.type = type;
    }

    public Matching getMatching() {
        return matching;
    }

    public void setMatching(Matching matching) {
        this.matching = matching;
    }

    @Override
    public String toString() {
        return "eTConfig(\"" + annotatorConfig.getName() + "\",\"" + datasetConfig.getName() + "\",\"" + type.name()
                + "\",\"" + matching.name() + "\")";
    }
}
