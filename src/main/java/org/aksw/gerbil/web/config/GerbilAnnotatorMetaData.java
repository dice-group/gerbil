package org.aksw.gerbil.web.config;

import it.acubelab.batframework.problems.TopicSystem;

import org.aksw.gerbil.annotations.GerbilAnnotator;
import org.aksw.gerbil.annotators.AnnotatorConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;

public class GerbilAnnotatorMetaData extends AbstractGerbilAdapterMetaData implements AnnotatorConfiguration {

    private Class<? extends TopicSystem> annotatorClass;

    public GerbilAnnotatorMetaData(GerbilAnnotator annotation, Class<? extends TopicSystem> annotatorClass) {
        super(annotation.name(), annotation.couldBeCached(), annotation.applicableForExperiments());
        this.annotatorClass = annotatorClass;
    }

    public TopicSystem loadAnnotator() throws InstantiationException, IllegalAccessException, GerbilException {
        return annotatorClass.newInstance();
    }

    @Override
    public TopicSystem getAnnotator(ExperimentType type) throws GerbilException {
        if (this.isApplicableForExperiment(type)) {
            try {
                return loadAnnotator();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new GerbilException("Exception while instantiating annotator.", e,
                        ErrorTypes.ANNOTATOR_LOADING_ERROR);
            }
        } else {
            return null;
        }
    }
}
