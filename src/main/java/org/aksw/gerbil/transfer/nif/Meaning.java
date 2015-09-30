/**
 * This file is part of NIF transfer library for the General Entity Annotator Benchmark.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NIF transfer library for the General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.transfer.nif;

import java.util.Set;

/**
 * A class implementing this interface contains a set of URI pointing to the
 * meaning of this object. Note that it is assumed that all URIs of this set are
 * pointing exactly to the same meaning. Thus, they could be connected using an
 * owl:sameAs predicate.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * 
 */
public interface Meaning extends Marking {

    @Deprecated
    public String getUri();

    @Deprecated
    public void setUri(String uri);

    public Set<String> getUris();

    public void setUris(Set<String> uris);

    public void addUri(String uri);

    public boolean containsUri(String uri);
}
