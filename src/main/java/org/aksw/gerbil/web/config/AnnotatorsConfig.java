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
import java.util.Set;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.AnnotatorConfiguration;
import org.aksw.gerbil.annotator.AnnotatorConfigurationImpl;
import org.aksw.gerbil.annotator.SingletonAnnotatorConfigImpl;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.web.config.check.Checker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnnotatorsConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotatorsConfig.class);

    public static final String ANNOTATOR_CONFIGURATION_PREFIX = "org.aksw.gerbil.annotators.definition";
    public static final String ANNOTATOR_CACHE_FLAG_SUFFIX = "cacheable";
    public static final String ANNOTATOR_CLASS_SUFFIX = "class";
    public static final String ANNOTATOR_CONSTRUCTOR_ARGS_SUFFIX = "constructorArgs";
    public static final String ANNOTATOR_EXPERIMENT_TYPE_SUFFIX = "experimentType";
    public static final String ANNOTATOR_NAME_SUFFIX = "name";
    public static final String ANNOTATOR_SINGLETON_FLAG_SUFFIX = "singleton";

    public static final String ANNOTATOR_CHECK_CLASS_SUFFIX = "check.class";
    public static final String ANNOTATOR_CHECK_ARGS_SUFFIX = "check.args";

    public static void main(String[] args) {
        annotators();
    }

    @Bean
    public static AdapterList<AnnotatorConfiguration> annotators() {
        List<AnnotatorConfiguration> annotatorConfigurations = new ArrayList<AnnotatorConfiguration>();
        Set<String> annotatorKeys = getAnnotatorKeys();
        AnnotatorConfiguration configuration;
        for (String annotatorKey : annotatorKeys) {
            try {
                configuration = getConfiguration(annotatorKey);
                if (configuration != null) {
                    annotatorConfigurations.add(configuration);
                    LOGGER.info("Found annotator configuration " + configuration.toString());
                }
            } catch (Exception e) {
                LOGGER.error("Got an exception while trying to load configuration of \"" + annotatorKey
                        + "\" annotator: " + e.toString());
            }
        }
        LOGGER.info("Found {} annotators.", annotatorConfigurations.size());
        return new AdapterList<AnnotatorConfiguration>(annotatorConfigurations);
    }

    private static Set<String> getAnnotatorKeys() {
        Set<String> annotatorKeys = new HashSet<String>();
        @SuppressWarnings("rawtypes")
        Iterator iterator = GerbilConfiguration.getInstance().getKeys(ANNOTATOR_CONFIGURATION_PREFIX);
        String annotatorKey;
        int pos;
        while (iterator.hasNext()) {
            annotatorKey = (String) iterator.next();
            annotatorKey = annotatorKey.substring(ANNOTATOR_CONFIGURATION_PREFIX.length() + 1);
            pos = annotatorKey.indexOf('.');
            if (pos > 0) {
                annotatorKey = annotatorKey.substring(0, pos);
                annotatorKeys.add(annotatorKey);
            }
        }
        return annotatorKeys;
    }

    private static AnnotatorConfiguration getConfiguration(String annotatorKey)
            throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        org.apache.commons.configuration.Configuration config = GerbilConfiguration.getInstance();
        StringBuilder keyBuilder = new StringBuilder();
        String key;

        key = buildKey(keyBuilder, annotatorKey, ANNOTATOR_NAME_SUFFIX);
        if (!config.containsKey(key)) {
            LOGGER.error("Couldn't get a name for the \"" + annotatorKey + "\" annotator.");
            return null;
        }
        String name = config.getString(key);

        key = buildKey(keyBuilder, annotatorKey, ANNOTATOR_CLASS_SUFFIX);
        if (!config.containsKey(key)) {
            LOGGER.error("Couldn't get a class for the \"" + annotatorKey + "\" annotator.");
            return null;
        }
        String classString = config.getString(key);
        @SuppressWarnings("unchecked")
        Class<? extends Annotator> annotatorClass = (Class<? extends Annotator>) AnnotatorsConfig.class.getClassLoader()
                .loadClass(classString);

        key = buildKey(keyBuilder, annotatorKey, ANNOTATOR_EXPERIMENT_TYPE_SUFFIX);
        if (!config.containsKey(key)) {
            LOGGER.error("Couldn't get an experiment type for the \"" + annotatorKey + "\" annotator.");
            return null;
        }
        String typeString = config.getString(key);
        ExperimentType type = ExperimentType.valueOf(typeString);

        key = buildKey(keyBuilder, annotatorKey, ANNOTATOR_CACHE_FLAG_SUFFIX);
        boolean cacheable = true;
        if (config.containsKey(key)) {
            cacheable = config.getBoolean(key);
        }

        key = buildKey(keyBuilder, annotatorKey, ANNOTATOR_SINGLETON_FLAG_SUFFIX);
        boolean isSingleton = config.containsKey(key) && config.getBoolean(key);

        key = buildKey(keyBuilder, annotatorKey, ANNOTATOR_CONSTRUCTOR_ARGS_SUFFIX);
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
        Constructor<? extends Annotator> constructor = null;
        try {
            constructor = annotatorClass.getConstructor(constructorArgClasses);
        } catch (NoClassDefFoundError e) {
            throw new IllegalArgumentException(
                    "The annotator configuriation tries to use a class (directly or indirectly) that is not existing ("
                            + e.getLocalizedMessage() + "). The annotator won't be available.",
                    e);
        }

        // If a checker class has been defined
        key = buildKey(keyBuilder, annotatorKey, ANNOTATOR_CHECK_CLASS_SUFFIX);
        if (config.containsKey(key)) {
            String checkerClassName = config.getString(key);
            // If checker arguments have been defined
            key = buildKey(keyBuilder, annotatorKey, ANNOTATOR_CHECK_ARGS_SUFFIX);
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
                Class<? extends Checker> checkerClass = (Class<? extends Checker>) AnnotatorsConfig.class
                        .getClassLoader().loadClass(checkerClassName);
                Checker checker = checkerClass.newInstance();
                if (!checker.check(checkerArgs)) {
                    LOGGER.info("Check for annotator \"{}\" failed. It won't be available.", name);
                    return null;
                }
            } catch (Exception e) {
                LOGGER.error("Error while trying to run check for annotator \"" + name + "\". Returning null.", e);
            }
        }

        if (isSingleton) {
            return new SingletonAnnotatorConfigImpl(name, cacheable, constructor, constructorArgs, type);
        } else {
            return new AnnotatorConfigurationImpl(name, cacheable, constructor, constructorArgs, type);
        }
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
