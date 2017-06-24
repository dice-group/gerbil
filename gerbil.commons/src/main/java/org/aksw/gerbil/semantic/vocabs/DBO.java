/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.semantic.vocabs;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * The DBpedia ontology vocabulary (http://dbpedia.org/ontology/). <b>Note</b>
 * that this class contains only the resources, that are needed by GERBIL.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class DBO {

    protected static final String URI = "http://dbpedia.org/ontology/";

    /**
     * returns the URI for this schema
     * 
     * @return the URI for this schema
     */
    public static String getURI() {
        return URI;
    }

    protected static final Resource resource(String local) {
        return ResourceFactory.createResource(URI + local);
    }

    protected static final Property property(String local) {
        return ResourceFactory.createProperty(URI, local);
    }

    public static final Property wikiPageID = property("wikiPageID");
    public static final Property wikiPageRedirects = property("wikiPageRedirects");

}
