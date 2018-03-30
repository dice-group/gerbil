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
package org.aksw.gerbil.semantic.subclass;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.web.config.RootConfig;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;

public class SimpleSubClassInferencerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSubClassInferencerFactory.class);

    private static final String SUB_CLASS_INFERENCER_RULE_FILE_KEY = "org.aksw.gerbil.semantic.subclass.SubClassInferencer.ruleResource";

    public static SimpleSubClassInferencer createInferencer(Model classModel) {
        String resourceName = GerbilConfiguration.getInstance().getString(SUB_CLASS_INFERENCER_RULE_FILE_KEY);
        if (resourceName == null) {
            LOGGER.error("Couldn't load subclass inferencer rules resource name from properties. Returning null.");
            return null;
        }
        InputStream is = RootConfig.class.getClassLoader().getResourceAsStream(resourceName);
        List<String> lines;
        try {
            lines = IOUtils.readLines(is);
        } catch (IOException e) {
            LOGGER.error("Couldn't load subclass inferencer rules from resource \"" + resourceName
                    + "\". Returning null.", e);
            return null;
        }
        IOUtils.closeQuietly(is);
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line);
        }

        Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(sb.toString()));
        InfModel inf = ModelFactory.createInfModel(reasoner, classModel);
        SimpleSubClassInferencer inferencer = new SimpleSubClassInferencer(inf);
        return inferencer;
    }
}
