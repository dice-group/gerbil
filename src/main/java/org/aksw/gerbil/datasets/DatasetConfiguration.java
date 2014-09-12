package org.aksw.gerbil.datasets;

import it.acubelab.batframework.problems.TopicDataset;

import org.aksw.gerbil.datatypes.ExperimentType;

public interface DatasetConfiguration {

    public String getDatasetName();

    public boolean couldBeCached();

    public TopicDataset getDataset(ExperimentType experimentType);

    // public A2WDataset getA2WDataset();
    //
    // public C2WDataset getC2WDataset();
    //
    // public D2WDataset getD2WDataset();
    //
    // public Rc2WDataset getRc2WDataset();
}
