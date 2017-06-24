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
package org.aksw.gerbil.annotator;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import org.aksw.gerbil.datatypes.AbstractAdapterConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;

/**
 * Contains all information needed to load an annotator for a specific
 * experiment type.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class AnnotatorConfigurationImpl extends AbstractAdapterConfiguration implements AnnotatorConfiguration {

    protected Constructor<? extends Annotator> constructor;
    protected Object constructorArgs[];

    public AnnotatorConfigurationImpl(String annotatorName, boolean couldBeCached,
            Constructor<? extends Annotator> constructor, Object constructorArgs[],
            ExperimentType applicableForExperiment) {
        super(annotatorName, couldBeCached, applicableForExperiment);
        this.constructor = constructor;
        this.constructorArgs = constructorArgs;
    }

    @Override
    public Annotator getAnnotator(ExperimentType experimentType) throws GerbilException {
        if (applicableForExperiment.equalsOrContainsType(experimentType)) {
            try {
                return loadAnnotator();
            } catch (GerbilException e) {
                throw e;
            } catch (Exception e) {
                throw new GerbilException(e, ErrorTypes.ANNOTATOR_LOADING_ERROR);
            }
        }
        return null;
    }

    protected Annotator loadAnnotator() throws Exception {
        Annotator instance = constructor.newInstance(constructorArgs);
        instance.setName(this.getName());
        return instance;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(\"");
        builder.append(name);
        builder.append("\",cached=");
        builder.append(couldBeCached);
        // builder.append(",expTypes={");
        // for (int i = 0; i < applicableForExperiments.length; ++i) {
        // if (i > 0) {
        // builder.append(',');
        // }
        // builder.append(applicableForExperiments[i].name());
        // }
        builder.append(",expType={");
        builder.append(applicableForExperiment.name());
        builder.append("},constr.=");
        builder.append(constructor);
        builder.append(",args=");
        builder.append(Arrays.toString(constructorArgs));
        builder.append(')');
        return builder.toString();
    }
}