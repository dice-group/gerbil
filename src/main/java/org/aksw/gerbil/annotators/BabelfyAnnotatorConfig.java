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
package org.aksw.gerbil.annotators;

import it.unipi.di.acube.batframework.problems.TopicSystem;
import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;
import it.uniroma1.lcl.babelfy.commons.BabelfyConfiguration;
import org.aksw.gerbil.bat.annotator.BabelfyAnnotator;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;

import java.io.File;
import java.io.IOException;

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
