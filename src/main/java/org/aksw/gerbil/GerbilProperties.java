package org.aksw.gerbil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GerbilProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(GerbilProperties.class);

    private static final String GERBIL_PROPERTIES_FILE_NAME = "gerbil.properties";
    public static final String GERBIL_DATAPATH_PROPERTY_NAME = "org.aksw.gerbil.DataPath";

    private static Properties gerbilProperties = null;

    public static synchronized void loadProperties() {
        if (gerbilProperties == null) {
            try {
                InputStream stream = Experimenter.class.getClassLoader()
                        .getResourceAsStream(GERBIL_PROPERTIES_FILE_NAME);
                if (stream == null) {
                    LOGGER.error("Couldnt load Properties from the properties file (\"" + GERBIL_PROPERTIES_FILE_NAME
                            + "\"). This GERBIL instance won't work as expected.");
                    return;
                }
                BufferedInputStream bStream = new BufferedInputStream(stream);
                gerbilProperties = new Properties();
                gerbilProperties.load(bStream);
                bStream.close();
            } catch (IOException e) {
                LOGGER.error("Couldnt load Properties from the properties file (\"" + GERBIL_PROPERTIES_FILE_NAME
                        + "\"). This GERBIL instance won't work as expected.", e);
                gerbilProperties = null;
            }
        }
    }

    public static String getPropertyValue(String propertyName) {
        String value = null;
        if (gerbilProperties != null) {
            value = gerbilProperties.getProperty(propertyName);
            if (value.contains("{0}")) {
                String gerbilDataPath = gerbilProperties.getProperty(GERBIL_DATAPATH_PROPERTY_NAME);
                value = MessageFormat.format(value, gerbilDataPath);
            }
        }
        return value;
    }
}
