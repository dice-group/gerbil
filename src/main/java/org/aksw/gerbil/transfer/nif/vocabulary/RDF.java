package org.aksw.gerbil.transfer.nif.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class RDF {
	
	protected static final String uri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

	/**
	 * returns the URI for this schema
	 *
	 * @return the URI for this schema
	 */
	public static String getURI() {
		return uri;
	}

	protected static final Resource resource(final String local) {
		return ResourceFactory.createResource(uri + local);
	}

	protected static final Property property(final String local) {
		return ResourceFactory.createProperty(uri, local);
	}

	public static final Property Subject = property("subject");
	public static final Property Predicate = property("predicate");
	public static final Property Object = property("object");
	public static final Resource Statement = resource("statement");

}
