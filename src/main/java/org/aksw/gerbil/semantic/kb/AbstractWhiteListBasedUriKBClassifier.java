/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
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
package org.aksw.gerbil.semantic.kb;

import java.util.Collection;
import java.util.List;

public abstract class AbstractWhiteListBasedUriKBClassifier implements UriKBClassifier {

    protected List<String> kbNamespaces;

    public AbstractWhiteListBasedUriKBClassifier(List<String> kbNamespaces) {
        this.kbNamespaces = kbNamespaces;
    }

    @Override
    public boolean isKBUri(String uri) {
        if (uri != null) {
            for (String namespace : kbNamespaces) {
                if (uri.startsWith(namespace)) {
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
