package org.aksw.gerbil.evaluate;

import java.util.ArrayList;
import java.util.List;
public class ExtendedEvaluationResult implements EvaluationResult{
    private String name;
    private List<ExtendedMacros> value;

    public ExtendedEvaluationResult(String name) {
        this.name = name;
        this.value = new ArrayList<ExtendedMacros>();
    }

    public ExtendedEvaluationResult(String name, ExtendedMacros[] value) {
        this(name);
        this.addMacros(value);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<ExtendedMacros> getValue() {
        return value;
    }

    public void addMacros(ExtendedMacros... macros) {
        for(ExtendedMacros macro:macros){
            this.value.add(macro);
        }
    }
}
