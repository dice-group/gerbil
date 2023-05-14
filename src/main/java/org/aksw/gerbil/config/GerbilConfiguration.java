/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.config;

import java.io.File;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the properties needed by GERBIL. Implements the Singleton pattern.
 * 
 * Note that the singleton instance is not an instance of this class but of the
 * {@link Configuration} class.
 * 
 * @author m.roeder
 * 
 */
public class GerbilConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(GerbilConfiguration.class);

    private static final String DEFAULT_GERBIL_PROPERTIES_FILE_NAME = "gerbil.properties";
    public static final String GERBIL_PROP_DIR_KEY = "GERBIL_PROP_DIR";
    public static final String GERBIL_DATAPATH_PROPERTY_NAME = "org.aksw.gerbil.DataPath";
    public static final String GERBIL_VERSION_PROPERTY_NAME = "org.aksw.gerbil.Version";

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
            ((CompositeConfiguration) getInstance())
                    .addConfiguration(new PropertiesConfiguration(derivePropertiesPath(fileName)));
        } catch (ConfigurationException e) {
            LOGGER.error("Couldnt load Properties from the properties file (\"" + fileName
                    + "\"). This GERBIL instance won't work as expected.", e);
        }
    }

    public static String derivePropertiesPath(String fileName) {
        if (System.getenv().containsKey(GERBIL_PROP_DIR_KEY)) {
            String temp = System.getenv().get(GERBIL_PROP_DIR_KEY);
            if (!temp.endsWith(File.separator)) {
                temp += File.separator;
            }
            return temp + fileName;
        } else {
            return fileName;
        }
    }

    public static String getGerbilVersion() {
        return getInstance().getString(GERBIL_VERSION_PROPERTY_NAME);
    }
}
