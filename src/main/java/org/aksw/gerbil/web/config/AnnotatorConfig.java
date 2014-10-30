package org.aksw.gerbil.web.config;

import java.util.List;

import org.aksw.gerbil.annotators.AnnotatorConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "org.aksw.gerbil.annotators")
public class AnnotatorConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotatorConfig.class);

    @Bean
    public AdapterList<AnnotatorConfiguration> annotatorList(List<AnnotatorConfiguration> configurations) {
        // if (LOGGER.isInfoEnabled()) {
        StringBuilder builder = new StringBuilder();
        builder.append("Found ");
        builder.append(configurations.size());
        builder.append(" annotators [");
        for (int i = 0; i < configurations.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(configurations.get(i).getClass().getSimpleName());
        }
        builder.append("].");
        LOGGER.error(builder.toString());
        // }
        return new AdapterList<AnnotatorConfiguration>(configurations);
    }
}
