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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.dataset.SingletonDatasetConfigImpl;
import org.aksw.gerbil.dataset.check.EntityCheckerManager;
import org.aksw.gerbil.dataset.datahub.DatahubNIFConfig;
import org.aksw.gerbil.dataset.datahub.DatahubNIFLoader;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.web.config.check.Checker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatasetsConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetsConfig.class);

    public static final String ANNOTATOR_CONFIGURATION_PREFIX = "org.aksw.gerbil.datasets.definition";
    public static final String ANNOTATOR_CACHE_FLAG_SUFFIX = "cacheable";
    public static final String ANNOTATOR_CLASS_SUFFIX = "class";
    public static final String ANNOTATOR_CONSTRUCTOR_ARGS_SUFFIX = "constructorArgs";
    public static final String ANNOTATOR_EXPERIMENT_TYPE_SUFFIX = "experimentType";
    public static final String ANNOTATOR_NAME_SUFFIX = "name";

    public static final String ANNOTATOR_CHECK_CLASS_SUFFIX = "check.class";
    public static final String ANNOTATOR_CHECK_ARGS_SUFFIX = "check.args";

    @Bean
    public static AdapterList<DatasetConfiguration> datasets(EntityCheckerManager entityCheckerManager,
            SameAsRetriever globalRetriever) {
        List<DatasetConfiguration> datasetConfigurations = new ArrayList<DatasetConfiguration>();
        Set<String> datasetKeys = getDatasetKeys();
        DatasetConfiguration configuration;
        for (String datasetKey : datasetKeys) {
            try {
                configuration = getConfiguration(datasetKey, entityCheckerManager, globalRetriever);
                if (configuration != null) {
                    datasetConfigurations.add(configuration);
                    LOGGER.info("Found dataset configuration " + configuration.toString());
                }
            } catch (Exception e) {
                LOGGER.error("Got an exception while trying to load configuration of \"" + datasetKey + "\" dataset: "
                        + e.toString());
            }
        }

        // load Datahub data
        DatahubNIFLoader datahub = new DatahubNIFLoader();
        Map<String, String> datasets = datahub.getDataSets();
        for (String datasetName : datasets.keySet()) {
            datasetConfigurations.add(new DatahubNIFConfig(datasetName, datasets.get(datasetName), true,
                    entityCheckerManager, globalRetriever));
        }

        LOGGER.info("Found {} datasets.", datasetConfigurations.size());
        return new AdapterList<DatasetConfiguration>(datasetConfigurations);
    }

    private static Set<String> getDatasetKeys() {
        Set<String> datasetKeys = new HashSet<String>();
        @SuppressWarnings("rawtypes")
        Iterator iterator = GerbilConfiguration.getInstance().getKeys(ANNOTATOR_CONFIGURATION_PREFIX);
        String datasetKey;
        int pos;
        while (iterator.hasNext()) {
            datasetKey = (String) iterator.next();
            datasetKey = datasetKey.substring(ANNOTATOR_CONFIGURATION_PREFIX.length() + 1);
            pos = datasetKey.indexOf('.');
            if (pos > 0) {
                datasetKey = datasetKey.substring(0, pos);
                datasetKeys.add(datasetKey);
            }
        }
        return datasetKeys;
    }

    private static DatasetConfiguration getConfiguration(String datasetKey, EntityCheckerManager entityCheckerManager,
            SameAsRetriever globalRetriever) throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        org.apache.commons.configuration.Configuration config = GerbilConfiguration.getInstance();
        StringBuilder keyBuilder = new StringBuilder();
        String key;

        key = buildKey(keyBuilder, datasetKey, ANNOTATOR_NAME_SUFFIX);
        if (!config.containsKey(key)) {
            LOGGER.error("Couldn't get a name for the \"" + datasetKey + "\" dataset.");
            return null;
        }
        String name = config.getString(key);

        key = buildKey(keyBuilder, datasetKey, ANNOTATOR_CLASS_SUFFIX);
        if (!config.containsKey(key)) {
            LOGGER.error("Couldn't get a class for the \"" + datasetKey + "\" dataset.");
            return null;
        }
        String classString = config.getString(key);
        @SuppressWarnings("unchecked")
        Class<? extends Dataset> datasetClass = (Class<? extends Dataset>) DatasetsConfig.class.getClassLoader()
                .loadClass(classString);

        key = buildKey(keyBuilder, datasetKey, ANNOTATOR_EXPERIMENT_TYPE_SUFFIX);
        if (!config.containsKey(key)) {
            LOGGER.error("Couldn't get a class for the \"" + datasetKey + "\" dataset.");
            return null;
        }
        String typeString = config.getString(key);
        ExperimentType type = ExperimentType.valueOf(typeString);

        key = buildKey(keyBuilder, datasetKey, ANNOTATOR_CACHE_FLAG_SUFFIX);
        boolean cacheable = true;
        if (config.containsKey(key)) {
            cacheable = config.getBoolean(key);
        }

        key = buildKey(keyBuilder, datasetKey, ANNOTATOR_CONSTRUCTOR_ARGS_SUFFIX);
        String constructorArgStrings[];
        if (config.containsKey(key)) {
            constructorArgStrings = config.getStringArray(key);
        } else {
            constructorArgStrings = new String[0];
        }
        Object constructorArgs[] = new Object[constructorArgStrings.length];
        Class<?> constructorArgClasses[] = new Class[constructorArgStrings.length];
        for (int i = 0; i < constructorArgs.length; ++i) {
            constructorArgs[i] = constructorArgStrings[i];
            constructorArgClasses[i] = String.class;
        }

        Constructor<? extends Dataset> constructor = datasetClass.getConstructor(constructorArgClasses);

        // If a checker class has been defined
        key = buildKey(keyBuilder, datasetKey, ANNOTATOR_CHECK_CLASS_SUFFIX);
        if (config.containsKey(key)) {
            String checkerClassName = config.getString(key);
            // If checker arguments have been defined
            key = buildKey(keyBuilder, datasetKey, ANNOTATOR_CHECK_ARGS_SUFFIX);
            String checkerArgStrings[];
            if (config.containsKey(key)) {
                checkerArgStrings = config.getStringArray(key);
            } else {
                checkerArgStrings = new String[0];
            }
            Object checkerArgs[] = new Object[checkerArgStrings.length];
            for (int i = 0; i < checkerArgs.length; ++i) {
                checkerArgs[i] = checkerArgStrings[i];
            }
            try {
                @SuppressWarnings("unchecked")
                Class<? extends Checker> checkerClass = (Class<? extends Checker>) DatasetsConfig.class.getClassLoader()
                        .loadClass(checkerClassName);
                Checker checker = checkerClass.newInstance();
                if (!checker.check(checkerArgs)) {
                    LOGGER.info("Check for dataset \"{}\" failed. It won't be available.", name);
                    return null;
                }
            } catch (Exception e) {
                LOGGER.error("Error while trying to run check for dataset \"" + name + "\". Returning null.", e);
            }
        }

        // return new DatasetConfigurationImpl(name, cacheable, constructor,
        // constructorArgs, type, entityCheckerManager);
        return new SingletonDatasetConfigImpl(name, cacheable, constructor, constructorArgs, type, entityCheckerManager,
                globalRetriever);
    }

    protected static String buildKey(StringBuilder keyBuilder, String annotatorKey, String suffix) {
        keyBuilder.append(ANNOTATOR_CONFIGURATION_PREFIX);
        keyBuilder.append('.');
        keyBuilder.append(annotatorKey);
        keyBuilder.append('.');
        keyBuilder.append(suffix);
        String key = keyBuilder.toString();
        keyBuilder.delete(0, keyBuilder.length());
        return key;
    }
}
