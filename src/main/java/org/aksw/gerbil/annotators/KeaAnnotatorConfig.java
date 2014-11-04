package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.bat.annotator.nif.NIFBasedAnnotatorWebservice;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeaAnnotatorConfig extends AbstractAnnotatorConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeaAnnotatorConfig.class);

    public static final String ANNOTATOR_NAME = "Kea";

    private static final String ANNOTATION_URL_PROPERTY_KEY = "org.aksw.gerbil.annotators.KeaAnnotatorConfig.annotationUrl";
    private static final String DISAMBIGATION_URL_PROPERTY_KEY = "org.aksw.gerbil.annotators.KeaAnnotatorConfig.disambiguationUrl";
    private static final String USER_NAME_PROPERTY_KEY = "org.aksw.gerbil.annotators.KeaAnnotatorConfig.user";
    private static final String PASSWORD_PROPERTY_KEY = "org.aksw.gerbil.annotators.KeaAnnotatorConfig.password";

    private WikipediaApiInterface wikiApi;
    private DBPediaApi dbpediaApi;

    public KeaAnnotatorConfig(WikipediaApiInterface wikiApi, DBPediaApi dbpediaApi) {
        super(ANNOTATOR_NAME, true, ExperimentType.Sa2W);
        this.wikiApi = wikiApi;
        this.dbpediaApi = dbpediaApi;
    }

    @Override
    protected TopicSystem loadAnnotator(ExperimentType type) throws Exception {
        String propertyKey;
        // If this we need a D2W system
        if (ExperimentType.D2W.equalsOrContainsType(type)) {
            propertyKey = DISAMBIGATION_URL_PROPERTY_KEY;
        } else {
            propertyKey = ANNOTATION_URL_PROPERTY_KEY;
        }
        String annotatorURL = GerbilConfiguration.getInstance().getString(ANNOTATION_URL_PROPERTY_KEY);
        if (annotatorURL == null) {
            throw new GerbilException("Couldn't load property \"" + propertyKey
                    + "\" containing the URL for the experiment type " + type, ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
        annotatorURL = annotatorURL.replace("http://", "");
        StringBuilder url = new StringBuilder();
        url.append("http://");

        // load user name and password
        String user = GerbilConfiguration.getInstance().getString(USER_NAME_PROPERTY_KEY);
        String password = GerbilConfiguration.getInstance().getString(PASSWORD_PROPERTY_KEY);
        if ((user != null) && (password != null)) {
            url.append(user);
            url.append(':');
            url.append(password);
            url.append('@');
        } else {
            LOGGER.error("Couldn't load the user name (" + USER_NAME_PROPERTY_KEY + ") or the password property ("
                    + PASSWORD_PROPERTY_KEY + "). It is possbile that this annotator won't work.");
        }
        url.append(annotatorURL);

        return new NIFBasedAnnotatorWebservice(url.toString(), this.getName(), wikiApi, dbpediaApi);
    }
}
