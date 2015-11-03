package org.aksw.gerbil.annotator.impl;

import java.io.IOException;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.ClosePermitionGranter;

public abstract class AbstractAnnotator implements Annotator {

    protected String name;
    protected ClosePermitionGranter granter;

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

    @Override
    public void setClosePermitionGranter(ClosePermitionGranter granter) {
        this.granter = granter;
    }

    @Override
    public final void close() throws IOException {
        if (granter.givePermissionToClose()) {
            performClose();
        }
    }

    protected void performClose() throws IOException {
        // nothing to do
    }
}
