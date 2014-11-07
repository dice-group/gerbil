package org.aksw.gerbil.transfer;

import java.util.List;


public class UploadFileContainer {
    private List<FileMeta> files;

    public UploadFileContainer(List<FileMeta> files) {
        this.files = files;
    }

    public List<FileMeta> getFiles() {
        return files;
    }

    public void setFiles(List<FileMeta> files) {
        this.files = files;
    }
}
