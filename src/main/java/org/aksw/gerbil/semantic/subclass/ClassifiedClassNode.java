package org.aksw.gerbil.semantic.subclass;

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
}
