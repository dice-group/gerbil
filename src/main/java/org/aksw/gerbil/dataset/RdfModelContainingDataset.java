package org.aksw.gerbil.dataset;

import com.hp.hpl.jena.rdf.model.Model;

public interface RdfModelContainingDataset extends Dataset {

    public Model getRdfModel();
}
