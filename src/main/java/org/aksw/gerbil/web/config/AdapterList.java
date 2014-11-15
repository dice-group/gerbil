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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.gerbil.datatypes.AdapterConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdapterList<T extends AdapterConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdapterList.class);

    protected List<T> experimentTypesToAdapterMapping[];
    protected Map<String, T> nameToAdapterMapping;
    protected List<T> configurations;

    public AdapterList(List<T> configurations) {
        setConfigurations(configurations);
    }

    @SuppressWarnings("unchecked")
    protected void setConfigurations(List<T> configurations) {
        this.configurations = configurations;
        // udpate mappings
        ExperimentType types[] = ExperimentType.values();
        experimentTypesToAdapterMapping = new List[types.length];
        for (int i = 0; i < types.length; i++) {
            experimentTypesToAdapterMapping[i] = new ArrayList<T>();
            for (T config : configurations) {
                if (config.isApplicableForExperiment(types[i])) {
                    experimentTypesToAdapterMapping[i].add(config);
                }
            }
        }

        nameToAdapterMapping = new HashMap<String, T>();
        for (T config : configurations) {
            if (nameToAdapterMapping.containsKey(config.getName())) {
                LOGGER.error("Found two adapters with the name \"" + config.getName() + "\". Ignoring the second one.");
            } else {
                nameToAdapterMapping.put(config.getName(), config);
            }
        }
    }

    public List<T> getAdaptersForExperiment(ExperimentType type) {
        return experimentTypesToAdapterMapping[type.ordinal()];
    }

    public Set<String> getAdapterNamesForExperiment(ExperimentType type) {
        List<T> configs = getAdaptersForExperiment(type);
        Set<String> names = new HashSet<String>(configs.size());
        for (T config : configs) {
            names.add(config.getName());
        }
        return names;
    }

    public T getAdapterForName(String name) {
        if (nameToAdapterMapping.containsKey(name)) {
            return nameToAdapterMapping.get(name);
        } else {
            return null;
        }
    }
}
