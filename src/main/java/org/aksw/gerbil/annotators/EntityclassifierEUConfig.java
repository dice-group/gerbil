package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.bat.annotator.nif.NIFBasedAnnotatorWebservice;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;

public class EntityclassifierEUConfig extends AbstractAnnotatorConfiguration {

    public static final String ANNOTATOR_NAME = "Entityclassifier.eu NER";

    private static final String URL_PROPERTY_KEY = "org.aksw.gerbil.annotators.EntityclassifierEUConfig.url";
    private static final String API_KEY_PROPERTY_KEY = "org.aksw.gerbil.annotators.EntityclassifierEUConfig.apiKey";

    private WikipediaApiInterface wikiApi;
    private DBPediaApi dbpediaApi;

    public EntityclassifierEUConfig(WikipediaApiInterface wikiApi, DBPediaApi dbpediaApi) {
        super(ANNOTATOR_NAME, true, ExperimentType.Sa2KB);
        this.wikiApi = wikiApi;
        this.dbpediaApi = dbpediaApi;
    }

    @Override
    protected TopicSystem loadAnnotator(ExperimentType type) throws Exception {
        String annotatorURL = GerbilConfiguration.getInstance().getString(URL_PROPERTY_KEY);
        if (annotatorURL == null) {
            throw new GerbilException("Couldn't load property \"" + URL_PROPERTY_KEY
                    + "\" containing the URL for the experiment type " + type, ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
        String apiKey = GerbilConfiguration.getInstance().getString(API_KEY_PROPERTY_KEY);
        if (apiKey == null) {
            throw new GerbilException("Couldn't load property \"" + API_KEY_PROPERTY_KEY
                    + "\" containing the API Key for the experiment type " + type, ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
        return new NIFBasedAnnotatorWebservice(annotatorURL + apiKey, this.getName(), wikiApi, dbpediaApi);
    }
}
