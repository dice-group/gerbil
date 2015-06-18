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
import org.aksw.gerbil.datasets.DatahubNIFConfig;
import org.aksw.gerbil.datasets.DatasetConfiguration;
import org.aksw.gerbil.datasets.DatasetConfigurationImpl;
import org.aksw.gerbil.datasets.datahub.DatahubNIFLoader;
import org.aksw.gerbil.datatypes.ExperimentType;
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

    public static void main(String[] args) {
        datasets();
    }

    @Bean
    public static AdapterList<DatasetConfiguration> datasets() {
        List<DatasetConfiguration> datasetConfigurations = new ArrayList<DatasetConfiguration>();
        Set<String> datasetKeys = getDatasetKeys();
        DatasetConfiguration configuration;
        for (String datasetKey : datasetKeys) {
            try {
                configuration = getConfiguration(datasetKey);
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
            datasetConfigurations.add(new DatahubNIFConfig(datasetName, datasets.get(datasetName), true));
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

    private static DatasetConfiguration getConfiguration(String datasetKey) throws ClassNotFoundException,
            NoSuchMethodException, SecurityException {
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

        return new DatasetConfigurationImpl(name, cacheable, constructor, constructorArgs, type);
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
