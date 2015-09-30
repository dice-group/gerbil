/**
 * This file is part of NIF transfer library for the General Entity Annotator Benchmark.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NIF transfer library for the General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
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

    public static final Resource Annotation = resource("Annotation");
    public static final Resource Context = resource("Context");
    public static final Resource String = resource("String");
    public static final Resource RFC5147String = resource("RFC5147String");

    public static final Property anchorOf = property("anchorOf");
    public static final Property beginIndex = property("beginIndex");
    public static final Property confidence = property("confidence");
    public static final Property isString = property("isString");
    public static final Property endIndex = property("endIndex");
    public static final Property keyword = property("keyword");
    public static final Property referenceContext = property("referenceContext");
    public static final Property topic = property("topic");

}
