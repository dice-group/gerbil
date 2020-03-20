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
package org.aksw.gerbil.dataset;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import org.aksw.gerbil.dataset.check.EntityCheckerManager;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;

public class DatasetConfigurationImpl extends AbstractDatasetConfiguration {

    protected Constructor<? extends Dataset> constructor;
    protected Object constructorArgs[];

    public DatasetConfigurationImpl(String datasetName, boolean couldBeCached,
                                    Constructor<? extends Dataset> constructor, Object[] constructorArgs,
                                    ExperimentType applicableForExperiment, EntityCheckerManager entityCheckerManager,
                                    SameAsRetriever globalRetriever) {
        super(datasetName, couldBeCached, applicableForExperiment, entityCheckerManager, globalRetriever);
        this.constructor = constructor;
        this.constructorArgs = constructorArgs;
    }

    protected Dataset loadDataset() throws Exception {
        Dataset instance = constructor.newInstance(constructorArgs);
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