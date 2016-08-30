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
package org.aksw.gerbil.semantic.sameas.impl.index;

import java.util.HashSet;
import java.util.Set;

import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.http.AbstractHttpRequestEmitter;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.semantic.sameas.index.LuceneConstants.IndexingStrategy;
import org.aksw.gerbil.semantic.sameas.index.Searcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexBasedSameAsRetriever extends AbstractHttpRequestEmitter implements SameAsRetriever {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexBasedSameAsRetriever.class);
	private Searcher searcher;

    public IndexBasedSameAsRetriever(String indexPath, IndexingStrategy strategy) throws GerbilException{
    	searcher = new Searcher(indexPath, strategy);
    }
    
    public IndexBasedSameAsRetriever(String indexPath, String strategy) throws GerbilException{
    	searcher = new Searcher(indexPath, IndexingStrategy.valueOf(strategy));
    }
    
    @Override
    public Set<String> retrieveSameURIs(String uri) {
        if ((uri == null) || (uri.isEmpty())) {
            return null;
        }
        try {
			return (Set<String>) searcher.search(uri);
		} catch (GerbilException e) {
			LOGGER.warn("Could not retrieve Same Uris", e);
			return null;
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
