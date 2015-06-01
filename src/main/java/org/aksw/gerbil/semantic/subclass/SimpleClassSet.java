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

import java.util.Iterator;
import java.util.Set;

import com.carrotsearch.hppc.ObjectObjectOpenHashMap;
import com.carrotsearch.hppc.ObjectOpenHashSet;

public class SimpleClassSet implements ClassSet {

    // private ClassNode root;
    private ObjectObjectOpenHashMap<String, ClassNode> uriToNodeIndex = new ObjectObjectOpenHashMap<String, ClassNode>();
    private ObjectOpenHashSet<ClassNode> nodes = new ObjectOpenHashSet<ClassNode>();

    // @Override
    // public ClassNode getRoot() {
    // return root;
    // }

    @Override
    public ClassNode getNode(String uri) {
        if (uriToNodeIndex.containsKey(uri)) {
            return uriToNodeIndex.get(uri);
        } else {
            return null;
        }
    }

    @Override
    public void addNode(ClassNode node) {
        nodes.add(node);
        Set<String> uris = node.getUris();
        for (String uri : uris) {
            uriToNodeIndex.put(uri, node);
        }
    }

    @Override
    public void addUriToNode(ClassNode node, String uri) {
        node.addUri(uri);
        uriToNodeIndex.put(uri, node);
    }

    @Override
    public void removeNode(ClassNode node) {
        Set<String> uris = node.getUris();
        for (String uri : uris) {
            uriToNodeIndex.remove(uri);
        }
        nodes.remove(node);
    }

    @Override
    public Iterator<ClassNode> iterator() {
        return new ClassNodeSetIterator(nodes);
    }

    @Override
    public String toString() {
        return nodes.toString();
    }

    protected static class ClassNodeSetIterator implements Iterator<ClassNode> {
        private ObjectOpenHashSet<ClassNode> nodes;
        private int position;

        public ClassNodeSetIterator(ObjectOpenHashSet<ClassNode> nodes) {
            this.nodes = nodes;
            position = -1;
            moveToNextNode();
        }

        private void moveToNextNode() {
            ++position;
            while ((position < nodes.allocated.length) && (!nodes.allocated[position])) {
                ++position;
            }
        }

        @Override
        public boolean hasNext() {
            return position < nodes.allocated.length;
        }

        @Override
        public ClassNode next() {
            if (hasNext()) {
                ClassNode node = (ClassNode) ((Object[]) nodes.keys)[position];
                moveToNextNode();
                return node;
            } else {
                return null;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }
}
