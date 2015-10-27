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


import it.unipi.di.acube.batframework.datasetPlugins.ACE2004Dataset;
import it.unipi.di.acube.batframework.problems.TopicDataset;
import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;

import java.io.IOException;

public class ACE2004DatasetConfig extends AbstractDatasetConfiguration {

    public static final String DATASET_NAME = "ACE2004";

    private static final String ACE2004_TEXTS_FOLDER_PROPERTY_NAME = "org.aksw.gerbil.datasets.ACE2004DatasetConfig.TextsFolder";
    private static final String ACE2004_ANNOTATIONS_FOLDER_PROPERTY_NAME = "org.aksw.gerbil.datasets.ACE2004DatasetConfig.AnnotationsFolder";

    private WikipediaApiInterface wikiAPI;

    public ACE2004DatasetConfig(WikipediaApiInterface wikiAPI) {
        super(DATASET_NAME, true, ExperimentType.Sa2KB);
        this.wikiAPI = wikiAPI;
    }

    @Override
    protected TopicDataset loadDataset() throws Exception {
        String textsFolder = GerbilConfiguration.getInstance().getString(ACE2004_TEXTS_FOLDER_PROPERTY_NAME);
        if (textsFolder == null) {
            throw new IOException("Couldn't load needed Property \"" + ACE2004_TEXTS_FOLDER_PROPERTY_NAME + "\".");
        }
        String annotationsFolder = GerbilConfiguration.getInstance()
                .getString(ACE2004_ANNOTATIONS_FOLDER_PROPERTY_NAME);
        if (annotationsFolder == null) {
            throw new IOException("Couldn't load needed Property \"" + ACE2004_ANNOTATIONS_FOLDER_PROPERTY_NAME + "\".");
        }
        return new ACE2004Dataset(textsFolder, annotationsFolder, wikiAPI);
    }
}
