package org.aksw.gerbil.annotator;

import java.util.List;

import org.aksw.gerbil.exceptions.GerbilException;
import org.apache.jena.rdf.model.Model;

public interface SWCTask2System extends Annotator {

    public List<Model> performTask2(Model model) throws GerbilException;

}
