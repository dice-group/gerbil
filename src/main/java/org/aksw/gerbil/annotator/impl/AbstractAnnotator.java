package org.aksw.gerbil.annotator.impl;

import org.aksw.gerbil.annotator.Annotator;

public abstract class AbstractAnnotator implements Annotator {

    protected String name;

    public AbstractAnnotator() {
    }

    public AbstractAnnotator(String name) {
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
