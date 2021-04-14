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

    /**
     * Returns {@code true} if the given {@link Meaning} instances have overlapping
     * URI sets.
     * 
     * @param m1
     *            an instance of {@link Meaning} that should be checked for its
     *            equality with the second instance based on the semantics of the
     *            {@link Meaning} interface.
     * @param m2
     *            an instance of {@link Meaning} that should be checked for its
     *            equality with the first instance based on the semantics of the
     *            {@link Meaning} interface.
     * @return {@code true} if the given {@link Meaning} instances have overlapping
     *         URI sets, else {@code false}.
     */
    public static boolean equals(Meaning m1, Meaning m2) {
        Set<String> m1Uris = m1.getUris();
        Set<String> m2Uris = m2.getUris();
        boolean m1HasUris = m1Uris != null && (m1Uris.size() > 0);
        boolean m2HasUris = m2Uris != null && (m2Uris.size() > 0);
        if (m1HasUris && m2HasUris) {
            for (String uri : m1Uris) {
                if (m2Uris.contains(uri)) {
                    return true;
                }
            }
            return false;
        } else {
            return m1HasUris == m2HasUris;
        }
    }
}
