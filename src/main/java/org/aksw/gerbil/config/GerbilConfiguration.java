/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
