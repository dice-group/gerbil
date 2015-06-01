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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassifyingClassNodeFactory implements ClassNodeFactory<ClassifiedClassNode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifyingClassNodeFactory.class);

    private int classId;

    public ClassifyingClassNodeFactory(int classId) {
        this.classId = classId;
    }

    @Override
    public ClassifiedClassNode createNode(String uri) {
        ClassifiedClassNode node = new ClassifiedClassNode(uri);
        node.addClassId(classId);
        return node;
    }

    @Override
    public void updateNode(ClassNode oldNode) {
        if (oldNode instanceof ClassifiedClassNode) {
            ((ClassifiedClassNode) oldNode).addClassId(classId);
        } else {
            LOGGER.warn("Got a node that is not an instance of the ClassifiedClassNode class. Can't add classification to this node.");
        }
    }

}
