package org.aksw.gerbil.semantic.subclass;

/**
 * A sub class inferencer shall infer all known subclasses of the given class
 * and store the resulting {@link ClassNode} objects in a {@link ClassSet}.
 * Note, that this shall include the given class itself. Thus, the set should
 * contain at least one single {@link ClassNode} object representing the given
 * class.
 * 
 * @author Michael R&ouml;der <roeder@informatik.uni-leipzig.de>
 * 
 */
public interface SubClassInferencer {

    public void inferSubClasses(String classURI, ClassSet classes, ClassNodeFactory<? extends ClassNode> factory);
}
