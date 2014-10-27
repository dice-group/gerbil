package org.aksw.gerbil.web.config;

import org.aksw.gerbil.database.ExperimentDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan(basePackages = "org.aksw.gerbil.web")
public class DatabaseConfig extends WebMvcConfigurerAdapter {

    private static final transient Logger LOGGER = LoggerFactory.getLogger(DatabaseConfig.class);

    /**
     * @return the database bean
     */
    @Bean
    public ExperimentDAO experimentDAO() {
        LOGGER.debug("Setting up database.");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "/spring/database/database-context.xml");
        ExperimentDAO database = context.getBean(ExperimentDAO.class);
        database.initialize();
        context.close();
        return database;
    }
}
