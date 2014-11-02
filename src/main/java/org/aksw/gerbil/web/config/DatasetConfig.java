package org.aksw.gerbil.web.config;

import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.util.List;

import org.aksw.gerbil.datasets.DatasetConfiguration;
import org.aksw.gerbil.datasets.KnownNIFFileDatasetConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "org.aksw.gerbil.datasets")
public class DatasetConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetConfig.class);

    @Bean
    public AdapterList<DatasetConfiguration> annotatorList(List<DatasetConfiguration> configurations) {
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
        return new AdapterList<DatasetConfiguration>(configurations);
    }

    public AdapterList<DatasetConfiguration> knownNIFFileDatasetList(WikipediaApiInterface wikiApi,
            DBPediaApi dbpediaApi) {
        return KnownNIFFileDatasetConfigs.create(wikiApi, dbpediaApi);
    }
}
