package org.aksw.gerbil.semantic.sameas;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.atlas.web.HttpException;
import org.apache.jena.riot.adapters.RDFReaderRIOT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;

public class HTTPBasedSameAsRetriever implements SameAsRetriever {

    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPBasedSameAsRetriever.class);

    private static final int MAXIMUM_NUMBER_OF_TRIES = 3;

    private RDFReader reader = new RDFReaderRIOT();

    @Override
    public Set<String> retrieveSameURIs(String uri) {
        Model model = ModelFactory.createDefaultModel();
        try {
            requestModel(model, uri, 0);
        } catch (Exception e) {
            LOGGER.info("Exception while requesting uri \"" + uri + "\". Returning null.", e);
            return null;
        }
        Set<String> result = null;
        Resource resource = model.getResource(uri);
        if (model.contains(resource, OWL.sameAs)) {
            result = new HashSet<String>();
            result.add(uri);
            NodeIterator iterator = model.listObjectsOfProperty(resource, OWL.sameAs);
            while (iterator.hasNext()) {
                result.add(iterator.next().asResource().getURI());
            }
        }
        return result;
    }

    protected void requestModel(Model model, String uri, int retryCount) throws Exception {
        try {
            reader.read(model, uri);
        } catch (HttpException e) {
            ++retryCount;
            if (retryCount < MAXIMUM_NUMBER_OF_TRIES) {
                requestModel(model, uri, retryCount);
            } else {
                throw e;
            }
        }
    }

    @Override
    public void addSameURIs(Set<String> uris) {
        Set<String> temp = new HashSet<String>();
        Set<String> result;
        for (String uri : uris) {
            result = retrieveSameURIs(uri);
            if (result != null) {
                temp.addAll(retrieveSameURIs(uri));
            }
        }
        uris.addAll(temp);
    }

}
