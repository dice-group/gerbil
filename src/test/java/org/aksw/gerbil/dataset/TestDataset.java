package org.aksw.gerbil.dataset;

import java.util.List;

import org.aksw.gerbil.datasets.AbstractDatasetConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.transfer.nif.Document;

public class TestDataset extends AbstractDatasetConfiguration implements Dataset {

    private List<Document> instances;

    public TestDataset(List<Document> instances, ExperimentType... applicableForExperiment) {
        super("TestDataset", false, applicableForExperiment);
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

}
