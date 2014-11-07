package org.aksw.gerbil.config;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the properties needed by GERBIL. Implements the Singleton pattern.
 * 
 * @author m.roeder
 * 
 *         FIXME we should use the Apache Commons Configuration for reading and managing our properties.
 * 
 */
public class GerbilConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(GerbilConfiguration.class);

    private static final String DEFAULT_GERBIL_PROPERTIES_FILE_NAME = "gerbil.properties";
    public static final String GERBIL_DATAPATH_PROPERTY_NAME = "org.aksw.gerbil.DataPath";

    private static Configuration instance = null;

    public static synchronized Configuration getInstance() {
        if (instance == null) {
            instance = new CompositeConfiguration();
            loadAdditionalProperties(DEFAULT_GERBIL_PROPERTIES_FILE_NAME);
        }
        return instance;
    }

    public static synchronized void loadAdditionalProperties(String fileName) {
        try {
            ((CompositeConfiguration) getInstance()).addConfiguration(new PropertiesConfiguration(fileName));
        } catch (ConfigurationException e) {
            LOGGER.error("Couldnt load Properties from the properties file (\"" + fileName
                    + "\"). This GERBIL instance won't work as expected.", e);
        }
    }
}
