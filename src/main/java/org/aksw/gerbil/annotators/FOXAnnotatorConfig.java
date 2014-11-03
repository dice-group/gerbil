package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.bat.annotator.FOXAnnotator;
import org.aksw.gerbil.datatypes.ExperimentType;

public class FOXAnnotatorConfig extends AbstractAnnotatorConfiguration {

    private WikipediaApiInterface wikiApi;

    public FOXAnnotatorConfig(WikipediaApiInterface wikiApi) {
        super(FOXAnnotator.NAME, true, ExperimentType.A2W);
        this.wikiApi = wikiApi;
    }

    @Override
    protected TopicSystem loadAnnotator() throws Exception {
        return new FOXAnnotator(wikiApi);
    }

}
