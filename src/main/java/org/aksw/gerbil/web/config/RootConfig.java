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

import java.io.PrintStream;

import org.aksw.gerbil.utils.ConsoleLogger;
import org.aksw.simba.topicmodeling.concurrent.overseers.Overseer;
import org.aksw.simba.topicmodeling.concurrent.overseers.pool.ExecutorBasedOverseer;
import org.aksw.simba.topicmodeling.concurrent.reporter.LogReporter;
import org.aksw.simba.topicmodeling.concurrent.reporter.Reporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * This is the root {@link Configuration} class that is processed by the Spring
 * framework and performs the following configurations:
 * <ul>
 * <li>Loads the properties file \"gerbil.properties\"</li>
 * <li>Starts a component scan inside the package
 * <code>org.aksw.gerbil.web.config</code> searching for other
 * {@link Configuration}s</li>
 * <li>Replaces the streams used by <code>System.out</code> and
 * <code>System.err</code> by two {@link ConsoleLogger} objects. (This is a very
 * ugly workaround that should be fixed in the near future)</li>
 * </ul>
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * @author Lars Wesemann
 * @author Didier Cherix
 * 
 */
@SuppressWarnings("deprecation")
@Configuration
@ComponentScan(basePackages = "org.aksw.gerbil.web.config")
@PropertySource("gerbil.properties")
public class RootConfig {

    private static final int DEFAULT_NUMBER_OF_WORKERS = 20;

    {
        // FIXME this is an extremely ugly workaround to be able to log the
        // stuff coming from the BAT-Framework
        replaceSystemStreams();
    }

    protected static void replaceSystemStreams() {
        System.setOut(new PrintStream(new ConsoleLogger(false), true));
        System.setErr(new PrintStream(new ConsoleLogger(true), true));
    }

    static @Bean
    public PropertySourcesPlaceholderConfigurer myPropertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer p = new PropertySourcesPlaceholderConfigurer();
        Resource[] resourceLocations = new Resource[] { new ClassPathResource("gerbil.properties"), };
        p.setLocations(resourceLocations);
        return p;
    }

    public static @Bean
    Overseer createOverseer() {
        Overseer overseer = new ExecutorBasedOverseer(DEFAULT_NUMBER_OF_WORKERS);
        @SuppressWarnings("unused")
        Reporter reporter = new LogReporter(overseer);
        return overseer;
    }

    // public static @Bean
    // SubClassInferencer<ClassifiedClassNode> createSubClassInferencer() {
    // return SimpleSubClassInferencerFactory.createInferencer();
    // FIXME
    // }
}
