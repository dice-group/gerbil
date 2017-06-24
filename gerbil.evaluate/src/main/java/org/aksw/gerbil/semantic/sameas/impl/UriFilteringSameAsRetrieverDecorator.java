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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.aksw.gerbil.semantic.sameas.SameAsRetriever;

public class UriFilteringSameAsRetrieverDecorator extends AbstractSameAsRetrieverDecorator {

    private Set<String> domainBlacklist;

    public UriFilteringSameAsRetrieverDecorator(SameAsRetriever decoratedRetriever, String... domainBlacklist) {
        this(decoratedRetriever, Arrays.asList(domainBlacklist));
    }

    public UriFilteringSameAsRetrieverDecorator(SameAsRetriever decoratedRetriever,
            Collection<String> domainBlacklist) {
        super(decoratedRetriever);
        this.domainBlacklist = new HashSet<String>(domainBlacklist);
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
    public Set<String> retrieveSameURIs(String uri) {
        return filter(decoratedRetriever.retrieveSameURIs(uri));
    }

    @Override
    public Set<String> retrieveSameURIs(String domain, String uri) {
        return filter(decoratedRetriever.retrieveSameURIs(domain, uri));
    }

    protected Set<String> filter(Set<String> uris) {
        if (uris == null) {
            return null;
        }
        Set<String> urisToRemove = null;
        String domain;
        for (String uri : uris) {
            domain = SimpleDomainExtractor.extractDomain(uri);
            if (domainBlacklist.contains(domain)) {
                if (urisToRemove == null) {
                    urisToRemove = new HashSet<String>();
                }
                urisToRemove.add(uri);
            }
        }
        if (urisToRemove != null) {
            uris.removeAll(urisToRemove);
        }
        return uris;
    }

}
