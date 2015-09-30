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

import it.acubelab.batframework.datasetPlugins.MeijDataset;
import it.acubelab.batframework.problems.TopicDataset;

import java.io.IOException;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;

public class MeijDatasetConfig extends AbstractDatasetConfiguration {

    public static final String DATASET_NAME = "Meij";

    private static final String MEIJ_TWEETS_FILE_PROPERTY_NAME = "org.aksw.gerbil.datasets.MeijDatasetConfig.tweetsFile";
    private static final String MEIJ_TAGS_FILE_PROPERTY_NAME = "org.aksw.gerbil.datasets.MeijDatasetConfig.tagsFile";
    private static final String MEIJ_RANK_FILE_PROPERTY_NAME = "org.aksw.gerbil.datasets.MeijDatasetConfig.rankFile";

    public MeijDatasetConfig() {
        super(DATASET_NAME, true, ExperimentType.Rc2KB);
    }

    @Override
    protected TopicDataset loadDataset() throws Exception {
        String tweetsFile = GerbilConfiguration.getInstance().getString(MEIJ_TWEETS_FILE_PROPERTY_NAME);
        if (tweetsFile == null) {
            throw new IOException("Couldn't load needed Property \"" + MEIJ_TWEETS_FILE_PROPERTY_NAME + "\".");
        }
        String tagsFile = GerbilConfiguration.getInstance().getString(MEIJ_TAGS_FILE_PROPERTY_NAME);
        if (tagsFile == null) {
            throw new IOException("Couldn't load needed Property \"" + MEIJ_TAGS_FILE_PROPERTY_NAME + "\".");
        }
        String rankFile = GerbilConfiguration.getInstance().getString(MEIJ_RANK_FILE_PROPERTY_NAME);
        if (rankFile == null) {
            throw new IOException("Couldn't load needed Property \"" + MEIJ_RANK_FILE_PROPERTY_NAME + "\".");
        }
        return new MeijDataset(tweetsFile, tagsFile, rankFile);
    }

}
