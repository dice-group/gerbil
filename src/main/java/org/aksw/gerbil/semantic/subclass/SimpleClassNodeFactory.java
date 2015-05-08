package org.aksw.gerbil.semantic.subclass;

public class SimpleClassNodeFactory implements ClassNodeFactory<SimpleClassNode> {

    @Override
    public SimpleClassNode createNode(String uri) {
        return new SimpleClassNode(uri);
    }

    @Override
    public void updateNode(ClassNode oldNode) {
        // nothing to do
    }

}
