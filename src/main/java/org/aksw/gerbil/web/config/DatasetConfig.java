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
package org.aksw.gerbil.web.config;

import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.datasets.DatasetConfiguration;
import org.aksw.gerbil.datasets.KnownNIFFileDatasetConfigs;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "org.aksw.gerbil.datasets")
public class DatasetConfig {

    // private static final Logger LOGGER =
    // LoggerFactory.getLogger(DatasetConfig.class);

    // @Bean
    // public AdapterList<DatasetConfiguration>
    // annotatorList(List<DatasetConfiguration> configurations) {
    // // if (LOGGER.isInfoEnabled()) {
    // StringBuilder builder = new StringBuilder();
    // builder.append("Found ");
    // builder.append(configurations.size());
    // builder.append(" annotators [");
    // for (int i = 0; i < configurations.size(); i++) {
    // if (i > 0) {
    // builder.append(", ");
    // }
    // builder.append(configurations.get(i).getClass().getSimpleName());
    // }
    // builder.append("].");
    // LOGGER.error(builder.toString());
    // // }
    // return new AdapterList<DatasetConfiguration>(configurations);
    // }

    public AdapterList<DatasetConfiguration> knownNIFFileDatasetList(WikipediaApiInterface wikiApi,
            DBPediaApi dbpediaApi) {
        return KnownNIFFileDatasetConfigs.create(wikiApi, dbpediaApi);
    }
}
