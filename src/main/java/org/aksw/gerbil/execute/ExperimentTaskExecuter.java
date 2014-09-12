package org.aksw.gerbil.execute;

import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;

public interface ExperimentTaskExecuter extends Runnable {
    
    public void executeExperimentTask(ExperimentTaskConfiguration configuration);
}
