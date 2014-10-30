package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.systemPlugins.AgdistisAnnotator;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.datatypes.ExperimentType;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Agdistis2AnnotatorConfig extends AbstractAnnotatorConfiguration {

    public static final String ANNOTATOR_NAME = "AGDISTIS";

    private WikipediaApiInterface wikiApi;

    public Agdistis2AnnotatorConfig(/* WikipediaApiInterface wikiApi */) {
        super(ANNOTATOR_NAME, true, ExperimentType.D2W);
        // this.wikiApi = wikiApi; Lars ist schuld
    }

    @Override
    protected TopicSystem loadAnnotator() throws Exception {
        return new AgdistisAnnotator(wikiApi);
    }
}
