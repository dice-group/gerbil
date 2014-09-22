package org.aksw.gerbil.datatypes;

import org.aksw.gerbil.annotators.AnnotatorConfiguration;
import org.aksw.gerbil.datasets.DatasetConfiguration;
import org.aksw.gerbil.matching.Matching;

public class ExperimentTaskResult extends ExperimentTaskConfiguration {

    public static final int MICRO_F1_MEASURE_INDEX = 0;
    public static final int MICRO_PRECISION_INDEX = 1;
    public static final int MICRO_RECALL_INDEX = 2;
    public static final int MACRO_F1_MEASURE_INDEX = 3;
    public static final int MACRO_PRECISION_INDEX = 4;
    public static final int MACRO_RECALL_INDEX = 5;

    public double results[];

    public ExperimentTaskResult(AnnotatorConfiguration annotatorConfig, DatasetConfiguration datasetConfig,
            ExperimentType type, Matching matching, double results[]) {
        super(annotatorConfig, datasetConfig, type, matching);
        this.results = results;
    }

    public ExperimentTaskResult(ExperimentTaskConfiguration configuration, double results[]) {
        super(configuration.annotatorConfig, configuration.datasetConfig, configuration.type, configuration.matching);
        this.results = results;
    }

    public double[] getResults() {
        return results;
    }

    public void setResults(double results[]) {
        this.results = results;
    }

    public double getMicroF1Measure() {
        return results[MICRO_F1_MEASURE_INDEX];
    }

    public double getMicroPrecision() {
        return results[MICRO_PRECISION_INDEX];
    }

    public double getMicroRecall() {
        return results[MICRO_RECALL_INDEX];
    }

    public double getMacroF1Measure() {
        return results[MACRO_F1_MEASURE_INDEX];
    }

    public double getMacroPrecision() {
        return results[MACRO_PRECISION_INDEX];
    }

    public double getMacroRecall() {
        return results[MACRO_RECALL_INDEX];
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ExperimentTaskResult(micF1=");
        builder.append(results[MICRO_F1_MEASURE_INDEX]);
        builder.append(",micPrecision=");
        builder.append(results[MICRO_PRECISION_INDEX]);
        builder.append(",micRecall=");
        builder.append(results[MICRO_RECALL_INDEX]);
        builder.append(",macF1=");
        builder.append(results[MACRO_F1_MEASURE_INDEX]);
        builder.append(",macPrecision=");
        builder.append(results[MACRO_PRECISION_INDEX]);
        builder.append(",macRecall=");
        builder.append(results[MACRO_RECALL_INDEX]);
        builder.append(")");
        return builder.toString();
    }
}
