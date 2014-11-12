package org.aksw.gerbil.web.config;

import it.acubelab.batframework.problems.TopicSystem;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.annotations.GerbilAnnotator;
import org.aksw.gerbil.annotators.AnnotatorConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
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
    public AdapterList<AnnotatorConfiguration> annotatorList() throws ClassNotFoundException {
        System.out.println("TEST_______________________________________________");
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(GerbilAnnotator.class));
        scanner.addIncludeFilter(new AssignableTypeFilter(TopicSystem.class));

        List<AnnotatorConfiguration> configs = new ArrayList<AnnotatorConfiguration>();
        GerbilAnnotator annotation;
        Class<?> clazz;
        for (BeanDefinition bd : scanner.findCandidateComponents("org.aksw.gerbil.annotators")) {
            clazz = this.getClass().getClassLoader().loadClass(bd.getBeanClassName());
            annotation = clazz.getAnnotation(GerbilAnnotator.class);
            configs.add(new GerbilAnnotatorMetaData(annotation, (Class<? extends TopicSystem>) clazz));
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
