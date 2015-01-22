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

import org.aksw.gerbil.database.ExperimentDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * This {@link Configuration} creates the {@link ExperimentDAO} bean by loading the XML config from the class path.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * @author Bernd Eickmann
 * 
 */
@Configuration
public class DatabaseConfig extends WebMvcConfigurerAdapter {

    private static final transient Logger LOGGER = LoggerFactory.getLogger(DatabaseConfig.class);

    /**
     * This {@link Configuration} creates the {@link ExperimentDAO} bean by loading the XML config from the class path.
     * After that, the bean is initialized using the {@link ExperimentDAO#initialize()} method.
     * 
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
