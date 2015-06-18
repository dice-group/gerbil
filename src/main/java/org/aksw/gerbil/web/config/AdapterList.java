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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.gerbil.datatypes.AdapterConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;

public class AdapterList<T extends AdapterConfiguration> {

    protected List<T> experimentTypesToAdapterMapping[];
    protected Map<String, List<T>> nameToAdapterMapping;
    protected List<T> configurations;

    public AdapterList(List<T> configurations) {
        setConfigurations(configurations);
    }

    @SuppressWarnings("unchecked")
    protected void setConfigurations(List<T> configurations) {
        this.configurations = configurations;
        // udpate mappings
        nameToAdapterMapping = new HashMap<String, List<T>>();
        List<T> adapters;
        for (T config : configurations) {
            if (nameToAdapterMapping.containsKey(config.getName())) {
                // LOGGER.error("Found two adapters with the name \"" +
                // config.getName() + "\". Ignoring the second one.");
                adapters = new ArrayList<T>(2);
                nameToAdapterMapping.put(config.getName(), adapters);
            } else {
                // nameToAdapterMapping.put(config.getName(), config);
                adapters = nameToAdapterMapping.get(config.getName());
            }
            adapters.add(config);
            Collections.sort(adapters);
        }

        ExperimentType types[] = ExperimentType.values();
        experimentTypesToAdapterMapping = new List[types.length];
        int id;
        for (int i = 0; i < types.length; ++i) {
            experimentTypesToAdapterMapping[i] = new ArrayList<T>();
            for (String name : nameToAdapterMapping.keySet()) {
                adapters = nameToAdapterMapping.get(name);
                id = 0;
                while ((id < adapters.size()) && (!adapters.get(id).isApplicableForExperiment(types[i]))) {
                    ++id;
                }
                if (id < adapters.size()) {
                    experimentTypesToAdapterMapping[i].add(adapters.get(id));
                }
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

    public List<T> getAdaptersForName(String name) {
        if (nameToAdapterMapping.containsKey(name)) {
            return nameToAdapterMapping.get(name);
        } else {
            return null;
        }
    }

    public List<T> getConfigurations() {
        return configurations;
    }
}
