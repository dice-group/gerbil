package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;

import org.aksw.gerbil.datatypes.AbstractAdapterConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;

public abstract class AbstractAnnotatorConfiguration extends AbstractAdapterConfiguration implements
        AnnotatorConfiguration {

    public AbstractAnnotatorConfiguration(String annotatorName, boolean couldBeCached,
            ExperimentType... applicableForExperiment) {
        super(annotatorName, couldBeCached, applicableForExperiment);
    }

    @Override
    public TopicSystem getAnnotator(ExperimentType experimentType) throws GerbilException {
        for (int i = 0; i < applicableForExperiments.length; ++i) {
            if (applicableForExperiments[i].equalsOrContainsType(experimentType)) {
                try {
                    return loadAnnotator(experimentType);
                } catch (Exception e) {
                    throw new GerbilException(e, ErrorTypes.ANNOTATOR_LOADING_ERROR);
                }
            }
        }
        return null;
    }

    protected abstract TopicSystem loadAnnotator(ExperimentType type) throws Exception;

    public ExperimentType[] getApplicableForExperiments() {
        return applicableForExperiments;
    }

    public void setApplicableForExperiments(ExperimentType[] applicableForExperiments) {
        this.applicableForExperiments = applicableForExperiments;
    }
}
