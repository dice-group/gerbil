package org.aksw.gerbil.web.response.execution;

import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;

import java.util.List;

public class ExperimentExecutionResponse {

    private String experimentId;
    private List<ExperimentTaskConfiguration> faultyConfigs;

    public ExperimentExecutionResponse(String experimentId) {
        this.experimentId = experimentId;
    }

    public ExperimentExecutionResponse(List<ExperimentTaskConfiguration> faultyConfigs) {
        this.faultyConfigs = faultyConfigs;
    }

    public ExperimentExecutionResponse(String experimentId, List<ExperimentTaskConfiguration> faultyConfigs) {
        this.experimentId = experimentId;
        this.faultyConfigs = faultyConfigs;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public List<ExperimentTaskConfiguration> getFaultyConfigs() {
        return faultyConfigs;
    }
}
