package org.aksw.gerbil.web.config;

import org.aksw.gerbil.datatypes.ExperimentType;

public class AbstractGerbilAdapterMetaData implements GerbilAdapterMetaData {

    protected String name;
    protected boolean couldBeCached;
    protected ExperimentType applicableForExperiments[];

    public AbstractGerbilAdapterMetaData(String name, boolean couldBeCached, ExperimentType... applicableForExperiment) {
        this.name = name;
        this.couldBeCached = couldBeCached;
        this.applicableForExperiments = applicableForExperiment;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean couldBeCached() {
        return couldBeCached;
    }

    @Override
    public void setCouldBeCached(boolean couldBeCached) {
        this.couldBeCached = couldBeCached;
    }

    @Override
    public boolean isApplicableForExperiment(ExperimentType type) {
        for (int i = 0; i < applicableForExperiments.length; i++) {
            if (applicableForExperiments[i].equalsOrContainsType(type)) {
                return true;
            }
        }
        return false;
    }

}
