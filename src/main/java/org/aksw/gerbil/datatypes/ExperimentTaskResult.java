package org.aksw.gerbil.datatypes;

import org.aksw.gerbil.annotators.AnnotatorConfiguration;
import org.aksw.gerbil.datasets.DatasetConfiguration;
import org.aksw.gerbil.matching.Matching;

public class ExperimentTaskResult extends ExperimentTaskConfiguration {

    public double result;

    public ExperimentTaskResult(AnnotatorConfiguration annotatorConfig, DatasetConfiguration datasetConfig,
            ExperimentType type, Matching metric, double result) {
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
