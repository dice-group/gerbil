package org.aksw.gerbil.transfer.nif;

import org.aksw.gerbil.transfer.nif.vocabulary.ITSRDF;
import org.aksw.gerbil.transfer.nif.vocabulary.NIF;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

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
