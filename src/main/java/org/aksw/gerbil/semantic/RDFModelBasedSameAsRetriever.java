package org.aksw.gerbil.semantic;

import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.vocabulary.OWL;

public class RDFModelBasedSameAsRetriever implements SameAsRetriever {

    private Model model;

    public RDFModelBasedSameAsRetriever(Model model) {
        this.model = model;
    }

    @Override
    public Set<String> retrieveSameURIs(String uri) {
        Set<String> result = null;
        Resource uriAsResource = new ResourceImpl(uri);
        if (model.containsResource(uriAsResource)
                && (model.contains(uriAsResource, OWL.sameAs) || model.contains(null, OWL.sameAs, uriAsResource))) {
            result = new HashSet<String>();
            result.add(uri);
            // FIXME isn't a real "crawling" needed?
            ResIterator resIterator = model.listSubjectsWithProperty(OWL.sameAs, uriAsResource);
            while (resIterator.hasNext()) {
                result.add(resIterator.next().toString());
            }
            NodeIterator nodeIter = model.listObjectsOfProperty(uriAsResource, OWL.sameAs);
            while (nodeIter.hasNext()) {
                result.add(nodeIter.next().toString());
            }
        }
        return result;
    }

}
