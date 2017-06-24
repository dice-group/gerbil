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

import java.util.HashSet;
import java.util.Set;

import org.aksw.gerbil.semantic.sameas.SameAsRetriever;

/**
 * This {@link SameAsRetriever} is used to fix common problems with URIs, e.g.,
 * if a URI has the domain <code>en.dbpedia.org</code> (which is not existing) a
 * URI with the correct domain (<code>dbpedia.org</code>) is added.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class ErrorFixingSameAsRetriever implements SameAsRetriever {

    private static final String WRONG_EN_DBPEDIA_DOMAIN = "en.dbpedia.org";
    private static final String DBPEDIA_DOMAIN = "dbpedia.org";

    @Override
    public Set<String> retrieveSameURIs(String uri) {
        return retrieveSameURIs(SimpleDomainExtractor.extractDomain(uri), uri);
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
        Set<String> uris = null;
        if (WRONG_EN_DBPEDIA_DOMAIN.equals(domain)) {
            uris = new HashSet<String>();
            uris.add(uri);
            uris.add(uri.replace(WRONG_EN_DBPEDIA_DOMAIN, DBPEDIA_DOMAIN));
        }
        return uris;
    }

}
