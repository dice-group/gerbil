package org.aksw.gerbil.transfer.nif.vocabulary;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class NIF {

	protected static final String uri = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#";

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

	public static final Resource Context = resource("Context");
	public static final Resource String = resource("String");
	public static final Resource RFC5147String = resource("RFC5147String");

	public static final Property anchorOf = property("anchorOf");
	public static final Property beginIndex = property("beginIndex");
	public static final Property isString = property("isString");
	public static final Property endIndex = property("endIndex");
	public static final Property referenceContext = property("referenceContext");

}
