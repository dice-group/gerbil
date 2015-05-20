/**
 * The MIT License (MIT)
 *
 * Copyright (C) ${year} Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
