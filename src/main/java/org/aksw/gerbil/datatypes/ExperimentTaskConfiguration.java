package org.aksw.gerbil.datatypes;

public class ExperimentTaskConfiguration {

    public AnnotatorConfiguration annotatorConfig;
    public DatasetConfiguration datasetConfig;
    public ExperimentType type; // enum
    public Metric metric; // enum

    public ExperimentTaskConfiguration(AnnotatorConfiguration annotatorConfig, DatasetConfiguration datasetConfig,
            ExperimentType type, Metric metric) {
        super();
        this.annotatorConfig = annotatorConfig;
        this.datasetConfig = datasetConfig;
        this.type = type;
        this.metric = metric;
    }

    public AnnotatorConfiguration getAnnotatorConfig() {
        return annotatorConfig;
    }

    public void setAnnotatorConfig(AnnotatorConfiguration annotatorConfig) {
        this.annotatorConfig = annotatorConfig;
    }

    public DatasetConfiguration getDatasetConfig() {
        return datasetConfig;
    }

    public void setDatasetConfig(DatasetConfiguration datasetConfig) {
        this.datasetConfig = datasetConfig;
    }

    public ExperimentType getType() {
        return type;
    }

    public void setType(ExperimentType type) {
        this.type = type;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

}
