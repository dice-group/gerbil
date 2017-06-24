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
package org.aksw.gerbil.dataset.impl.nif;

import org.aksw.gerbil.dataset.AbstractDatasetConfiguration;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.check.EntityCheckerManager;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.apache.jena.riot.Lang;

public class NIFFileDatasetConfig extends AbstractDatasetConfiguration {

    private String file;

    public NIFFileDatasetConfig(String name, String file, boolean couldBeCached, ExperimentType applicableForExperiment,
            EntityCheckerManager entityCheckerManager, SameAsRetriever globalRetriever) {
        super(name, couldBeCached, applicableForExperiment, entityCheckerManager, globalRetriever);
        this.file = file;
    }

    @Override
    protected Dataset loadDataset() throws Exception {
        FileBasedNIFDataset dataset = new FileBasedNIFDataset(file, getName(), Lang.TTL);
        dataset.init();
        return dataset;
    }
}
