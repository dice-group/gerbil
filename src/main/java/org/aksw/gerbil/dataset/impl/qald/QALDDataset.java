package org.aksw.gerbil.dataset.impl.qald;

import java.util.List;

import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.QALD_Loader;

/**
 * This {@link Dataset} represents a QALD dataset.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class QALDDataset extends AbstractDataset implements InitializableDataset {

    protected List<Document> instances;
    protected String qaldDatasetName;

    /**
     * Constructor taking the name of the QALD dataset. Note that the name
     * should be a value of the {@link org.aksw.qa.commons.load.Dataset} enum.
     * 
     * @param qaldDatasetName
     */
    public QALDDataset(String qaldDatasetName) {
        this.qaldDatasetName = qaldDatasetName;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public List<Document> getInstances() {
        return instances;
    }

    @Override
    public void init() throws GerbilException {
        org.aksw.qa.commons.load.Dataset datasetId = null;
        try {
            datasetId = org.aksw.qa.commons.load.Dataset.valueOf(qaldDatasetName);
        } catch (Exception e) {
            throw new GerbilException(
                    "The name \"" + qaldDatasetName
                            + "\" of this QALD dataset is not a value of the org.aksw.qa.commons.load.Dataset enum.",
                    e, ErrorTypes.DATASET_LOADING_ERROR);
        }
        if (datasetId == null) {
            throw new GerbilException(
                    "The name \"" + qaldDatasetName
                            + "\" of this QALD dataset is not a value of the org.aksw.qa.commons.load.Dataset enum.",
                    ErrorTypes.DATASET_LOADING_ERROR);
        } else {
            List<IQuestion> questions = QALD_Loader.load(datasetId);
            if (questions == null) {
                throw new GerbilException("Couldn't load questions of QALD dataset " + datasetId.toString() + ".",
                        ErrorTypes.DATASET_LOADING_ERROR);
            }
            
        }
    }

}
