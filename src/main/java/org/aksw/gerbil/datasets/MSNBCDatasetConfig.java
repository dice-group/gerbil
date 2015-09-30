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

import it.acubelab.batframework.datasetPlugins.MSNBCDataset;
import it.acubelab.batframework.problems.TopicDataset;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.io.IOException;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;

public class MSNBCDatasetConfig extends AbstractDatasetConfiguration {

    public static final String DATASET_NAME = "MSNBC";

    private static final String MSNBC_TEXTS_FOLDER_PROPERTY_NAME = "org.aksw.gerbil.datasets.MSNBCDatasetConfig.TextsFolder";
    private static final String MSNBC_ANNOTATIONS_FOLDER_PROPERTY_NAME = "org.aksw.gerbil.datasets.MSNBCDatasetConfig.AnnotationsFolder";

    private WikipediaApiInterface wikiAPI;

    public MSNBCDatasetConfig(WikipediaApiInterface wikiAPI) {
        super(DATASET_NAME, true, ExperimentType.Sa2KB);
        this.wikiAPI = wikiAPI;
    }

    @Override
    protected TopicDataset loadDataset() throws Exception {
        String textsFolder = GerbilConfiguration.getInstance().getString(MSNBC_TEXTS_FOLDER_PROPERTY_NAME);
        if (textsFolder == null) {
            throw new IOException("Couldn't load needed Property \"" + MSNBC_TEXTS_FOLDER_PROPERTY_NAME + "\".");
        }
        String annotationsFolder = GerbilConfiguration.getInstance()
                .getString(MSNBC_ANNOTATIONS_FOLDER_PROPERTY_NAME);
        if (annotationsFolder == null) {
            throw new IOException("Couldn't load needed Property \"" + MSNBC_ANNOTATIONS_FOLDER_PROPERTY_NAME + "\".");
        }
        return new MSNBCDataset(textsFolder, annotationsFolder, wikiAPI);
    }
}
