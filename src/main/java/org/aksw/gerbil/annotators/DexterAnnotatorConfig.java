package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.bat.annotator.nif.NIFBasedAnnotatorWebservice;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;

public class DexterAnnotatorConfig extends AbstractAnnotatorConfiguration {

    public static final String ANNOTATOR_NAME = "Dexter";

    private static final String ANNOTATION_URL_PROPERTY_KEY = "org.aksw.gerbil.annotators.DexterAnnotatorConfig.annotationUrl";

    private WikipediaApiInterface wikiApi;
    private DBPediaApi dbpediaApi;

    public DexterAnnotatorConfig(WikipediaApiInterface wikiApi, DBPediaApi dbpediaApi) {
        super(ANNOTATOR_NAME, true, ExperimentType.Sa2W);
        this.wikiApi = wikiApi;
        this.dbpediaApi = dbpediaApi;
    }

    @Override
    protected TopicSystem loadAnnotator(ExperimentType type) throws Exception {
        String annotatorURL = GerbilConfiguration.getInstance().getString(ANNOTATION_URL_PROPERTY_KEY);
        if (annotatorURL == null) {
            throw new GerbilException("Couldn't load the needed property \"" + ANNOTATION_URL_PROPERTY_KEY
                    + "\".", ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
        return new NIFBasedAnnotatorWebservice(annotatorURL, this.getName(), wikiApi, dbpediaApi);
    }
}
