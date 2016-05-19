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
package org.aksw.gerbil.dataset;

import java.io.IOException;
import java.util.List;

import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.utils.ClosePermitionGranter;

public class TestDataset extends AbstractDatasetConfiguration implements Dataset {

    private List<Document> instances;

    public TestDataset(List<Document> instances, ExperimentType applicableForExperiment) {
        this("TestDataset", instances, applicableForExperiment);
    }

    public TestDataset(String name, List<Document> instances, ExperimentType applicableForExperiment) {
        super(name, false, applicableForExperiment, null, null);
        this.instances = instances;
    }

    @Override
    public int size() {
        return instances.size();
    }

    @Override
    public List<Document> getInstances() {
        return instances;
    }

    @Override
    protected Dataset loadDataset() throws Exception {
        return this;
    }

    @Override
    public void close() throws IOException {
        // nothing to do
    }

    @Override
    public void setClosePermitionGranter(ClosePermitionGranter granter) {
        // nothing to do
    }

}
