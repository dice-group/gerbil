package org.aksw.gerbil.transfer.nif;

import com.hp.hpl.jena.rdf.model.Model;

public class TurtleNIFDocumentCreator extends AbstractNIFDocumentCreator {
	
	private static final String HTTP_CONTENT_TYPE = "application/x-turtle";

	public TurtleNIFDocumentCreator() {
		super(HTTP_CONTENT_TYPE);
	}

	@Override
	protected String generateNIFStringFromModel(Model nifModel) {
		// FIXME write document to String using an RDF writer
		return null;
	}

}
