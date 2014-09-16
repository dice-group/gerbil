package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;

import java.util.HashSet;
import java.util.Set;

import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;

public abstract class AbstractAnnotatorConfiguration implements AnnotatorConfiguration {

    private String annotatorName;
    private boolean couldBeCached;
    private Set<ExperimentType> applicableForExperiments;

    public AbstractAnnotatorConfiguration(String annotatorName, boolean couldBeCached,
            ExperimentType... applicableForExperiment) {
        this.annotatorName = annotatorName;
        this.couldBeCached = couldBeCached;
        applicableForExperiments = new HashSet<ExperimentType>();
        for (int i = 0; i < applicableForExperiment.length; ++i) {
            applicableForExperiments.add(applicableForExperiment[i]);
        }
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
        if (applicableForExperiments.contains(experimentType)) {
            try {
                return loadAnnotator();
            } catch (Exception e) {
                throw new GerbilException(e, ErrorTypes.DATASET_LOADING_ERROR);
            }
        } else {
            return null;
        }
    }

    protected abstract TopicSystem loadAnnotator() throws Exception;

}
