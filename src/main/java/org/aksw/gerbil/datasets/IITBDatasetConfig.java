package org.aksw.gerbil.datasets;

import it.acubelab.batframework.datasetPlugins.IITBDataset;
import it.acubelab.batframework.problems.TopicDataset;

import org.aksw.gerbil.datatypes.ExperimentType;

public class IITBDatasetConfig extends AbstractDatasetConfiguration {

    public IITBDatasetConfig() {
        super("IITB", true, ExperimentType.D2W);
    }

    @Override
    protected TopicDataset loadDataset() throws Exception {
        // TODO
        // return new IITBDataset(textPath, annotationsPath, api);
        return new IITBDataset(null, null, null);
    }

}
