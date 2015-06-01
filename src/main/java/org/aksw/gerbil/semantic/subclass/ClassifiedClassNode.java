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
package org.aksw.gerbil.semantic.subclass;

import java.util.Arrays;
import java.util.Set;

import com.carrotsearch.hppc.IntOpenHashSet;

public class ClassifiedClassNode extends SimpleClassNode {

    private IntOpenHashSet classIds = new IntOpenHashSet(2);

    public ClassifiedClassNode(String uri) {
        super(uri);
    }

    public ClassifiedClassNode(Set<String> uris/*
                                                * , List<SimpleClassNode>
                                                * children
                                                */) {
        super(uris/* , children */);
    }

    public IntOpenHashSet getClassIds() {
        return classIds;
    }

    public void setClassIds(IntOpenHashSet classIds) {
        this.classIds = classIds;
    }

    public void addClassId(int classId) {
        this.classIds.add(classId);
    }

    @Override
    public String toString() {
        return "(uris=" + Arrays.toString(uris.toArray(new String[uris.size()])) + ",classId=" + classIds.toString()
                + ")";
    }
}
