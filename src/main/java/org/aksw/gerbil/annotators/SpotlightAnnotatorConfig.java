package org.aksw.gerbil.annotators;

import org.aksw.gerbil.datatypes.ExperimentType;

import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.systemPlugins.SpotlightAnnotator;
import it.acubelab.batframework.utils.WikipediaApiInterface;

public class SpotlightAnnotatorConfig extends AbstractAnnotatorConfiguration {

    private WikipediaApiInterface wikiApi;
    private DBPediaApi dbpApi;

    public SpotlightAnnotatorConfig(WikipediaApiInterface wikiApi, DBPediaApi dbpApi) {
        super("DBpediaSpotlight", true, new ExperimentType[] { ExperimentType.Sa2W });
        this.wikiApi = wikiApi;
        this.dbpApi = dbpApi;
    }

    @Override
    protected TopicSystem loadAnnotator() throws Exception {
        return new SpotlightAnnotator(dbpApi, wikiApi);
    }

}
