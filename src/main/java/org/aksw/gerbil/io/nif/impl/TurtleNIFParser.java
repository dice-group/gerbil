package org.aksw.gerbil.io.nif.impl;

import java.io.InputStream;
import java.io.Reader;

import org.aksw.gerbil.io.nif.AbstractNIFParser;
import org.aksw.gerbil.transfer.nif.NIFTransferPrefixMapping;
import org.apache.jena.riot.adapters.JenaReadersWriters.RDFReaderRIOT_TTL;
import org.apache.jena.riot.adapters.RDFReaderRIOT;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TurtleNIFParser extends AbstractNIFParser {

    private static final String HTTP_CONTENT_TYPE = "application/x-turtle";

    public TurtleNIFParser() {
        super(HTTP_CONTENT_TYPE);
    }

    @Override
    protected Model parseNIFModel(InputStream is) {
        RDFReaderRIOT rdfReader = new RDFReaderRIOT_TTL();
        Model nifModel = ModelFactory.createDefaultModel();
        nifModel.setNsPrefixes(NIFTransferPrefixMapping.getInstance());
        rdfReader.read(nifModel, is, "");
        return nifModel;
    }

    @Override
    protected Model parseNIFModel(Reader reader) {
        RDFReaderRIOT rdfReader = new RDFReaderRIOT_TTL();
        Model nifModel = ModelFactory.createDefaultModel();
        nifModel.setNsPrefixes(NIFTransferPrefixMapping.getInstance());
        rdfReader.read(nifModel, reader, "");
        return nifModel;
    }

}
