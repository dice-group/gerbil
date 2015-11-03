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
package org.aksw.gerbil.semantic.sameas;

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
