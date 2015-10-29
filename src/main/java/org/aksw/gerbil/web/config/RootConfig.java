/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
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

import java.io.File;
import java.io.IOException;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.execute.AnnotatorOutputWriter;
import org.aksw.gerbil.semantic.sameas.FileBasedCachingSameAsRetriever;
import org.aksw.gerbil.semantic.sameas.HTTPBasedSameAsRetriever;
import org.aksw.gerbil.semantic.sameas.InMemoryCachingSameAsRetriever;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.semantic.subclass.ClassHierarchyLoader;
import org.aksw.gerbil.semantic.subclass.SimpleSubClassInferencer;
import org.aksw.gerbil.semantic.subclass.SubClassInferencer;
import org.aksw.gerbil.utils.ConsoleLogger;
import org.aksw.simba.topicmodeling.concurrent.overseers.pool.DefeatableOverseer;
import org.aksw.simba.topicmodeling.concurrent.overseers.pool.ExecutorBasedOverseer;
import org.aksw.simba.topicmodeling.concurrent.reporter.LogReporter;
import org.aksw.simba.topicmodeling.concurrent.reporter.Reporter;
import org.apache.commons.configuration.ConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(RootConfig.class);

    private static final int DEFAULT_NUMBER_OF_WORKERS = 20;

    private static final String SAME_AS_CACHE_FILE_KEY = "org.aksw.gerbil.semantic.sameas.CachingSameAsRetriever.cacheFile";
    private static final String SAME_AS_IN_MEMORY_CACHE_SIZE_KEY = "org.aksw.gerbil.semantic.sameas.InMemoryCachingSameAsRetriever.cacheSize";

    private static final String ANNOTATOR_OUTPUT_WRITER_USAGE_KEY = "org.aksw.gerbil.execute.AnnotatorOutputWriter.printAnnotatorResults";
    private static final String ANNOTATOR_OUTPUT_WRITER_DIRECTORY_KEY = "org.aksw.gerbil.execute.AnnotatorOutputWriter.outputDirectory";

    // {
    // // FIXME this is an extremely ugly workaround to be able to log the
    // // stuff coming from the BAT-Framework
    // replaceSystemStreams();
    // }
    //
    // protected static void replaceSystemStreams() {
    // System.setOut(new PrintStream(new ConsoleLogger(false), true));
    // System.setErr(new PrintStream(new ConsoleLogger(true), true));
    // }

    static @Bean public PropertySourcesPlaceholderConfigurer myPropertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer p = new PropertySourcesPlaceholderConfigurer();
        Resource[] resourceLocations = new Resource[] { new ClassPathResource("gerbil.properties"), };
        p.setLocations(resourceLocations);
        return p;
    }

    public static @Bean DefeatableOverseer createOverseer() {
        DefeatableOverseer overseer = new ExecutorBasedOverseer(DEFAULT_NUMBER_OF_WORKERS);
        @SuppressWarnings("unused")
        Reporter reporter = new LogReporter(overseer);
        return overseer;
    }

    public static @Bean SubClassInferencer createSubClassInferencer() {
        Model classModel = ModelFactory.createDefaultModel();
        String hierarchyFiles[] = GerbilConfiguration.getInstance()
                .getStringArray("org.aksw.gerbil.semantic.subclass.SubClassInferencer.classHierarchyFiles");
        ClassHierarchyLoader loader = new ClassHierarchyLoader();
        for (int i = 0; i < hierarchyFiles.length; i += 3) {
            try {
                loader.loadClassHierarchy(new File(hierarchyFiles[i]), hierarchyFiles[i + 1], hierarchyFiles[i + 2],
                        classModel);
            } catch (IOException e) {
                LOGGER.error("Got an exception while trying to load the class hierarchy from the file \""
                        + hierarchyFiles[i] + "\" encoded with \"" + hierarchyFiles[i + 1] + "\" using the base URI \""
                        + hierarchyFiles[i + 2] + "\".", e);
            }
        }
        return new SimpleSubClassInferencer(classModel);
    }

    public static @Bean SameAsRetriever createSameAsRetriever() {
        SameAsRetriever sameAsRetriever = new HTTPBasedSameAsRetriever();
        SameAsRetriever decoratedRetriever = null;
        if (GerbilConfiguration.getInstance().containsKey(SAME_AS_CACHE_FILE_KEY)) {
            decoratedRetriever = FileBasedCachingSameAsRetriever.create(sameAsRetriever, true,
                    new File(GerbilConfiguration.getInstance().getString(SAME_AS_CACHE_FILE_KEY)));
        }
        if (decoratedRetriever == null) {
            LOGGER.warn("Couldn't create file based cache for sameAs retrieving.");
        } else {
            sameAsRetriever = decoratedRetriever;
            decoratedRetriever = null;
        }
        if (GerbilConfiguration.getInstance().containsKey(SAME_AS_IN_MEMORY_CACHE_SIZE_KEY)) {
            try {
                int cacheSize = GerbilConfiguration.getInstance().getInt(SAME_AS_IN_MEMORY_CACHE_SIZE_KEY);
                decoratedRetriever = new InMemoryCachingSameAsRetriever(sameAsRetriever, cacheSize);
            } catch (ConversionException e) {
                LOGGER.warn("Exception while trying to load parameter \"" + SAME_AS_IN_MEMORY_CACHE_SIZE_KEY + "\".",
                        e);
            }
        }
        if (decoratedRetriever == null) {
            LOGGER.info("Using default cache size for sameAs link in memory cache.");
            sameAsRetriever = new InMemoryCachingSameAsRetriever(sameAsRetriever);
        } else {
            sameAsRetriever = decoratedRetriever;
            decoratedRetriever = null;
        }

        return sameAsRetriever;
    }

    public static @Bean EvaluatorFactory createEvaluatorFactory(SubClassInferencer inferencer,
            SameAsRetriever sameAsRetriever) {
        return new EvaluatorFactory(sameAsRetriever, null, inferencer);
    }

    public static AnnotatorOutputWriter getAnnotatorOutputWriter() {
        if (GerbilConfiguration.getInstance().containsKey(ANNOTATOR_OUTPUT_WRITER_USAGE_KEY)
                && GerbilConfiguration.getInstance().getBoolean(ANNOTATOR_OUTPUT_WRITER_USAGE_KEY)
                && GerbilConfiguration.getInstance().containsKey(ANNOTATOR_OUTPUT_WRITER_DIRECTORY_KEY)) {
            return new AnnotatorOutputWriter(
                    GerbilConfiguration.getInstance().getString(ANNOTATOR_OUTPUT_WRITER_DIRECTORY_KEY));
        } else {
            return null;
        }
    }

}
