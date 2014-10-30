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
    public AnnotatorList annotatorList(List<AnnotatorConfiguration> configurations) {
        LOGGER.error("Found " + configurations.size() + " annotators.");
        return new AnnotatorList(configurations);
    }
}
