package org.aksw.gerbil.dataset.check.impl;

import org.aksw.gerbil.dataset.check.EntityCheckerManager;

public class CachingEntityCheckerManagerDecorator extends AbstractEntityCheckerManagerDecorator {

    public CachingEntityCheckerManagerDecorator(EntityCheckerManager decorated) {
        super(decorated);
    }

}
