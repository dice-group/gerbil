package org.aksw.gerbil.bat.datasets;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileBasedNIFDataset extends AbstractNIFDataset {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileBasedNIFDataset.class);

    private String filePath;
    private Lang language;

    public FileBasedNIFDataset(String filePath, String name, Lang language) {
        super(name);
        this.filePath = filePath;
        this.language = language;
        init();
    }

    @Override
    protected InputStream getDataAsInputStream() {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            LOGGER.error("Couldn't load NIF dataset from file.", e);
        }
        return fin;
    }

    @Override
    protected Lang getDataLanguage() {
        return language;
    }

}
