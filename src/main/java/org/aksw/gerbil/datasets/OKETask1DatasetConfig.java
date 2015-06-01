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
package org.aksw.gerbil.datasets;

import java.io.IOException;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.impl.nif.FileBasedNIFDataset;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.apache.jena.riot.Lang;

public class OKETask1DatasetConfig extends AbstractDatasetConfiguration {

    private static final String NIF_DATASET_FILE_PROPERTY_NAME = "org.aksw.gerbil.datasets.OKETask1DatasetConfig.";

    public static enum NIFDatasets {
        OKE_2015_TASK1_EXAMPLE("OKE 2015 Task 1 example set"), OKE_2015_TASK1_GS_SAMPLE(
                "OKE 2015 Task 1 gold standard sample"), OKE_2015_TASK1_EVALUATION("OKE 2015 Task 1 evaluation dataset"), OKE_2015_TASK2_EXAMPLE(
                "OKE 2015 Task 2 example set"), OKE_2015_TASK2_GS_SAMPLE("OKE 2015 Task 2 gold standard sample"), OKE_2015_TASK2_EVALUATION(
                "OKE 2015 Task 2 evaluation dataset");

        private String name;

        private NIFDatasets(String name) {
            this.name = name;
        }

        public String getDatasetName() {
            return name;
        }
    }

    private NIFDatasets dataset;

    public OKETask1DatasetConfig(NIFDatasets dataset) {
        super(dataset.getDatasetName(), true, ExperimentType.OKE_Task1);
        this.dataset = dataset;
    }

    @Override
    protected Dataset loadDataset() throws Exception {
        String propertyKey = NIF_DATASET_FILE_PROPERTY_NAME + dataset.name();
        String nifFile = GerbilConfiguration.getInstance().getString(propertyKey);
        if (nifFile == null) {
            throw new IOException("Couldn't load needed Property \"" + propertyKey + "\".");
        }
        FileBasedNIFDataset dataset = new FileBasedNIFDataset(nifFile, getName(), Lang.TTL);
        dataset.init();
        return dataset;
    }
}
