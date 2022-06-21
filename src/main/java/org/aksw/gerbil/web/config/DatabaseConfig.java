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
package org.aksw.gerbil.web.config;

import org.aksw.gerbil.database.ExperimentDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * This {@link Configuration} creates the {@link ExperimentDAO} bean by loading
 * the XML config from the class path.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * @author Bernd Eickmann
 * 
 */
@Configuration
public class DatabaseConfig extends WebMvcConfigurerAdapter {

    private static final transient Logger LOGGER = LoggerFactory.getLogger(DatabaseConfig.class);

    /**
     * This {@link Configuration} creates the {@link ExperimentDAO} bean by loading
     * the XML config from the class path. After that, the bean is initialized using
     * the {@link ExperimentDAO#initialize()} method.
     * 
     * @return the database bean
     */
    @Bean
    public ExperimentDAO experimentDAO() {
        LOGGER.debug("Setting up database.");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "/spring/database/database-context.xml");
        try {
            ExperimentDAO database = context.getBean(ExperimentDAO.class);
            database.initialize();
            return database;
        } catch (org.springframework.beans.factory.BeanCreationException e) {
            // Check the source of the exception according to
            // https://github.com/dice-group/gerbil/issues/322#issuecomment-495609086
            Throwable t2 = e;
            Throwable t = t2.getCause();
            while ((t != null) && (t != t2) && !java.sql.SQLSyntaxErrorException.class.isInstance(t)) {
                t2 = t;
                t = t2.getCause();
            }
            if (java.sql.SQLSyntaxErrorException.class.isInstance(t)
                    && t.getMessage().contains("user lacks privilege or object not found")) {
                LOGGER.error(
                        "It seems like the existing database does not fit to this version of GERBIL. Please think about moving the database so that GERBIL can generate a fresh database.");
                throw new IllegalStateException(
                        "It seems like the existing database does not fit to this version of GERBIL. Please think about moving the database so that GERBIL can generate a fresh database.",
                        e);
            } else {
                throw new IllegalStateException("An unknown error occured while creating the database bean.", e);
            }
        } finally {
            context.close();
        }
    }
}
