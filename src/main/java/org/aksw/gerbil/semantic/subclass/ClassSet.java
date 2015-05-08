package org.aksw.gerbil.semantic.subclass;

public interface ClassSet extends Iterable<ClassNode> {

    // public ClassNode getRoot();

    public ClassNode getNode(String uri);

    public void addNode(ClassNode node/* , ClassNode parent */);

    public void addUriToNode(ClassNode node, String uri);

    /**
     * Removes the given node and all its children.
     * 
     * @param node
     */
    public void removeNode(ClassNode node);
}
