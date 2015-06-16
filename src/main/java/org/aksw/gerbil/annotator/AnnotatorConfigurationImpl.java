package org.aksw.gerbil.annotator;

import java.lang.reflect.Constructor;

import org.aksw.gerbil.annotators.AnnotatorConfiguration;
import org.aksw.gerbil.datatypes.AbstractAdapterConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;

public class AnnotatorConfigurationImpl extends AbstractAdapterConfiguration implements AnnotatorConfiguration {

    protected Constructor<? extends Annotator> constructor;
    protected Object constructorArgs[];

    public AnnotatorConfigurationImpl(String annotatorName, boolean couldBeCached,
            Constructor<? extends Annotator> constructor, Object constructorArgs[],
            ExperimentType[] applicableForExperiment) {
        super(annotatorName, couldBeCached, applicableForExperiment);
        this.constructor = constructor;
        this.constructorArgs = constructorArgs;
    }

    @Override
    public Annotator getAnnotator(ExperimentType experimentType) throws GerbilException {
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

    protected Annotator loadAnnotator() throws Exception {
        return constructor.newInstance(constructorArgs);
    }

    public ExperimentType[] getApplicableForExperiments() {
        return applicableForExperiments;
    }

    public void setApplicableForExperiments(ExperimentType[] applicableForExperiments) {
        this.applicableForExperiments = applicableForExperiments;
    }
}