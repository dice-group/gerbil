package org.aksw.gerbil.semantic.sameas.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.semantic.sameas.SameAsRetrieverDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This {@link SameAsRetrieverDecorator} implements a crawling strategy for
 * retrieving new URIs. As long as new URIs are found, it calls the decorated
 * {@link SameAsRetriever} with these new links.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class CrawlingSameAsRetrieverDecorator extends AbstractSameAsRetrieverDecorator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlingSameAsRetrieverDecorator.class);

    private boolean debugCrawling = false;

    public CrawlingSameAsRetrieverDecorator(SameAsRetriever decoratedRetriever) {
        super(decoratedRetriever);
    }

    @Override
    public Set<String> retrieveSameURIs(String uri) {
        Set<String> uris = new HashSet<String>();
        uris.add(uri);
        addSameURIs(uris);
        if (uris.size() > 1) {
            return uris;
        } else {
            return null;
        }
    }

    @Override
    public Set<String> retrieveSameURIs(String domain, String uri) {
        return retrieveSameURIs(uri);
    }

    @Override
    public void addSameURIs(Set<String> uris) {
        Queue<String> queue = new LinkedList<String>();
        queue.addAll(uris);
        String uri;
        Set<String> newUris;
        while (queue.size() > 0) {
            uri = queue.poll();
            newUris = decoratedRetriever.retrieveSameURIs(uri);
            // if new URIs have been retrieved
            if (newUris != null) {
                for (String newUri : newUris) {
                    // check whether the URI was really not known before and add
                    // it to the queue and the result set
                    if (!uris.contains(newUri)) {
                        if (debugCrawling && LOGGER.isDebugEnabled()) {
                            LOGGER.debug(uri + " -> " + newUri);
                        }
                        queue.add(newUri);
                        uris.add(newUri);
                    }
                }
            }
        }
    }

    public boolean isDebugCrawling() {
        return debugCrawling;
    }

    public void setDebugCrawling(boolean debugCrawling) {
        this.debugCrawling = debugCrawling;
    }

}
