package org.aksw.gerbil.dataset;

import org.aksw.gerbil.exceptions.GerbilException;

public interface InitializableDataset extends Dataset {

    public void init() throws GerbilException;
}
