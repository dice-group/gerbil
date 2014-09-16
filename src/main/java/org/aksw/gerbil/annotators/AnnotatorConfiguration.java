package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;

import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;

public interface AnnotatorConfiguration {

    public String getAnnotatorName();

    public boolean couldBeCached();

    public TopicSystem getAnnotator(ExperimentType type) throws GerbilException;
}
