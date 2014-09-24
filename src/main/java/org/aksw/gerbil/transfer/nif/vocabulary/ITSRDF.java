package org.aksw.gerbil.transfer.nif.vocabulary;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class ITSRDF {

	protected static final String uri = "http://www.w3.org/2005/11/its/rdf#";

	/**
	 * returns the URI for this schema
	 * 
	 * @return the URI for this schema
	 */
	public static String getURI() {
		return uri;
	}

	protected static final Resource resource(String local) {
		return ResourceFactory.createResource(uri + local);
	}

	protected static final Property property(String local) {
		return ResourceFactory.createProperty(uri, local);
	}

	public static final Property taIdentRef = property("taIdentRef");
	public static final Property taSource = property("taSource");
	public static final Property taConfidence = property("taConfidence");

}
