package org.aksw.gerbil.dataset.impl.qald;

import java.io.FileInputStream;
import java.util.List;

import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.qa.QALDStreamType;
import org.aksw.gerbil.qa.QALDStreamUtils;
import org.aksw.gerbil.transfer.nif.Document;
import org.apache.commons.io.IOUtils;

/**
 * This {@link Dataset} represents a QALD dataset.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class FileBasedQALDDataset extends AbstractDataset implements InitializableDataset {

    protected List<Document> instances;
    protected String file;
    protected QALDStreamType fileType;

    public FileBasedQALDDataset(String file, QALDStreamType fileType) {
        this.file = file;
        this.fileType = fileType;
    }

    public FileBasedQALDDataset(String name, String file, QALDStreamType fileType) {
        super(name);
        this.file = file;
        this.fileType = fileType;
    }

    @Override
    public int size() {
        return instances.size();
    }

    @Override
    public List<Document> getInstances() {
        return instances;
    }

    @Override
    public void init() throws GerbilException {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            instances = QALDStreamUtils.parseDocument(fin, fileType, getName());
        } catch (Exception e) {
            throw new GerbilException("Couldn't load the given QALD file.", e, ErrorTypes.DATASET_LOADING_ERROR);
        } finally {
            IOUtils.closeQuietly(fin);
        }
    }

}
