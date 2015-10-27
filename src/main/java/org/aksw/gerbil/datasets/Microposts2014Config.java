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


import it.unipi.di.acube.batframework.problems.TopicDataset;
import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;
import org.aksw.gerbil.bat.datasets.Microposts2014Dataset;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;

import java.io.IOException;

/**
 * Configuration class that is able to load the Micropost2014 datasets (train and test).
 * The datasets are distinguished using the {@link Microposts2014Chunk} enum.
 * 
 * @author Giuseppe Rizzo <giuse.rizzo@gmail.com>
 */
public class Microposts2014Config extends AbstractDatasetConfiguration {

    public static final String DATASET_NAME_START = "Microposts2014";
    private static final String DATASET_FILE_PROPERTY_NAME = "org.aksw.gerbil.datasets.Microposts2014DatasetConfig";

    private Microposts2014Chunk chunk;
    private WikipediaApiInterface wikiApi;

    public static enum Microposts2014Chunk {
        TRAIN, TEST
    }

    public Microposts2014Config(
            Microposts2014Chunk chunk,
            WikipediaApiInterface wikiApi)
    {
        super(DATASET_NAME_START, true, ExperimentType.Sa2KB);
        this.chunk = chunk;
        this.wikiApi = wikiApi;
        // Set the correct name
        switch (chunk) {
        case TRAIN: {
            setName(getName() + "-Train");
            break;
        }
        case TEST: {
            setName(getName() + "-Test");
            break;
        }
        }
    }

    @Override
    protected TopicDataset loadDataset() throws Exception {
        switch (chunk) {
        case TRAIN: {
            String file = GerbilConfiguration.getInstance().getString(DATASET_FILE_PROPERTY_NAME.concat(".Train"));
            if (file == null) {
                throw new IOException("Couldn't load needed Property \"" + DATASET_FILE_PROPERTY_NAME + "\".");
            }
            return new Microposts2014Dataset(file, wikiApi);
        }
        case TEST: {
            String file = GerbilConfiguration.getInstance().getString(DATASET_FILE_PROPERTY_NAME.concat(".Test"));
            if (file == null) {
                throw new IOException("Couldn't load needed Property \"" + DATASET_FILE_PROPERTY_NAME + "\".");
            }
            return new Microposts2014Dataset(file, wikiApi);
        }
        }
        return null;
    }

}
