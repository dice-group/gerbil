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
