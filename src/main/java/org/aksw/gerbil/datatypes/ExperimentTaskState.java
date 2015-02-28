package org.aksw.gerbil.datatypes;

public class ExperimentTaskState {

    private int numberOfExperimentSteps = 0;
    private int maxNumberOfExperimentSteps = 0;

    public ExperimentTaskState(int maxNumberOfExperimentSteps) {
        this.maxNumberOfExperimentSteps = maxNumberOfExperimentSteps;
    }

    public void increaseExperimentStepCount() {
        ++numberOfExperimentSteps;
    }

    public double getExperimentTaskProcess() {
        return ((double) numberOfExperimentSteps) / (double) maxNumberOfExperimentSteps;
    }
}
