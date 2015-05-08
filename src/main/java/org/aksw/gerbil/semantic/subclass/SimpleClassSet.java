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
    public void addNode(ClassNode node/* , ClassNode parent */) {
        // if (root != null) {
        // // we have to remove the root!
        // removeNode(root);
        // }
        // root = node;
        // } else {
        // if (parent != null) {
        // parent.addChild(node);
        // node.addParent(parent);
        // }
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
        // remove connection to children
        // List<ClassNode> children = node.getChildren();
        // for (ClassNode child : children) {
        // child.removeParent(node);
        // if (child.getParents().size() == 0) {
        // removeNode(child);
        // }
        // }
        // remove connection to parents
        // List<ClassNode> parents = node.getParents();
        // for (ClassNode parent : parents) {
        // parent.removeChild(node);
        // }
        Set<String> uris = node.getUris();
        for (String uri : uris) {
            uriToNodeIndex.remove(uri);
        }
        nodes.remove(node);
    }

    @Override
    public Iterator<ClassNode> iterator() {
        // return new SimpleSubClassHierarchyIterator(root);
        return new ClassNodeSetIterator(nodes);
    }

    // /**
    // * Pre-order iterator.
    // *
    // * @author Michael R&ouml;der <roeder@informatik.uni-leipzig.de>
    // *
    // */
    // protected static class SimpleSubClassHierarchyIterator implements
    // Iterator<ClassNode> {
    // private Stack<Iterator<ClassNode>> stack = new
    // Stack<Iterator<ClassNode>>();
    //
    // public SimpleSubClassHierarchyIterator(ClassNode root) {
    // List<ClassNode> rootList = new ArrayList<ClassNode>(1);
    // rootList.add(root);
    // stack.add(rootList.iterator());
    // }
    //
    // private void addChildrenToStack(ClassNode n) {
    // stack.add(n.getChildren().iterator());
    // }
    //
    // @Override
    // public boolean hasNext() {
    // return !stack.empty();
    // }
    //
    // @Override
    // public ClassNode next() {
    // ClassNode next = null;
    // while ((!stack.empty()) && (stack.peek().hasNext())) {
    // stack.pop();
    // }
    // if (!stack.empty()) {
    // next = stack.peek().next();
    // addChildrenToStack(next);
    // }
    // return next;
    // }
    //
    // @Override
    // public void remove() {
    // throw new UnsupportedOperationException();
    // }
    // }

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
