package org.aksw.gerbil.evaluate;

public class DoubleEvaluationResult implements EvaluationResult {

    private String name;
    private double value;
    
    public DoubleEvaluationResult(String name, double value) {
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
    
    public double getValueAsDouble() {
        return value;
    }
}
