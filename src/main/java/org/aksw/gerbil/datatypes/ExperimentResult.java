package org.aksw.gerbil.datatypes;

public class ExperimentResult extends ExperimentTaskConfiguration {

    public double result;

    public ExperimentResult(AnnotatorConfiguration annotatorConfig, DatasetConfiguration datasetConfig,
            ExperimentType type, Metric metric, double result) {
        super(annotatorConfig, datasetConfig, type, metric);
        this.result = result;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }

}
