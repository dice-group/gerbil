package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;

import org.aksw.gerbil.bat.annotator.BabelfyAnnotator;
import org.aksw.gerbil.datatypes.ExperimentType;

public class BabelfyAnnotatorConfig extends AbstractAnnotatorConfiguration {

    public BabelfyAnnotatorConfig() {
        super("BabelFy", true, ExperimentType.D2W);
    }

    @Override
    protected TopicSystem loadAnnotator() throws Exception {
        return new BabelfyAnnotator();
    }

}
