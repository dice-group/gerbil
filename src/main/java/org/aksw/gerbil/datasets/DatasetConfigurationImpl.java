package org.aksw.gerbil.datasets;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.datatypes.AbstractAdapterConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;

public class DatasetConfigurationImpl extends AbstractAdapterConfiguration implements DatasetConfiguration {

    protected Constructor<? extends Dataset> constructor;
    protected Object constructorArgs[];

    public DatasetConfigurationImpl(String datasetName, boolean couldBeCached,
            Constructor<? extends Dataset> constructor, Object constructorArgs[], ExperimentType applicableForExperiment) {
        super(datasetName, couldBeCached, applicableForExperiment);
        this.constructor = constructor;
        this.constructorArgs = constructorArgs;
    }

    @Override
    public Dataset getDataset(ExperimentType experimentType) throws GerbilException {
        // for (int i = 0; i < applicableForExperiments.length; ++i) {
        // if (applicableForExperiments[i].equalsOrContainsType(experimentType))
        if (applicableForExperiment.equalsOrContainsType(experimentType)) {
            try {
                return loadDataset();
            } catch (Exception e) {
                throw new GerbilException(e, ErrorTypes.ANNOTATOR_LOADING_ERROR);
            }
        }
        return null;
    }

    protected Dataset loadDataset() throws Exception {
        Dataset instance = constructor.newInstance(constructorArgs);
        instance.setName(this.getName());
        // If this dataset should be initialized
        if (instance instanceof InitializableDataset) {
            ((InitializableDataset) instance).init();
        }
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