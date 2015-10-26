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
package org.aksw.gerbil.datasets;

import it.unipi.di.acube.batframework.datasetPlugins.IITBDataset;
import it.unipi.di.acube.batframework.problems.TopicDataset;
import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;

import java.io.IOException;

public class IITBDatasetConfig extends AbstractDatasetConfiguration {

    public static final String DATASET_NAME = "IITB";

    private static final String IITB_CRAWL_FOLDER_PROPERTY_NAME = "org.aksw.gerbil.datasets.IITBDatasetConfig.CrawledDocs";
    private static final String IITB_ANNOTATIONS_FILE_PROPERTY_NAME = "org.aksw.gerbil.datasets.IITBDatasetConfig.Annotations";

    private WikipediaApiInterface wikiAPI;

    public IITBDatasetConfig(WikipediaApiInterface wikiAPI) {
        super(DATASET_NAME, true, ExperimentType.Sa2KB);
        this.wikiAPI = wikiAPI;
    }

    @Override
    protected TopicDataset loadDataset() throws Exception {
        String crawlFolder = GerbilConfiguration.getInstance().getString(IITB_CRAWL_FOLDER_PROPERTY_NAME);
        if (crawlFolder == null) {
            throw new IOException("Couldn't load needed Property \"" + IITB_CRAWL_FOLDER_PROPERTY_NAME + "\".");
        }
        String annotationsFile = GerbilConfiguration.getInstance().getString(IITB_ANNOTATIONS_FILE_PROPERTY_NAME);
        if (annotationsFile == null) {
            throw new IOException("Couldn't load needed Property \"" + IITB_ANNOTATIONS_FILE_PROPERTY_NAME + "\".");
        }
        return new IITBDataset(crawlFolder, annotationsFile, wikiAPI);
    }

}
