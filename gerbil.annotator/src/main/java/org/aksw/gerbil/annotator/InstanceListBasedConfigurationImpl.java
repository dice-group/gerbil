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

import org.aksw.gerbil.annotator.impl.instance.InstanceListBasedAnnotator;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.datatypes.AbstractAdapterConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.apache.jena.riot.RiotException;

/**
 * Contains all information needed to load an annotator for a specific
 * experiment type.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class InstanceListBasedConfigurationImpl extends AbstractAdapterConfiguration implements AnnotatorConfiguration {

    protected DatasetConfiguration datasetConfig;
    
    private File2SystemEntry fileMapping;

    public InstanceListBasedConfigurationImpl(String annotatorName, boolean couldBeCached,
            DatasetConfiguration datasetConfig, ExperimentType applicableForExperiment) {
        super(annotatorName, couldBeCached, applicableForExperiment);
        this.datasetConfig = datasetConfig;
    }

    @Override
    public Annotator getAnnotator(ExperimentType experimentType) throws GerbilException {
        if (applicableForExperiment.equalsOrContainsType(experimentType)) {
            try {
                return loadAnnotator(experimentType);
            } catch (GerbilException e) {
                // If the error comes from the dataset, make sure that it can be
                // seen that we try to create an annotator
                if ((e.getErrorType() == ErrorTypes.DATASET_LOADING_ERROR)
                        || (e.getErrorType() == ErrorTypes.DATASET_LOADING_ERROR)) {
                    throw new GerbilException(e, ErrorTypes.ANNOTATOR_LOADING_ERROR);
                } else {
                    throw e;
                }
            } catch(RiotException e) {
            	throw new GerbilException(e, ErrorTypes.RDF_IS_NOT_VALID);
            } catch (Exception e) {

                throw new GerbilException(e, ErrorTypes.ANNOTATOR_LOADING_ERROR);
            }
        }
        return null;
    }

    protected Annotator loadAnnotator(ExperimentType experimentType) throws Exception {
        Dataset dataset = datasetConfig.getDataset(experimentType);
        if (dataset == null) {
            return null;
        }
        Annotator system =  new InstanceListBasedAnnotator(getName(), dataset.getInstances());
        system.setFileMapping(fileMapping);
        return system;
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
        builder.append("},dataset=");
        builder.append(datasetConfig.toString());
        builder.append(')');
        return builder.toString();
    }

	@Override
	public void setFileMapping(File2SystemEntry entry) {
		this.fileMapping=entry;
	}

}