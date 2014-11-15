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
import it.uniroma1.lcl.babelnet.BabelNetConfiguration;

import java.io.File;
import java.io.IOException;

import org.aksw.gerbil.bat.annotator.BabelfyAnnotator;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Deprecated
@Component
public class BabelfyAnnotatorConfig extends AbstractAnnotatorConfiguration {

    public static final String ANNOTATOR_NAME = "Babelfy";

    private static final String BABELNET_CONFIG_FILE_PROPERTY_NAME = "org.aksw.gerbil.annotators.BabelfyAnnotatorConfig.ConfigFile";
    private static final String BABELFY_WEB_SERVICE_KEY_PROPERTY_NAME = "org.aksw.gerbil.annotators.BabelfyAnnotatorConfig.Key";

    @Autowired
    private WikipediaApiInterface wikiApi;

    public BabelfyAnnotatorConfig() {
        super(ANNOTATOR_NAME, true, ExperimentType.A2W);
    }

    public BabelfyAnnotatorConfig(WikipediaApiInterface wikiApi) {
        this(wikiApi, false);
    }

    public BabelfyAnnotatorConfig(WikipediaApiInterface wikiApi, boolean optimizedForShortTexts) {
        super(ANNOTATOR_NAME, true, ExperimentType.A2W);
        this.wikiApi = wikiApi;
    }

    @Override
    protected TopicSystem loadAnnotator(ExperimentType type) throws Exception {
        String configFile = GerbilConfiguration.getInstance().getString(BABELNET_CONFIG_FILE_PROPERTY_NAME);
        if (configFile == null) {
            throw new IOException("Couldn't load needed Property \"" + BABELNET_CONFIG_FILE_PROPERTY_NAME + "\".");
        }
        // Load the configuration
        BabelNetConfiguration.getInstance().setConfigurationFile(new File(configFile));

        // Load and use the key if there is one
        String key = GerbilConfiguration.getInstance().getString(BABELFY_WEB_SERVICE_KEY_PROPERTY_NAME);
        if (key == null) {
            return new BabelfyAnnotator(wikiApi);
        } else {
            return new BabelfyAnnotator(key, wikiApi);
        }
    }
}
