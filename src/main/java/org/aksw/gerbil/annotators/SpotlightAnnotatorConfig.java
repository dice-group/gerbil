package org.aksw.gerbil.annotators;

import org.aksw.gerbil.datatypes.ExperimentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.systemPlugins.SpotlightAnnotator;
import it.acubelab.batframework.utils.WikipediaApiInterface;

@Component
public class SpotlightAnnotatorConfig extends AbstractAnnotatorConfiguration {

    public static final String ANNOTATOR_NAME = "DBpedia Spotlight";

    @Autowired
    private WikipediaApiInterface wikiApi;

    @Autowired
    private DBPediaApi dbpApi;

    public SpotlightAnnotatorConfig() {
        super(ANNOTATOR_NAME, true, new ExperimentType[] { ExperimentType.Sa2W });
    }

    public SpotlightAnnotatorConfig(WikipediaApiInterface wikiApi, DBPediaApi dbpApi) {
        this();
        this.wikiApi = wikiApi;
        this.dbpApi = dbpApi;
    }

    @Override
    protected TopicSystem loadAnnotator() throws Exception {
        return new SpotlightAnnotator(dbpApi, wikiApi);
    }

}
