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
