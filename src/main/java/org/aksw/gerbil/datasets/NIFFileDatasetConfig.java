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
import org.aksw.gerbil.bat.datasets.FileBasedNIFDataset;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.apache.jena.riot.Lang;

public class NIFFileDatasetConfig extends AbstractDatasetConfiguration {

    private String file;
    private WikipediaApiInterface wikiApi;

    public NIFFileDatasetConfig(WikipediaApiInterface wikiApi, String name, String file, boolean couldBeCached,
            ExperimentType... applicableForExperiment) {
        super(name, couldBeCached, applicableForExperiment);
        this.wikiApi = wikiApi;
        this.file = file;
    }

    @Override
    protected TopicDataset loadDataset() throws Exception {
        FileBasedNIFDataset dataset = new FileBasedNIFDataset(wikiApi, file, getName(), Lang.TTL);
        dataset.init();
        return dataset;
    }
}
