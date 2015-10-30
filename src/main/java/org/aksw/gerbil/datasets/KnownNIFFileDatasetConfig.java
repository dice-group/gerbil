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

@Deprecated
public class KnownNIFFileDatasetConfig extends AbstractDatasetConfiguration {

    private static final String NIF_DATASET_FILE_PROPERTY_NAME_PREFIX = "org.aksw.gerbil.datasets.";
    private static final String NIF_DATASET_FILE_PROPERTY_NAME_SUFFIX = ".file";

    public static enum NIFDatasets {
        KORE50("KORE50"),
        // N3_NEWS_100("N3-News-100"), Removed since this is a german dataset
        N3_REUTERS_128("N3-Reuters-128"), N3_RSS_500("N3-RSS-500"), DBPEDIA_SPOTLIGHT("DBpediaSpotlight");

        private String name;

        private NIFDatasets(String name) {
            this.name = name;
        }

        public String getDatasetName() {
            return name;
        }
    }

    private NIFDatasets dataset;

    public KnownNIFFileDatasetConfig(NIFDatasets dataset) {
        super(dataset.getDatasetName(), true, ExperimentType.A2KB);
        this.dataset = dataset;
    }

    @Override
    protected Dataset loadDataset() throws Exception {
        String propertyKey = NIF_DATASET_FILE_PROPERTY_NAME_PREFIX + dataset.name()
                + NIF_DATASET_FILE_PROPERTY_NAME_SUFFIX;
        String nifFile = GerbilConfiguration.getInstance().getString(propertyKey);
        if (nifFile == null) {
            throw new IOException("Couldn't load needed Property \"" + propertyKey + "\".");
        }
        FileBasedNIFDataset dataset = new FileBasedNIFDataset(nifFile, getName(), Lang.TTL);
        dataset.init();
        return dataset;
    }
}
