/**
 * This file is part of NIF transfer library for the General Entity Annotator
 * Benchmark.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is free
 * software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NIF transfer library for the General Entity Annotator Benchmark.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.transfer.nif.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class PROV {

	protected static final String uri = "http://www.w3.org/ns/prov#";

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

	public static final Resource Activity = resource("Activity");
	public static final Resource Agent = resource("Agent");
	public static final Resource Entity = resource("Entity");

	public static final Property endedAtTime = property("endedAtTime");
    public static final Property startedAtTime = property("startedAtTime");
	public static final Property wasGeneratedBy = property("wasGeneratedBy");
	public static final Property wasAssociatedWith = property("wasAssociatedWith");

}
