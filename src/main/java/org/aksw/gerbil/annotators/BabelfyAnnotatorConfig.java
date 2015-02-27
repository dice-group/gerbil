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
package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.utils.WikipediaApiInterface;
import it.uniroma1.lcl.babelfy.commons.BabelfyConfiguration;

import java.io.File;
import java.io.IOException;

import org.aksw.gerbil.bat.annotator.BabelfyAnnotator;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;

public class BabelfyAnnotatorConfig extends AbstractAnnotatorConfiguration {

    public static final String ANNOTATOR_NAME = "Babelfy";

    private static final String BABELNET_CONFIG_FILE_PROPERTY_NAME = "org.aksw.gerbil.annotators.BabelfyAnnotatorConfig.ConfigFile";
    private static final String BABELFY_WEB_SERVICE_KEY_PROPERTY_NAME = "org.aksw.gerbil.annotators.BabelfyAnnotatorConfig.Key";

    private WikipediaApiInterface wikiApi;
    /**
     * The annotator instance shared by all experiment tasks.
     */
    private BabelfyAnnotator instance = null;

    public BabelfyAnnotatorConfig(WikipediaApiInterface wikiApi) {
        super(ANNOTATOR_NAME, true, ExperimentType.Sa2KB);
        this.wikiApi = wikiApi;
    }

    @Override
    protected TopicSystem loadAnnotator(ExperimentType type) throws Exception {
        if (instance == null) {
            String configFile = GerbilConfiguration.getInstance().getString(BABELNET_CONFIG_FILE_PROPERTY_NAME);
            if (configFile == null) {
                throw new IOException("Couldn't load needed Property \"" + BABELNET_CONFIG_FILE_PROPERTY_NAME + "\".");
            }
            // Load the configuration
            BabelfyConfiguration.getInstance().setConfigurationFile(new File(configFile));

            // Load and use the key if there is one
            String key = GerbilConfiguration.getInstance().getString(BABELFY_WEB_SERVICE_KEY_PROPERTY_NAME);
            BabelfyConfiguration.getInstance().setRFkey(key);
            instance = new BabelfyAnnotator(wikiApi);
        }
        return instance;
    }
}
