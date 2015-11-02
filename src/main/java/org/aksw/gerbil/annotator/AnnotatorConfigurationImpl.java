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