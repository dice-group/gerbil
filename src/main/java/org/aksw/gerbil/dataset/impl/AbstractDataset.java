package org.aksw.gerbil.dataset.impl;

import org.aksw.gerbil.dataset.Dataset;

public abstract class AbstractDataset implements Dataset {

    protected String name;

    public AbstractDataset() {
    }

    public AbstractDataset(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

}
