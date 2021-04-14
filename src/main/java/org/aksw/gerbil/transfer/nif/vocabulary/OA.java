package org.aksw.gerbil.transfer.nif.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class OA {
	
	protected static final String uri = "http://www.w3.org/ns/oa#";

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

	public static final Property hasSource = property("hasSource");
	public static final Property hasTarget = property("hasTarget");
	
	public static final Resource SpecificResource = resource("SpecificResource");
	public static final Resource Annotation = resource("Annotation");
}
