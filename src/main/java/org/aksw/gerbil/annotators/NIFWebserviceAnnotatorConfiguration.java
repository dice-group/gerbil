package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;

import org.aksw.gerbil.bat.annotator.nif.NIFBasedAnnotatorWebservice;
import org.aksw.gerbil.datatypes.ExperimentType;

public class NIFWebserviceAnnotatorConfiguration extends AbstractAnnotatorConfiguration {
    
    public static final String ANNOTATOR_NAME = "NIF-based Web Service";

    private String annotaturURL;

    public NIFWebserviceAnnotatorConfiguration(String annotaturURL, String annotatorName, boolean couldBeCached,
            ExperimentType... applicableForExperiment) {
        super(annotatorName, false, applicableForExperiment);
        this.annotaturURL = annotaturURL;
    }

    @Override
    protected TopicSystem loadAnnotator() throws Exception {
        return new NIFBasedAnnotatorWebservice(annotaturURL, this.getAnnotatorName());
    }

}
