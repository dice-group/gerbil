package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;

import org.aksw.gerbil.datatypes.ExperimentType;

public interface AnnotatorConfiguration {

    public String getAnnotatorName();

    public boolean couldBeCached();

    public TopicSystem getAnnotator(ExperimentType type);

    // public A2WSystem getA2WAnnotator();
    //
    // public C2WSystem getC2WAnnotator();
    //
    // public D2WSystem getD2WAnnotator();
    //
    // public Sa2WSystem getSa2WAnnotator();
    //
    // public Sc2WSystem getSc2WAnnotator();
}
