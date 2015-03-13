package org.aksw.gerbil.semantic;

import java.util.Set;

import org.apache.jena.riot.adapters.RDFReaderRIOT;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;

public class HTTPBasedSameAsRetriever implements SameAsRetriever {
    private RDFReader reader = new RDFReaderRIOT();

    @Override
    public Set<String> retrieveSameURIs(String uri) {
        Model model = ModelFactory.createDefaultModel();
        try {
            reader.read(model, uri);
        } catch (Exception e) {
            return null;
        }
        Set<String> result = null;
        Resource resource = model.getResource(uri);
        if (model.contains(resource, OWL.sameAs)) {
            // FIXME
        }
        return result;
    }

}
