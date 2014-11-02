package org.aksw.gerbil.datatypes;

import org.aksw.gerbil.annotators.AnnotatorConfiguration;
import org.aksw.gerbil.datasets.DatasetConfiguration;
import org.aksw.gerbil.matching.Matching;

public class ExperimentTaskConfiguration {

    public AnnotatorConfiguration annotatorConfig;
    public DatasetConfiguration datasetConfig;
    public ExperimentType type;
    public Matching matching;

    public ExperimentTaskConfiguration(AnnotatorConfiguration annotatorConfig, DatasetConfiguration datasetConfig,
            ExperimentType type, Matching matching) {
        super();
        this.annotatorConfig = annotatorConfig;
        this.datasetConfig = datasetConfig;
        this.type = type;
        this.matching = matching;
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

    public Matching getMatching() {
        return matching;
    }

    public void setMatching(Matching matching) {
        this.matching = matching;
    }

    @Override
    public String toString() {
        return "eTConfig(\"" + annotatorConfig.getName() + "\",\"" + datasetConfig.getName() + "\",\"" + type.name()
                + "\",\"" + matching.name() + "\")";
    }
}
