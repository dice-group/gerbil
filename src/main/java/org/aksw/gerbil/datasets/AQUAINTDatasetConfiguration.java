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

import java.io.IOException;

import it.acubelab.batframework.datasetPlugins.AQUAINTDataset;
import it.acubelab.batframework.problems.TopicDataset;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;

public class AQUAINTDatasetConfiguration extends AbstractDatasetConfiguration {

    public static final String DATASET_NAME = "AQUAINT";

    private static final String TEXTS_PATH_PROPERTY_NAME = "org.aksw.gerbil.datasets.AQUAINTDatasetConfiguration.TextsPath";
    private static final String ANNOTATIONS_PATH_PROPERTY_NAME = "org.aksw.gerbil.datasets.AQUAINTDatasetConfiguration.AnnotationsPath";

    private WikipediaApiInterface wikiApi;

    public AQUAINTDatasetConfiguration(WikipediaApiInterface wikiApi) {
        super(DATASET_NAME, true, ExperimentType.Sa2KB);
        this.wikiApi = wikiApi;
    }

    @Override
    protected TopicDataset loadDataset() throws Exception {
        String textsPath = GerbilConfiguration.getInstance().getString(TEXTS_PATH_PROPERTY_NAME);
        if (textsPath == null) {
            throw new IOException("Couldn't load needed Property \"" + TEXTS_PATH_PROPERTY_NAME + "\".");
        }
        String annotationsPath = GerbilConfiguration.getInstance().getString(ANNOTATIONS_PATH_PROPERTY_NAME);
        if (annotationsPath == null) {
            throw new IOException("Couldn't load needed Property \"" + ANNOTATIONS_PATH_PROPERTY_NAME + "\".");
        }
        return new AQUAINTDataset(textsPath, annotationsPath, wikiApi);
    }

}
