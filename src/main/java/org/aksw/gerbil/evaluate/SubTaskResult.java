package org.aksw.gerbil.evaluate;

import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;

public class SubTaskResult extends EvaluationResultContainer {

    private ExperimentTaskConfiguration configuration;

    public SubTaskResult(ExperimentTaskConfiguration configuration) {
        super();
        this.configuration = configuration;
    }

    public ExperimentTaskConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ExperimentTaskConfiguration configuration) {
        this.configuration = configuration;
    }

}
