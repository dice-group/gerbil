package org.aksw.gerbil.io.nif.utils;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class NIFModelHelper {

    public static Model getDefaultModel() {
        Model nifModel = ModelFactory.createDefaultModel();
        nifModel.setNsPrefixes(NIFTransferPrefixMapping.getInstance());
        return nifModel;
    }

}
