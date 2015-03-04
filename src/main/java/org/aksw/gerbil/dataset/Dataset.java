package org.aksw.gerbil.dataset;

import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;

public interface Dataset {

    public int size();

    public String getName();

    public List<Document> getInstances();
}
