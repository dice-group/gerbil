package org.aksw.gerbil.annotators;

import java.io.File;
import java.io.IOException;

import it.acubelab.batframework.problems.TopicSystem;
import it.uniroma1.lcl.babelnet.BabelNetConfiguration;

import org.aksw.gerbil.GerbilConfiguration;
import org.aksw.gerbil.bat.annotator.BabelfyAnnotator;
import org.aksw.gerbil.datatypes.ExperimentType;

public class BabelfyAnnotatorConfig extends AbstractAnnotatorConfiguration {

    private static final String BABELNET_CONFIG_FILE_PROPERTY_NAME = "org.aksw.gerbil.annotators.BabelfyAnnotatorConfig.ConfigFile";
    private static final String BABELFY_WEB_SERVICE_KEY_PROPERTY_NAME = "org.aksw.gerbil.annotators.BabelfyAnnotatorConfig.Key";

    public BabelfyAnnotatorConfig() {
        super("BabelFy", true, ExperimentType.D2W);
    }

    @Override
    protected TopicSystem loadAnnotator() throws Exception {
        String configFile = GerbilConfiguration.getInstance().getString(BABELNET_CONFIG_FILE_PROPERTY_NAME);
        if (configFile == null) {
            throw new IOException("Couldn't load needed Property \"" + BABELNET_CONFIG_FILE_PROPERTY_NAME + "\".");
        }
        // Load the configuration
        BabelNetConfiguration.getInstance().setConfigurationFile(new File(configFile));

        // Load and use the key if there is one
        String key = GerbilConfiguration.getInstance().getString(BABELFY_WEB_SERVICE_KEY_PROPERTY_NAME);
        if (key == null) {
            return new BabelfyAnnotator();
        } else {
            return new BabelfyAnnotator(key);
        }
    }
}
