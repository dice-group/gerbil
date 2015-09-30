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
package org.aksw.gerbil.transfer.nif.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningEqualityChecker;

/**
 * An Annotation is a meaning which is added to a document.
 * 
 * @author Michael Röder
 * 
 */
public class Annotation implements Meaning {

    @Deprecated
    protected String uri;
    protected Set<String> uris = new HashSet<String>();

    public Annotation(String uri) {
        this.uri = uri;
        this.uris.add(uri);
    }

    public Annotation(Set<String> uris) {
        setUris(uris);
    }

    @Deprecated
    @Override
    public String getUri() {
        return uri;
    }

    @Deprecated
    @Override
    public void setUri(String uri) {
        this.uri = uri;
        this.uris.clear();
        this.uris.add(uri);
    }

    @Override
    public Set<String> getUris() {
        return uris;
    }

    @Override
    public void setUris(Set<String> uris) {
        this.uris = uris;
        if (uris.size() > 0) {
            this.uri = uris.iterator().next();
        } else {
            this.uri = null;
        }
    }

    @Override
    public void addUri(String uri) {
        this.uris.add(uri);
        if (this.uri == null) {
            this.uri = uri;
        }
    }

    @Override
    public boolean containsUri(String uri) {
        return uris.contains(uri);
    }

    @Override
    public String toString() {
        return "Annotation [uri=" + Arrays.toString(uris.toArray()) + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uris == null) ? 0 : uris.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Annotation other = (Annotation) obj;
        if (uris == null) {
            if (other.uris != null)
                return false;
        } else if (!MeaningEqualityChecker.overlaps(this, other))
            return false;
        return true;
    }

}
