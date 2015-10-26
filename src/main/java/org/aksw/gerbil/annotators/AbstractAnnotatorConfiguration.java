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
package org.aksw.gerbil.annotators;


import it.unipi.di.acube.batframework.problems.TopicSystem;
import org.aksw.gerbil.datatypes.AbstractAdapterConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;

public abstract class AbstractAnnotatorConfiguration extends AbstractAdapterConfiguration implements
        AnnotatorConfiguration {

    public AbstractAnnotatorConfiguration(String annotatorName, boolean couldBeCached,
            ExperimentType... applicableForExperiment) {
        super(annotatorName, couldBeCached, applicableForExperiment);
    }

    @Override
    public TopicSystem getAnnotator(ExperimentType experimentType) throws GerbilException {
        for (int i = 0; i < applicableForExperiments.length; ++i) {
            if (applicableForExperiments[i].equalsOrContainsType(experimentType)) {
                try {
                    return loadAnnotator(experimentType);
                } catch (Exception e) {
                    throw new GerbilException(e, ErrorTypes.ANNOTATOR_LOADING_ERROR);
                }
            }
        }
        return null;
    }

    protected abstract TopicSystem loadAnnotator(ExperimentType type) throws Exception;

    public ExperimentType[] getApplicableForExperiments() {
        return applicableForExperiments;
    }

    public void setApplicableForExperiments(ExperimentType[] applicableForExperiments) {
        this.applicableForExperiments = applicableForExperiments;
    }
}
