package org.aksw.gerbil.evaluate;

public class ObjectEvaluationResult<T> extends AbstractEvaluationResult<T> implements EvaluationResult<T> {

    protected T value;

    public ObjectEvaluationResult(String name, T value) {
        super(name);
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }

}
