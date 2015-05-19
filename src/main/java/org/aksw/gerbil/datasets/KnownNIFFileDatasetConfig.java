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

import it.acubelab.batframework.problems.TopicDataset;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.io.IOException;

import org.aksw.gerbil.bat.datasets.FileBasedNIFDataset;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.apache.jena.riot.Lang;

public class KnownNIFFileDatasetConfig extends AbstractDatasetConfiguration {

    private static final String NIF_DATASET_FILE_PROPERTY_NAME = "org.aksw.gerbil.datasets.KnownNIFFileDatasetConfig.";

    public static enum NIFDatasets {
        KORE50("KORE50"),
        // N3_NEWS_100("N3-News-100"), Removed since this is a german dataset
        N3_REUTERS_128("N3-Reuters-128"),
        N3_RSS_500("N3-RSS-500"),
        DBPEDIA_SPOTLIGHT("DBpediaSpotlight");

        private String name;

        private NIFDatasets(String name) {
            this.name = name;
        }

        public String getDatasetName() {
            return name;
        }
    }

    private NIFDatasets dataset;
    private WikipediaApiInterface wikiApi;

    public KnownNIFFileDatasetConfig(WikipediaApiInterface wikiApi, NIFDatasets dataset) {
        super(dataset.getDatasetName(), true, ExperimentType.Sa2KB);
        this.dataset = dataset;
        this.wikiApi = wikiApi;
    }

    @Override
    protected TopicDataset loadDataset() throws Exception {
        String propertyKey = NIF_DATASET_FILE_PROPERTY_NAME + dataset.name();
        String nifFile = GerbilConfiguration.getInstance().getString(propertyKey);
        if (nifFile == null) {
            throw new IOException("Couldn't load needed Property \"" + propertyKey + "\".");
        }
        FileBasedNIFDataset dataset = new FileBasedNIFDataset(wikiApi, nifFile, getName(), Lang.TTL);
        dataset.init();
        return dataset;
    }
}
