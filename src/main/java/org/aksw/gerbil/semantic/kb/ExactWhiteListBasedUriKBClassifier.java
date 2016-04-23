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
package org.aksw.gerbil.semantic.kb;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This classifier is based on an exact list of URIs. It classifies an URI as
 * part of the KB if it can be found inside its internal list of URIs. Thus, it
 * does not use namespaces for classification, but complete URIs.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class ExactWhiteListBasedUriKBClassifier implements UriKBClassifier {

    protected Set<String> uris;

    public ExactWhiteListBasedUriKBClassifier(Set<String> uris) {
        this.uris = uris;
    }

    public ExactWhiteListBasedUriKBClassifier(Collection<String> uris) {
        this.uris = new HashSet<String>(uris);
    }

    @Override
    public boolean isKBUri(String uri) {
        return uris.contains(uri);
    }

    @Override
    public boolean containsKBUri(Collection<String> uris) {
        for (String uri : uris) {
            if (isKBUri(uri)) {
                return true;
            }
        }
        return false;
    }

}
