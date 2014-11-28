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

import it.acubelab.batframework.problems.TopicDataset;
import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.annotations.GerbilDataset;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datasets.DatasetConfiguration;
import org.aksw.gerbil.datasets.KnownNIFFileDatasetConfigs;
import org.aksw.gerbil.datatypes.GerbilDatasetMetaData;
import org.aksw.gerbil.web.config.spring.AndTypeFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;

@Configuration
public class DatasetConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotatorConfig.class);

    private static final String DATASET_PACKAGES_PROPERTY_KEY = "org.aksw.gerbil.web.config.DatasetConfig.DatasetPackages";

    @SuppressWarnings("unchecked")
    @Bean
    public AdapterList<DatasetConfiguration> datasetList(ApplicationContext context) throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AndTypeFilter(new AnnotationTypeFilter(GerbilDataset.class),
                new AssignableTypeFilter(TopicDataset.class)));

        List<DatasetConfiguration> configs = new ArrayList<DatasetConfiguration>();
        String packages[] = GerbilConfiguration.getInstance().getStringArray(DATASET_PACKAGES_PROPERTY_KEY);
        GerbilDataset annotation;
        Class<?> clazz;
        for (int i = 0; i < packages.length; i++) {
            Set<BeanDefinition> definitions = scanner.findCandidateComponents(packages[i]);
            for (BeanDefinition beanDefinition : definitions) {
                clazz = this.getClass().getClassLoader().loadClass(beanDefinition.getBeanClassName());
                annotation = clazz.getAnnotation(GerbilDataset.class);
                configs.add(new GerbilDatasetMetaData(annotation, context, (Class<? extends TopicDataset>) clazz));
            }
        }

        if (LOGGER.isInfoEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append("Found ");
            builder.append(configs.size());
            builder.append(" datasets [");
            for (int i = 0; i < configs.size(); i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(configs.get(i).getClass().getSimpleName());
                builder.append('(');
                builder.append(configs.get(i).getName());
                builder.append(')');
            }
            builder.append("].");
            LOGGER.info(builder.toString());
        }
        return new AdapterList<DatasetConfiguration>(configs);
    }

    public AdapterList<DatasetConfiguration> knownNIFFileDatasetList(WikipediaApiInterface wikiApi,
            DBPediaApi dbpediaApi) {
        return KnownNIFFileDatasetConfigs.create(wikiApi, dbpediaApi);
    }
}
