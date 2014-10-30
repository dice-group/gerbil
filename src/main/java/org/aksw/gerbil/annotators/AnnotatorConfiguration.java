package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;

import org.aksw.gerbil.datatypes.AdapterConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;

public interface AnnotatorConfiguration extends AdapterConfiguration {

    /**
     * Returns the annotator or null if the annotator can't be used for the
     * given {@link ExperimentType}.
     * 
     * @param type
     * @return
     * @throws GerbilException
     *             if an error occurs while loading the annotator
     */
    public TopicSystem getAnnotator(ExperimentType type) throws GerbilException;
}
