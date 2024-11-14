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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.aksw.gerbil.dataset.AdapterConfigSerializer;
import org.aksw.gerbil.datatypes.AbstractAdapterConfiguration;
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
                adapters = nameToAdapterMapping.get(config.getName());
            } else {
                adapters = new ArrayList<T>(2);
                nameToAdapterMapping.put(config.getName(), adapters);
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

    public List<String> getAdapterDetailsForExperiment(ExperimentType type) {
        List<T> configs = getAdaptersForExperiment(type);
        List<String> serializedConfigs = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(AbstractAdapterConfiguration.class, new AdapterConfigSerializer(AbstractAdapterConfiguration.class));
        mapper.registerModule(module);
        for (T config : configs) {
            String json = null;
            try {
                json = mapper.writeValueAsString(config);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            serializedConfigs.add(json);
        }
        return serializedConfigs;
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
