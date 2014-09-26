package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;

import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;

public abstract class AbstractAnnotatorConfiguration implements AnnotatorConfiguration {

    private String annotatorName;
    private boolean couldBeCached;
    private ExperimentType applicableForExperiments[];

    public AbstractAnnotatorConfiguration(String annotatorName, boolean couldBeCached,
            ExperimentType... applicableForExperiment) {
        this.annotatorName = annotatorName;
        this.couldBeCached = couldBeCached;
        applicableForExperiments = applicableForExperiment;
    }

    @Override
    public String getAnnotatorName() {
        return annotatorName;
    }

    @Override
    public boolean couldBeCached() {
        return couldBeCached;
    }

    @Override
    public TopicSystem getAnnotator(ExperimentType experimentType) throws GerbilException {
        for (int i = 0; i < applicableForExperiments.length; ++i) {
            if (applicableForExperiments[i].equalsOrContainsType(experimentType)) {
                try {
                    return loadAnnotator();
                } catch (Exception e) {
                    throw new GerbilException(e, ErrorTypes.ANNOTATOR_LOADING_ERROR);
                }
            }
        }
        return null;
    }

    protected abstract TopicSystem loadAnnotator() throws Exception;

    public boolean isCouldBeCached() {
        return couldBeCached;
    }

    public void setCouldBeCached(boolean couldBeCached) {
        this.couldBeCached = couldBeCached;
    }

    public ExperimentType[] getApplicableForExperiments() {
        return applicableForExperiments;
    }

    public void setApplicableForExperiments(ExperimentType[] applicableForExperiments) {
        this.applicableForExperiments = applicableForExperiments;
    }

    public void setAnnotatorName(String annotatorName) {
        this.annotatorName = annotatorName;
    }
}
