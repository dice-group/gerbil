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

import java.util.List;

import org.aksw.gerbil.annotator.impl.instance.InstanceListBasedAnnotator;
import org.aksw.gerbil.datatypes.AbstractAdapterConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.junit.Ignore;

@Ignore
public class TestAnnotatorConfiguration extends AbstractAdapterConfiguration implements AnnotatorConfiguration {

    private Annotator annotator;

    public TestAnnotatorConfiguration(List<Document> instances, ExperimentType applicableForExperiment) {
        this("Test-" + applicableForExperiment.name(), false, instances, applicableForExperiment);
    }

    public TestAnnotatorConfiguration(String annotatorName, boolean couldBeCached, List<Document> instances,
            ExperimentType applicableForExperiment) {
        super(annotatorName, couldBeCached, applicableForExperiment);
        annotator = new InstanceListBasedAnnotator(annotatorName, instances);
    }

    @Override
    public Annotator getAnnotator(ExperimentType experimentType) throws GerbilException {
        if (applicableForExperiment.equalsOrContainsType(experimentType)) {
            try {
                return loadAnnotator(experimentType);
            } catch (Exception e) {
                throw new GerbilException(e, ErrorTypes.ANNOTATOR_LOADING_ERROR);
            }
        }
        return null;
    }

    protected Annotator loadAnnotator(ExperimentType type) throws Exception {
        return annotator;
    }

}
