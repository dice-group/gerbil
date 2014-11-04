package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.bat.annotator.AgdistisAnnotator;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;

public class AgdistisAnnotatorConfig extends AbstractAnnotatorConfiguration {

    public static final String ANNOTATOR_NAME = "AGDISTIS";

    private static final String AGDISTIS_HOST_PROPERTY_NAME = "org.aksw.gerbil.annotators.AgdistisAnnotatorConfig.Host";
    private static final String AGDISTIS_PORT_PROPERTY_NAME = "org.aksw.gerbil.annotators.AgdistisAnnotatorConfig.Port";

    private WikipediaApiInterface wikiApi;

    public AgdistisAnnotatorConfig(WikipediaApiInterface wikiApi) {
        super(ANNOTATOR_NAME, true, ExperimentType.D2W);
        this.wikiApi = wikiApi;
    }

    @Override
    protected TopicSystem loadAnnotator(ExperimentType type) throws Exception {
        String host = GerbilConfiguration.getInstance().getString(AGDISTIS_HOST_PROPERTY_NAME);
        if (host == null) {
            throw new GerbilException("Couldn't load needed property \"" + AGDISTIS_HOST_PROPERTY_NAME + "\".",
                    ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
        String portString = GerbilConfiguration.getInstance().getString(AGDISTIS_PORT_PROPERTY_NAME);
        if (portString == null) {
            throw new GerbilException("Couldn't load needed property \"" + AGDISTIS_PORT_PROPERTY_NAME + "\".",
                    ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
        int port;
        try {
            port = Integer.parseInt(portString);
        } catch (Exception e) {
            throw new GerbilException("Couldn't parse the integer of the property \"" + AGDISTIS_PORT_PROPERTY_NAME
                    + "\".", e, ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
        return new AgdistisAnnotator(host, port, wikiApi);
    }
}
