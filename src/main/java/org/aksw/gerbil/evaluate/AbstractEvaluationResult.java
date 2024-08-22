package org.aksw.gerbil.evaluate;

import java.util.Objects;

/**
 * An abstract, parameterized implementation of the EvaluationResult interface
 * that handles the name of the evaluation result. The value (i.e., mainly the
 * {@link #getValue()} method) has to be implemented by the extending class.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 * @param <T>
 */
public abstract class AbstractEvaluationResult<T> implements EvaluationResult<T> {

    /**
     * The result's name.
     */
    protected String name;

    /**
     * Constructor.
     * 
     * @param name The result's name.
     */
    public AbstractEvaluationResult(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + "=" + Objects.toString(getValue());
    }
}
