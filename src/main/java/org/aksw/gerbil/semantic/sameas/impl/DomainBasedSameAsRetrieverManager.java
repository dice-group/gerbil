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
package org.aksw.gerbil.semantic.sameas.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.semantic.sameas.SingleUriSameAsRetriever;

public class DomainBasedSameAsRetrieverManager implements SameAsRetriever {

    private List<SingleUriSameAsRetriever> staticRetrievers = new ArrayList<SingleUriSameAsRetriever>();
    private List<SingleUriSameAsRetriever> defaultRetrievers = new ArrayList<SingleUriSameAsRetriever>();
    private Map<String, List<SingleUriSameAsRetriever>> domainRetrieverMapping = new HashMap<>();

    public void addStaticRetriever(SingleUriSameAsRetriever retriever) {
        staticRetrievers.add(retriever);
    }

    public void addDefaultRetriever(SingleUriSameAsRetriever retriever) {
        defaultRetrievers.add(retriever);
    }

    public void addDomainSpecificRetriever(String domain, SingleUriSameAsRetriever retriever) {
        List<SingleUriSameAsRetriever> retrievers;
        if (domainRetrieverMapping.containsKey(domain)) {
            retrievers = domainRetrieverMapping.get(domain);
        } else {
            retrievers = new ArrayList<>();
            domainRetrieverMapping.put(domain, retrievers);
        }
        retrievers.add(retriever);
    }

    @Override
    public Set<String> retrieveSameURIs(String uri) {
        return retrieveSameURIs(SimpleDomainExtractor.extractDomain(uri), uri);
    }

    @Override
    public Set<String> retrieveSameURIs(String domain, String uri) {
        if (uri == null) {
            return null;
        }
        Set<String> result = null;
        if ((domain != null) && (domainRetrieverMapping.containsKey(domain))) {
            result = retrieve(domain, uri, domainRetrieverMapping.get(domain), result);
        } else {
            result = retrieve(domain, uri, defaultRetrievers, result);
        }
        result = retrieve(domain, uri, staticRetrievers, result);
        return result;
    }

    protected static Set<String> retrieve(String domain, String uri, Collection<SingleUriSameAsRetriever> retrievers,
            Set<String> result) {
        for (SingleUriSameAsRetriever retriever : retrievers) {
            result = merge(result, retriever.retrieveSameURIs(domain, uri));
        }
        return result;
    }

    public static Set<String> merge(Set<String> oldResults, Set<String> newResults) {
        if (newResults != null) {
            if (oldResults != null) {
                oldResults.addAll(newResults);
            } else {
                return newResults;
            }
        }
        return oldResults;
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
