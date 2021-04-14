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
package org.aksw.gerbil.io.nif.utils;

import org.aksw.gerbil.transfer.nif.vocabulary.ITSRDF;
import org.aksw.gerbil.transfer.nif.vocabulary.NIF;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.shared.impl.PrefixMappingImpl;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

/**
 * Defines the default prefix mapping for a NIF model.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class NIFTransferPrefixMapping {

    private static final String PREFIX_TO_NS_MAPPING[][] = new String[][] { { "nif", NIF.getURI() },
            { "itsrdf", ITSRDF.getURI() }, { "rdf", RDF.getURI() }, { "rdfs", RDFS.getURI() },
            { "xsd", XSDDatatype.XSD + '#' } };

    private static PrefixMapping instance = null;

    public static synchronized PrefixMapping getInstance() {
        if (instance == null) {
            instance = new PrefixMappingImpl();
            for (int i = 0; i < PREFIX_TO_NS_MAPPING.length; ++i) {
                instance.setNsPrefix(PREFIX_TO_NS_MAPPING[i][0], PREFIX_TO_NS_MAPPING[i][1]);
            }
        }
        return instance;
    }
}
