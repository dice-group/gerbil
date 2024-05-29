package org.aksw.gerbil.exceptions;

import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;

import java.util.List;

public class FaultyConfigurationException extends RuntimeException{
    private final List<ExperimentTaskConfiguration> faultyConfigs;

    public FaultyConfigurationException(List<ExperimentTaskConfiguration> faultyConfigs) {
        super("Faulty configurations encountered: " + faultyConfigs);
        this.faultyConfigs = faultyConfigs;
    }

    public List<ExperimentTaskConfiguration> getFaultyConfigs() {
        return faultyConfigs;
    }
}
