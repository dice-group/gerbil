package org.aksw.gerbil.transfer.nif;

import java.io.Reader;

import org.apache.jena.riot.adapters.JenaReadersWriters.RDFReaderRIOT_TTL;
import org.apache.jena.riot.adapters.RDFReaderRIOT;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TurtleNIFDocumentParser extends AbstractNIFDocumentParser {

    private static final String HTTP_CONTENT_TYPE = "application/x-turtle";

    public TurtleNIFDocumentParser() {
        super(HTTP_CONTENT_TYPE);
    }

    @Override
    protected Model parseNIFModelFromReader(Reader reader) throws Exception {
        RDFReaderRIOT rdfReader = new RDFReaderRIOT_TTL();
        Model nifModel = ModelFactory.createDefaultModel();
        nifModel.setNsPrefixes(NIFTransferPrefixMapping.getInstance());
        rdfReader.read(nifModel, reader, "");
        return nifModel;
    }

}
