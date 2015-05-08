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
