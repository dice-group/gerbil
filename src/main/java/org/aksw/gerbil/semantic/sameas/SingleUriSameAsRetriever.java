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

import java.util.Set;

/**
 * This interface defines methods implemented by classes that can retrieve URIs
 * pointing at the same entity as the given URI.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public interface SingleUriSameAsRetriever {

    /**
     * Returns a Set containing the URIs having a sameAs link to this URI or
     * null if no such URIs could be found.
     * 
     * @param uri
     *            the URI for which sameAs links should be discovered
     * @return a Set of URIs or null if no URIs could be found
     */
    public Set<String> retrieveSameURIs(String uri);

    /**
     * Returns a Set containing the URIs having a sameAs link to this URI or
     * null if no such URIs could be found.
     * 
     * @param uri
     *            the URI for which sameAs links should be discovered
     * @param domain
     *            the domain of the URI
     * @return a Set of URIs or null if no URIs could be found
     */
    public Set<String> retrieveSameURIs(String domain, String uri);
}
