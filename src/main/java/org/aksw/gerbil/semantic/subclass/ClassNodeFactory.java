package org.aksw.gerbil.semantic.subclass;

public interface ClassNodeFactory<T extends ClassNode> {

    public T createNode(String uri);

    public void updateNode(ClassNode oldNode);
}
