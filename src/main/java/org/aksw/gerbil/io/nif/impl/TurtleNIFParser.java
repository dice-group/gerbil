package org.aksw.gerbil.io.nif.impl;

import java.io.InputStream;
import java.io.Reader;

import org.aksw.gerbil.io.nif.AbstractNIFParser;
import org.apache.jena.riot.adapters.JenaReadersWriters.RDFReaderRIOT_TTL;
import org.apache.jena.riot.adapters.RDFReaderRIOT;

import com.hp.hpl.jena.rdf.model.Model;

public class TurtleNIFParser extends AbstractNIFParser {

    private static final String HTTP_CONTENT_TYPE = "application/x-turtle";

    public TurtleNIFParser() {
        super(HTTP_CONTENT_TYPE);
    }

    @Override
    protected Model parseNIFModel(InputStream is, Model nifModel) {
        RDFReaderRIOT rdfReader = new RDFReaderRIOT_TTL();
        rdfReader.read(nifModel, is, "");
        return nifModel;
    }

    @Override
    protected Model parseNIFModel(Reader reader, Model nifModel) {
        RDFReaderRIOT rdfReader = new RDFReaderRIOT_TTL();
        rdfReader.read(nifModel, reader, "");
        return nifModel;
    }

}
