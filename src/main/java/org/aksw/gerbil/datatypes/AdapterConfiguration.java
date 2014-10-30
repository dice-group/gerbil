package org.aksw.gerbil.datatypes;


public interface AdapterConfiguration {

    public String getName();

    public void setName(String name);

    public boolean couldBeCached();

    public void setCouldBeCached(boolean couldBeCached);

    public boolean isApplicableForExperiment(ExperimentType type);
}
