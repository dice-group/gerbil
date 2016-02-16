package org.aksw.gerbil.dataset.check.impl;

import org.aksw.gerbil.dataset.check.EntityCheckerManager;
import org.aksw.gerbil.dataset.check.EntityCheckerManagerDecorator;

public abstract class AbstractEntityCheckerManagerDecorator implements EntityCheckerManagerDecorator {

    protected EntityCheckerManager decorated;

    public AbstractEntityCheckerManagerDecorator(EntityCheckerManager decorated) {
        this.decorated = decorated;
    }

    @Override
    public EntityCheckerManager getDecorated() {
        return decorated;
    }

}
