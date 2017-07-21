package org.aksw.gerbil.annotator;

import java.util.List;

import org.aksw.gerbil.exceptions.GerbilException;
import org.apache.jena.rdf.model.Model;

public interface SWCTask1System extends Annotator {

    public List<Model> performTask1(Model model) throws GerbilException;

}
