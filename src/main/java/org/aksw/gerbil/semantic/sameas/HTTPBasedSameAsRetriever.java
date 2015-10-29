/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
import com.hp.hpl.jena.rdf.model.ResIterator;
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
            LOGGER.debug("Exception while requesting uri \"" + uri + "\". Returning null.", e);
            return null;
        }
        Set<String> result = new HashSet<String>();
        result.add(uri);
        findLinks(uri, result, model);
        if (result.size() > 1) {
            return result;
        } else {
            return null;
        }
    }

    protected void findLinks(String uri, Set<String> uris, Model model) {
        Resource resource = model.getResource(uri);
        String foundUri;
        if (model.contains(resource, OWL.sameAs)) {
            NodeIterator iterator = model.listObjectsOfProperty(resource, OWL.sameAs);
            while (iterator.hasNext()) {
                foundUri = iterator.next().asResource().getURI();
                if (!uris.contains(foundUri)) {
                    uris.add(foundUri);
                    findLinks(foundUri, uris, model);
                }
            }
        }
        if (model.contains(null, OWL.sameAs, resource)) {
            ResIterator iterator = model.listSubjectsWithProperty(OWL.sameAs, resource);
            while (iterator.hasNext()) {
                foundUri = iterator.next().getURI();
                if (!uris.contains(foundUri)) {
                    uris.add(foundUri);
                    findLinks(foundUri, uris, model);
                }
            }
        }
    }

    protected void requestModel(Model model, String uri, int retryCount) throws Exception {
        if (uri == null) {
            return;
        }
        try {
            reader.read(model, uri);
        } catch (HttpException e) {
            // if this URI is unknown
            if ((e.getStatusLine() != null) && (e.getStatusLine().equals("Not Found"))) {
                return;
            }
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
