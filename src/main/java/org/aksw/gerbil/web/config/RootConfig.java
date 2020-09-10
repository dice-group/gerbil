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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.dataset.check.EntityCheckerManager;
import org.aksw.gerbil.dataset.check.impl.EntityCheckerManagerImpl;
import org.aksw.gerbil.dataset.check.impl.FileBasedCachingEntityCheckerManager;
import org.aksw.gerbil.dataset.check.impl.HttpBasedEntityChecker;
import org.aksw.gerbil.dataset.check.impl.InMemoryCachingEntityCheckerManager;
import org.aksw.gerbil.dataset.check.index.IndexBasedEntityChecker;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.execute.AnnotatorOutputWriter;
import org.aksw.gerbil.semantic.kb.SimpleWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.semantic.sameas.SingleUriSameAsRetriever;
import org.aksw.gerbil.semantic.sameas.impl.CrawlingSameAsRetrieverDecorator;
import org.aksw.gerbil.semantic.sameas.impl.DomainBasedSameAsRetrieverManager;
import org.aksw.gerbil.semantic.sameas.impl.ErrorFixingSameAsRetriever;
import org.aksw.gerbil.semantic.sameas.impl.UriEncodingHandlingSameAsRetriever;
import org.aksw.gerbil.semantic.sameas.impl.UriFilteringSameAsRetrieverDecorator;
import org.aksw.gerbil.semantic.sameas.impl.cache.FileBasedCachingSameAsRetriever;
import org.aksw.gerbil.semantic.sameas.impl.cache.InMemoryCachingSameAsRetriever;
import org.aksw.gerbil.semantic.sameas.impl.http.HTTPBasedSameAsRetriever;
import org.aksw.gerbil.semantic.sameas.impl.index.IndexBasedSameAsRetriever;
import org.aksw.gerbil.semantic.sameas.impl.wiki.WikiDbPediaBridgingSameAsRetriever;
import org.aksw.gerbil.semantic.sameas.impl.wiki.WikipediaApiBasedSingleUriSameAsRetriever;
import org.aksw.gerbil.semantic.subclass.ClassHierarchyLoader;
import org.aksw.gerbil.semantic.subclass.SimpleSubClassInferencer;
import org.aksw.gerbil.semantic.subclass.SubClassInferencer;
import org.aksw.gerbil.utils.ConsoleLogger;
import org.aksw.simba.topicmodeling.concurrent.overseers.pool.DefeatableOverseer;
import org.aksw.simba.topicmodeling.concurrent.overseers.pool.ExecutorBasedOverseer;
import org.aksw.simba.topicmodeling.concurrent.reporter.LogReporter;
import org.aksw.simba.topicmodeling.concurrent.reporter.Reporter;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import javax.annotation.PostConstruct;

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
@org.springframework.context.annotation.Configuration
@ComponentScan(basePackages = "org.aksw.gerbil.web.config")
@PropertySource("gerbil.properties")
public class RootConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RootConfig.class);

    private static final int DEFAULT_NUMBER_OF_WORKERS = 20;

    private static final String NUMBER_OF_WORKERS_KEY = "org.aksw.gerbil.web.config.overseerWorkers";

    private static final String SAME_AS_CACHE_FILE_KEY = "org.aksw.gerbil.semantic.sameas.CachingSameAsRetriever.cacheFile";
    private static final String SAME_AS_IN_MEMORY_CACHE_SIZE_KEY = "org.aksw.gerbil.semantic.sameas.InMemoryCachingSameAsRetriever.cacheSize";

    private static final String ANNOTATOR_OUTPUT_WRITER_USAGE_KEY = "org.aksw.gerbil.execute.AnnotatorOutputWriter.printAnnotatorResults";
    private static final String ANNOTATOR_OUTPUT_WRITER_DIRECTORY_KEY = "org.aksw.gerbil.execute.AnnotatorOutputWriter.outputDirectory";

    private static final String HTTP_SAME_AS_RETRIEVAL_DOMAIN_KEY = "org.aksw.gerbil.semantic.sameas.impl.http.HTTPBasedSameAsRetriever.domain";

    private static final String ENTITY_CHECKING_MANAGER_USE_PERSISTENT_CACHE_KEY = "org.aksw.gerbil.dataset.check.EntityCheckerManagerImpl.usePersistentCache";
    private static final String ENTITY_CHECKING_MANAGER_PERSISTENT_CACHE_FILE_NAME_KEY = "org.aksw.gerbil.dataset.check.FileBasedCachingEntityCheckerManager.cacheFile";
    private static final String ENTITY_CHECKING_MANAGER_PERSISTENT_CACHE_DURATION_KEY = "org.aksw.gerbil.dataset.check.FileBasedCachingEntityCheckerManager.cacheDuration";
    private static final String ENTITY_CHECKING_MANAGER_IN_MEM_CACHE_SIZE_KEY = "org.aksw.gerbil.dataset.check.InMemoryCachingEntityCheckerManager.cacheSize";
    private static final String ENTITY_CHECKING_MANAGER_IN_MEM_CACHE_DURATION_KEY = "org.aksw.gerbil.dataset.check.InMemoryCachingEntityCheckerManager.cacheDuration";
    private static final String HTTP_BASED_ENTITY_CHECKING_NAMESPACE_KEY = "org.aksw.gerbil.dataset.check.HttpBasedEntityChecker.namespace";
    private static final String INDEX_BASED_ENTITY_CHECKING_CONFIG_KEY_START = "org.aksw.gerbil.dataset.check.IndexBasedEntityChecker";
    private static final String WIKIPEDIA_BASED_SAME_AS_RETRIEVAL_DOMAIN_KEY = "org.aksw.gerbil.semantic.sameas.impl.wiki.WikipediaApiBasedSingleUriSameAsRetriever.domain";
    private static final String SAME_AS_RETRIEVAL_DOMAIN_BLACKLIST_KEY = "org.aksw.gerbil.semantic.sameas.impl.UriFilteringSameAsRetrieverDecorator.domainBlacklist";
    private static final String INDEXED_BASED_SAME_AS_RETRIEVER_FOLDER_KEY = "org.aksw.gerbil.semantic.sameas.impl.index.IndexBasedSameAsRetriever.folder";
    private static final String INDEXED_BASED_SAME_AS_RETRIEVER_DOMAIN_KEY = "org.aksw.gerbil.semantic.sameas.impl.index.IndexBasedSameAsRetriever.domain";

    private static final String AVAILABLE_EXPERIMENT_TYPES_KEY = "org.aksw.gerbil.web.MainController.availableExperimentTypes";

    private static final String DEFAULT_WELL_KNOWN_KBS_PARAMETER_KEY = "org.aksw.gerbil.evaluate.DefaultWellKnownKB";

    static @Bean public PropertySourcesPlaceholderConfigurer myPropertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer p = new PropertySourcesPlaceholderConfigurer();
        Resource[] resourceLocations = new Resource[] { new ClassPathResource("gerbil.properties"), };
        p.setLocations(resourceLocations);
        return p;
    }

    public static @Bean DefeatableOverseer createOverseer() {
        int numberOfWorkers = DEFAULT_NUMBER_OF_WORKERS;
        if (GerbilConfiguration.getInstance().containsKey(NUMBER_OF_WORKERS_KEY)) {
            try {
                numberOfWorkers = GerbilConfiguration.getInstance().getInt(NUMBER_OF_WORKERS_KEY);
            } catch (Exception e) {
                LOGGER.warn("Couldn't load number of workers from config. Using the default number.", e);
            }
        } else {
            LOGGER.warn("Couldn't load number of workers from config. Using the default number.");
        }
        DefeatableOverseer overseer = new ExecutorBasedOverseer(numberOfWorkers);
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
        DomainBasedSameAsRetrieverManager retrieverManager = new DomainBasedSameAsRetrieverManager();
        retrieverManager.addStaticRetriever(new ErrorFixingSameAsRetriever());
        retrieverManager.addStaticRetriever(new UriEncodingHandlingSameAsRetriever());

        // HTTP based same as retrieval
        HTTPBasedSameAsRetriever httpRetriever = null;
        if (GerbilConfiguration.getInstance().containsKey(HTTP_SAME_AS_RETRIEVAL_DOMAIN_KEY)) {
            httpRetriever = new HTTPBasedSameAsRetriever();
            for (String domain : GerbilConfiguration.getInstance().getStringArray(HTTP_SAME_AS_RETRIEVAL_DOMAIN_KEY)) {
                retrieverManager.addDomainSpecificRetriever(domain, httpRetriever);
            }
        }

        // If there is an index based same as retriever available
        if (GerbilConfiguration.getInstance().containsKey(INDEXED_BASED_SAME_AS_RETRIEVER_FOLDER_KEY)) {
            SameAsRetriever retriever;
            try {
                retriever = new IndexBasedSameAsRetriever(
                        GerbilConfiguration.getInstance().getString(INDEXED_BASED_SAME_AS_RETRIEVER_FOLDER_KEY));

            } catch (GerbilException e) {
                LOGGER.error("Could not load Index Retriever. using HTTPBasedSameAs Retriever instead");
                if (httpRetriever == null) {
                    retriever = new HTTPBasedSameAsRetriever();
                } else {
                    retriever = httpRetriever;
                }
            }
            for (String domain : GerbilConfiguration.getInstance()
                    .getStringArray(INDEXED_BASED_SAME_AS_RETRIEVER_DOMAIN_KEY)) {
                retrieverManager.addDomainSpecificRetriever(domain, retriever);
            }
        }
        // Wikipedia API based same as retrieval
        if (GerbilConfiguration.getInstance().containsKey(WIKIPEDIA_BASED_SAME_AS_RETRIEVAL_DOMAIN_KEY)) {
            SingleUriSameAsRetriever singleRetriever = new WikipediaApiBasedSingleUriSameAsRetriever();
            for (String domain : GerbilConfiguration.getInstance()
                    .getStringArray(WIKIPEDIA_BASED_SAME_AS_RETRIEVAL_DOMAIN_KEY)) {
                retrieverManager.addDomainSpecificRetriever(domain, singleRetriever);
            }
        }

        // Wikipedia to DBpedia URI translation
        (new WikiDbPediaBridgingSameAsRetriever()).addToManager(retrieverManager);

        // The manager is ready
        SameAsRetriever sameAsRetriever = retrieverManager;

        // same as retrieval domain blacklist
        if (GerbilConfiguration.getInstance().containsKey(SAME_AS_RETRIEVAL_DOMAIN_BLACKLIST_KEY)) {
            sameAsRetriever = new UriFilteringSameAsRetrieverDecorator(sameAsRetriever,
                    GerbilConfiguration.getInstance().getStringArray(SAME_AS_RETRIEVAL_DOMAIN_BLACKLIST_KEY));
        }

        // same as crawling
        sameAsRetriever = new CrawlingSameAsRetrieverDecorator(sameAsRetriever);

        SameAsRetriever decoratedRetriever = null;
        if (GerbilConfiguration.getInstance().containsKey(SAME_AS_CACHE_FILE_KEY)) {
            decoratedRetriever = FileBasedCachingSameAsRetriever.create(sameAsRetriever, false,
                    new File(GerbilConfiguration.getInstance().getString(SAME_AS_CACHE_FILE_KEY)));
        }

        if (decoratedRetriever == null) {
            LOGGER.warn("Couldn't create file based cache for sameAs retrieving. Trying to create in Memory cache.");
            if (GerbilConfiguration.getInstance().containsKey(SAME_AS_IN_MEMORY_CACHE_SIZE_KEY)) {
                try {
                    int cacheSize = GerbilConfiguration.getInstance().getInt(SAME_AS_IN_MEMORY_CACHE_SIZE_KEY);
                    decoratedRetriever = new InMemoryCachingSameAsRetriever(sameAsRetriever, cacheSize);
                } catch (ConversionException e) {
                    LOGGER.warn(
                            "Exception while trying to load parameter \"" + SAME_AS_IN_MEMORY_CACHE_SIZE_KEY + "\".",
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
        } else {
            sameAsRetriever = decoratedRetriever;
            decoratedRetriever = null;
        }

        return sameAsRetriever;
    }

    public static @Bean EvaluatorFactory createEvaluatorFactory(SubClassInferencer inferencer) {
        return new EvaluatorFactory(inferencer);
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




    @SuppressWarnings("unchecked")
    public static @Bean EntityCheckerManager getEntityCheckerManager() {
        EntityCheckerManager manager = null;
        Configuration config = GerbilConfiguration.getInstance();
        if (config.containsKey(ENTITY_CHECKING_MANAGER_USE_PERSISTENT_CACHE_KEY)
                && config.getBoolean(ENTITY_CHECKING_MANAGER_USE_PERSISTENT_CACHE_KEY)
                && config.containsKey(ENTITY_CHECKING_MANAGER_PERSISTENT_CACHE_DURATION_KEY)) {
            LOGGER.info("Using file based cache for entity checking.");
            try {
                long duration = config.getLong(ENTITY_CHECKING_MANAGER_PERSISTENT_CACHE_DURATION_KEY);
                String cacheFile = config.getString(ENTITY_CHECKING_MANAGER_PERSISTENT_CACHE_FILE_NAME_KEY);
                manager = FileBasedCachingEntityCheckerManager.create(duration, new File(cacheFile));
            } catch (ConversionException e) {
                LOGGER.error("Exception while parsing parameter.", e);
            }
        }
        if ((manager == null) && config.containsKey(ENTITY_CHECKING_MANAGER_IN_MEM_CACHE_SIZE_KEY)
                && config.containsKey(ENTITY_CHECKING_MANAGER_IN_MEM_CACHE_DURATION_KEY)) {
            LOGGER.info("Using in-memory based cache for entity checking.");
            try {
                int cacheSize = config.getInt(ENTITY_CHECKING_MANAGER_IN_MEM_CACHE_SIZE_KEY);
                long duration = config.getLong(ENTITY_CHECKING_MANAGER_IN_MEM_CACHE_DURATION_KEY);
                manager = new InMemoryCachingEntityCheckerManager(cacheSize, duration);
            } catch (Exception e) {
                LOGGER.error("Exception while parsing parameter. Creating default EntityCheckerManagerImpl.", e);
                manager = new EntityCheckerManagerImpl();
            }
        }
        if (manager == null) {
            manager = new EntityCheckerManagerImpl();
        }
        List<String> namespaces = config.getList(HTTP_BASED_ENTITY_CHECKING_NAMESPACE_KEY);
        if (!namespaces.isEmpty()) {
            for (String namespace : namespaces) {
                manager.registerEntityChecker(namespace.toString(), new HttpBasedEntityChecker(namespace.toString()));
            }
        }
        @SuppressWarnings("rawtypes")
        Iterator keyIterator = config.getKeys(INDEX_BASED_ENTITY_CHECKING_CONFIG_KEY_START);
        while (keyIterator.hasNext()) {
            String key = keyIterator.next().toString();
            namespaces = config.getList(key);
            if (!namespaces.isEmpty()) {
                // the first "namespace" is the directory of the index
                IndexBasedEntityChecker indexBasedChecker = IndexBasedEntityChecker.create(namespaces.get(0));
                if (indexBasedChecker != null) {
                    boolean first = true;
                    for (String namespace : namespaces) {
                        if (first) {
                            first = false;
                        } else {
                            manager.registerEntityChecker(namespace.toString(), indexBasedChecker);
                        }
                    }
                } else {
                    LOGGER.warn(
                            "Couldn't create index based entity checker for index \"{}\". Creating HTTP based checker.",
                            namespaces.get(0));
                    // use HTTP based checker
                    for (String namespace : namespaces) {
                        manager.registerEntityChecker(namespace.toString(),
                                new HttpBasedEntityChecker(namespace.toString()));
                    }
                }
            }
        }
        return manager;
    }

    public static ExperimentType[] getAvailableExperimentTypes() {
        Configuration config = GerbilConfiguration.getInstance();
        Set<ExperimentType> types = new HashSet<ExperimentType>();
        if (config.containsKey(AVAILABLE_EXPERIMENT_TYPES_KEY)) {
            String typeNames[] = config.getStringArray(AVAILABLE_EXPERIMENT_TYPES_KEY);
            ExperimentType type = null;
            for (int i = 0; i < typeNames.length; ++i) {
                try {
                    type = ExperimentType.valueOf(typeNames[i]);
                    types.add(type);
                } catch (IllegalArgumentException e) {
                    LOGGER.warn(
                            "Couldn't find the experiment type \"{}\" defined in the properties file. It will be ignored.",
                            typeNames[i]);
                }
            }
        }
        if (types.size() == 0) {
            LOGGER.error(
                    "Couldn't load the list of available experiment types. This GERBIL instance won't work as expected. Please define a list of experiment types using the {} key in the configuration file.",
                    AVAILABLE_EXPERIMENT_TYPES_KEY);
            return new ExperimentType[0];
        } else {
            ExperimentType typesArray[] = types.toArray(new ExperimentType[types.size()]);
            Arrays.sort(typesArray);
            return typesArray;
        }
    }

    public static UriKBClassifier createDefaultUriKBClassifier() {
        return new SimpleWhiteListBasedUriKBClassifier(loadDefaultKBs());
    }

    public static String[] loadDefaultKBs() {
        String kbs[] = GerbilConfiguration.getInstance().getStringArray(DEFAULT_WELL_KNOWN_KBS_PARAMETER_KEY);
        if (kbs == null) {
            LOGGER.error("Couldn't load the list of well known KBs. This GERBIL instance might not work as expected!");
        }
        return kbs;
    }

    public static int getNoOfWorkers() {
        int numberOfWorkers = DEFAULT_NUMBER_OF_WORKERS;
        if (GerbilConfiguration.getInstance().containsKey(NUMBER_OF_WORKERS_KEY)) {
            try {
                numberOfWorkers = GerbilConfiguration.getInstance().getInt(NUMBER_OF_WORKERS_KEY);
            } catch (Exception e) {
                // LOGGER.warn("Couldn't load number of workers from config.
                // Using the default number.", e);
            }
        } else {
            // LOGGER.warn("Couldn't load number of workers from config. Using
            // the default number.");
        }
        return numberOfWorkers;
    }

}
