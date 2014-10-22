package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.bat.annotator.nif.NIFBasedAnnotatorWebservice;
import org.aksw.gerbil.datatypes.ExperimentType;

public class NIFWebserviceAnnotatorConfiguration extends AbstractAnnotatorConfiguration {

    public static final String ANNOTATOR_NAME = "NIF-based Web Service";

    private String annotaturURL;
    private WikipediaApiInterface wikiApi;
    private DBPediaApi dbpediaApi;

    public NIFWebserviceAnnotatorConfiguration(String annotaturURL, String annotatorName, boolean couldBeCached,
            WikipediaApiInterface wikiApi, DBPediaApi dbpediaApi, ExperimentType... applicableForExperiment) {
        super(annotatorName, couldBeCached, applicableForExperiment);
        this.annotaturURL = annotaturURL;
        this.wikiApi = wikiApi;
        this.dbpediaApi = dbpediaApi;
    }

    @Override
    protected TopicSystem loadAnnotator() throws Exception {
        return new NIFBasedAnnotatorWebservice(annotaturURL, this.getAnnotatorName(), wikiApi, dbpediaApi);
    }

}
