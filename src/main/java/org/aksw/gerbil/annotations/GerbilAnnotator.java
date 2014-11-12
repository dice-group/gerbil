package org.aksw.gerbil.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.aksw.gerbil.datatypes.ExperimentType;

@Retention(RetentionPolicy.RUNTIME)
public @interface GerbilAnnotator {

    public String name();
    
    public ExperimentType[] applicableForExperiments();
    
    public boolean couldBeCached();
}
