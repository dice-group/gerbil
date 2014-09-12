package org.aksw.gerbil;

import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;

public class Experimenter implements Runnable {

    private ExperimentTaskConfiguration configs[];
    private String id;

    public Experimenter(ExperimentTaskConfiguration configs[], String id) {
        this.configs = configs;
        this.id = id;
    }

    @Override
    public void run() {
        
    }

}
