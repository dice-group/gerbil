package org.aksw.gerbil.data;

import java.io.File;

import org.aksw.gerbil.transfer.nif.data.AbstractMarkingImpl;

public class SimpleFileRef extends AbstractMarkingImpl implements FileRef {

    private File reference;

    public SimpleFileRef(File reference) {
        this.reference = reference;
    }

    @Override
    public File getFileRef() {
        return reference;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new SimpleFileRef(reference);
    }

}