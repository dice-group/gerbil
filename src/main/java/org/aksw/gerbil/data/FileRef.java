package org.aksw.gerbil.data;

import java.io.File;

import org.aksw.gerbil.transfer.nif.Marking;

public interface FileRef extends Marking {

    public File getFileRef();
}
