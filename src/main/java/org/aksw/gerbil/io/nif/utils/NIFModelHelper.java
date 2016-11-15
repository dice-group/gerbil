package org.aksw.gerbil.io.nif.utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class NIFModelHelper {

    public static Model getDefaultModel() {
        Model nifModel = ModelFactory.createDefaultModel();
        nifModel.setNsPrefixes(NIFTransferPrefixMapping.getInstance());
        return nifModel;
    }

}
