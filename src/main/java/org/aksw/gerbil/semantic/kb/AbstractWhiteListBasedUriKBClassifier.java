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
import java.util.List;

public abstract class AbstractWhiteListBasedUriKBClassifier implements UriKBClassifier {

    protected String kbNamespaces[];

    public AbstractWhiteListBasedUriKBClassifier(String kbNamespaces[]) {
        this.kbNamespaces = kbNamespaces;
    }

    public AbstractWhiteListBasedUriKBClassifier(List<String> kbNamespaces) {
        this.kbNamespaces = kbNamespaces.toArray(new String[kbNamespaces.size()]);
    }

    @Override
    public boolean isKBUri(String uri) {
        if (uri != null) {
            for (int i = 0; i < kbNamespaces.length; ++i) {
                if (uri.startsWith(kbNamespaces[i])) {
                    return true;
                }
            }
        }
        return false;
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
