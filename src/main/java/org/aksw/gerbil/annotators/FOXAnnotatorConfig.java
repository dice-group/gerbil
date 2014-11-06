package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.bat.annotator.FOXAnnotator;
import org.aksw.gerbil.datatypes.ExperimentType;

public class FOXAnnotatorConfig extends AbstractAnnotatorConfiguration {

    private WikipediaApiInterface wikiApi;
    // don't cache me in the final version
    private static boolean        cache = false;

    public FOXAnnotatorConfig(WikipediaApiInterface wikiApi) {
        super(FOXAnnotator.NAME, cache, ExperimentType.Sa2W);
        this.wikiApi = wikiApi;
    }

    @Override
    protected TopicSystem loadAnnotator(ExperimentType type) throws Exception {
        return new FOXAnnotator(wikiApi);
    }
}
