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

public class MultipleSameAsRetriever implements SameAsRetriever {

    private SameAsRetriever retriever[];

    public MultipleSameAsRetriever(SameAsRetriever... retriever) {
        this.retriever = retriever;
    }

    @Override
    public Set<String> retrieveSameURIs(String uri) {
        Set<String> result = null, newResult = null;
        for (int i = 0; i < retriever.length; ++i) {
            newResult = retriever[i].retrieveSameURIs(uri);
            if (newResult != null) {
                if (result != null) {
                    result.addAll(newResult);
                } else {
                    result = newResult;
                }
            }
        }
        return result;
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

    public SameAsRetriever[] getRetriever() {
        return retriever;
    }

    public void setRetriever(SameAsRetriever[] retriever) {
        this.retriever = retriever;
    }

    @Override
    public Set<String> retrieveSameURIs(String domain, String uri) {
        Set<String> result = null, newResult = null;
        for (int i = 0; i < retriever.length; ++i) {
            newResult = retriever[i].retrieveSameURIs(domain, uri);
            if (newResult != null) {
                if (result != null) {
                    result.addAll(newResult);
                } else {
                    result = newResult;
                }
            }
        }
        return result;
    }

}
