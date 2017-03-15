package org.aksw.gerbil.dataset.impl.qald;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.qa.QAUtils;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.LoaderController;

/**
 * This {@link Dataset} represents a QALD dataset.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class QALDDataset extends AbstractDataset implements InitializableDataset {

    private static final String QALD_DERIVE_EDNPOINT = "org.aksw.gerbil.dataset.impl.qald.deriveUri";
	protected List<Document> instances;
    protected String qaldDatasetName;
    protected String deriveUri;
//	private String questionLanguage;

    
    /**
     * Constructor taking the name of the QALD dataset. Note that the name
     * should be a value of the {@link org.aksw.qa.commons.load.Dataset} enum.
     * 
     * @param qaldDatasetName
     */
    public QALDDataset(String qaldDatasetName) {
        super(qaldDatasetName);
        this.qaldDatasetName = qaldDatasetName;
    
        deriveUri = GerbilConfiguration.getInstance().getString(QALD_DERIVE_EDNPOINT);
//        this.questionLanguage= questionLanguage;
    }

//    private void initLanguage(){
//    	this.questionLanguage = "en";
//    }

    
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
//            List<IQuestion> questions = QALD_Loader.load(datasetId);
        	
        	List<IQuestion> questions = LoaderController.load(datasetId, deriveUri, qLang);
            if (questions == null) {
                throw new GerbilException("Couldn't load questions of QALD dataset " + datasetId.toString() + ".",
                        ErrorTypes.DATASET_LOADING_ERROR);
            }

            String questionUriPrefix;
            try {
                questionUriPrefix = "http://qa.gerbil.aksw.org/" + URLEncoder.encode(getName(), "UTF-8") + "/question#";
            } catch (UnsupportedEncodingException e) {
                throw new GerbilException("Severe error while trying to encode dataset name.", e,
                        ErrorTypes.DATASET_LOADING_ERROR);
            }

            instances = new ArrayList<Document>(questions.size());
            Document document;
            for (IQuestion question : questions) {
            	if(question.getOutOfScope()!=null && question.getOutOfScope()){
            		continue;
            	}
                document = QAUtils.translateQuestion(question, questionUriPrefix + question.getId(), qLang);
                if (document != null) {
                    instances.add(document);
                }
            }
        }
    }

}
