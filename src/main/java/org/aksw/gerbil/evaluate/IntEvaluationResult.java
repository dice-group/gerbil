package org.aksw.gerbil.evaluate;

public class IntEvaluationResult implements EvaluationResult {

    private String name;
    private int value;

    public IntEvaluationResult(String name, int value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getValue() {
        return value;
    }

    public int getValueAsInt() {
        return value;
    }
}
