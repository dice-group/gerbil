package org.aksw.gerbil.evaluate;

public class ObjectEvaluationResult implements EvaluationResult{
    private String name;

    private Object value;

    public ObjectEvaluationResult(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public Object getValueAsObject() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getValue() {
        return value;
    }

}
