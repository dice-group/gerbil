package org.aksw.gerbil.transfer.nif;

import java.io.StringWriter;

import com.hp.hpl.jena.rdf.model.Model;

public class TurtleNIFDocumentCreator extends AbstractNIFDocumentCreator {

    private static final String HTTP_CONTENT_TYPE = "application/x-turtle";

    public TurtleNIFDocumentCreator() {
        super(HTTP_CONTENT_TYPE);
    }

    @Override
    protected String generateNIFStringFromModel(Model nifModel) {
        StringWriter writer = new StringWriter();
        nifModel.write(writer, "TTL");
        return writer.toString();
    }

}
