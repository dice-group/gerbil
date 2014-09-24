package org.aksw.gerbil;

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

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new CompositeConfiguration();
            loadAdditionalProperties(DEFAULT_GERBIL_PROPERTIES_FILE_NAME);
        }
        return instance;
    }

    // private Properties gerbilProperties = new Properties();

    // private GerbilConfiguration() {
    // }

    public static synchronized void loadAdditionalProperties(String fileName) {
        // try {
        // InputStream stream = Experimenter.class.getClassLoader()
        // .getResourceAsStream(fileName);
        // if (stream == null) {
        // LOGGER.error("Couldnt load Properties from the properties file (\"" + fileName
        // + "\"). This GERBIL instance won't work as expected.");
        // return;
        // }
        // BufferedInputStream bStream = new BufferedInputStream(stream);
        // gerbilProperties.load(bStream);
        // bStream.close();
        // } catch (IOException e) {
        // LOGGER.error("Couldnt load Properties from the properties file (\"" + fileName
        // + "\"). This GERBIL instance won't work as expected.", e);
        // gerbilProperties = null;
        // }
        try {
            ((CompositeConfiguration) getInstance()).addConfiguration(new PropertiesConfiguration(fileName));
        } catch (ConfigurationException e) {
            LOGGER.error("Couldnt load Properties from the properties file (\"" + fileName
                    + "\"). This GERBIL instance won't work as expected.", e);
        }
    }

    // public String getPropertyValue(String propertyName) {
    // String value = null;
    // if (gerbilProperties != null) {
    // value = gerbilProperties.getProperty(propertyName);
    // if (value.contains("{0}")) {
    // String gerbilDataPath = gerbilProperties.getProperty(GERBIL_DATAPATH_PROPERTY_NAME);
    // value = MessageFormat.format(value, gerbilDataPath);
    // }
    // }
    // return value;
    // }
}
