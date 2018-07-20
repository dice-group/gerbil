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
package org.aksw.gerbil.semantic.sameas.impl.model;

import java.util.HashSet;
import java.util.Set;

import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.semantic.vocabs.DBO;
import org.aksw.gerbil.utils.URIValidator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;

public abstract class AbstractRDFModelBasedSameAsRetriever implements SameAsRetriever {

    private static final Property SAME_AS_PROPERTIES[] = new Property[] { OWL.sameAs, DBO.wikiPageRedirects };

    protected abstract Model getModel(String uri);

    
    @Override
    public Set<String> retrieveSameURIs(String uri) {
        if ((uri == null) || (uri.isEmpty())) {
            return null;
        }
        Model model = getModel(uri);
        if (model == null) {
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

    public static void findLinks(String uri, Set<String> uris, Model model) {
        Resource resource = model.getResource(uri);
        for (int i = 0; i < SAME_AS_PROPERTIES.length; ++i) {
            findLinks(resource, uris, model, SAME_AS_PROPERTIES[i]);
        }
    }

    public static void findLinks(Resource resource, Set<String> uris, Model model, Property sameAsProperty) {
        String foundUri;
        if (model.contains(resource, sameAsProperty)) {
            NodeIterator iterator = model.listObjectsOfProperty(resource, sameAsProperty);
            while (iterator.hasNext()) {
                foundUri = iterator.next().asResource().getURI();
                
                if (!uris.contains(foundUri) && URIValidator.isValidURI(foundUri)) {
                    uris.add(foundUri);
                    findLinks(foundUri, uris, model);
                }
            }
        }
        if (model.contains(null, sameAsProperty, resource)) {
            ResIterator iterator = model.listSubjectsWithProperty(sameAsProperty, resource);
            while (iterator.hasNext()) {
                foundUri = iterator.next().getURI();
                if (!uris.contains(foundUri)  && URIValidator.isValidURI(foundUri)) {
                    uris.add(foundUri);
                    findLinks(foundUri, uris, model);
                }
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

    @Override
    public Set<String> retrieveSameURIs(String domain, String uri) {
        return retrieveSameURIs(uri);
    }
}
