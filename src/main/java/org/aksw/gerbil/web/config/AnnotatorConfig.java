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

import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.annotations.GerbilAnnotator;
import org.aksw.gerbil.annotators.AnnotatorConfiguration;
import org.aksw.gerbil.web.config.spring.AndTypeFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;

@Configuration
@ComponentScan(basePackages = "org.aksw.gerbil.annotators")
public class AnnotatorConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotatorConfig.class);

    // @Bean
    // public AdapterList<AnnotatorConfiguration>
    // annotatorList(List<AnnotatorConfiguration> configurations) {
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
    // return new AdapterList<AnnotatorConfiguration>(configurations);
    // }

    @SuppressWarnings("unchecked")
    @Bean
    public AdapterList<AnnotatorConfiguration> annotatorList(ApplicationContext context) throws ClassNotFoundException {
        System.out.println("TEST_______________________________________________");
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        // scanner.addIncludeFilter(new
        // AnnotationTypeFilter(GerbilAnnotator.class));
        // scanner.addIncludeFilter(new
        // AssignableTypeFilter(TopicSystem.class));
        scanner.addIncludeFilter(new AndTypeFilter(new AnnotationTypeFilter(GerbilAnnotator.class),
                new AssignableTypeFilter(TopicSystem.class)));

        List<AnnotatorConfiguration> configs = new ArrayList<AnnotatorConfiguration>();
        GerbilAnnotator annotation;
        Class<?> clazz;
        Set<BeanDefinition> definitions = scanner.findCandidateComponents("org.aksw.gerbil.annotators");
        for (BeanDefinition beanDefinition : definitions) {
            clazz = this.getClass().getClassLoader().loadClass(beanDefinition.getBeanClassName());
            annotation = clazz.getAnnotation(GerbilAnnotator.class);
            configs.add(new GerbilAnnotatorMetaData(annotation, context, (Class<? extends TopicSystem>) clazz));
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Found ");
        builder.append(configs.size());
        builder.append(" annotators [");
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
        LOGGER.error(builder.toString());
        return new AdapterList<AnnotatorConfiguration>(configs);
    }
}
