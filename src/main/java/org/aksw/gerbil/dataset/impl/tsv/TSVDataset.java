package org.aksw.gerbil.dataset.impl.tsv;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.qa.QAUtils;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.utils.ClosePermitionGranter;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.LoaderController;

public class TSVDataset extends AbstractDataset implements InitializableDataset {
	 protected String tsvDatasetName;
	    protected String deriveUri;
		protected List<Document> instances;

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Document> getInstances() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setClosePermitionGranter(ClosePermitionGranter granter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setQuestionLanguage(String qLang) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getQuestionLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

	 public TSVDataset(String tsvDatasetName) {
	        super(tsvDatasetName);
	        this.tsvDatasetName = tsvDatasetName;
	    
	    }

	

	@Override
	 public void init() throws GerbilException {
        org.aksw.qa.commons.load.Dataset datasetId = null;
        try {
            datasetId = org.aksw.qa.commons.load.Dataset.valueOf(tsvDatasetName);
        } catch (Exception e) {
            throw new GerbilException(
                    "The name \"" + tsvDatasetName
                            + "\" of this QALD dataset is not a value of the org.aksw.qa.commons.load.Dataset enum.",
                    e, ErrorTypes.DATASET_LOADING_ERROR);
        }
        if (datasetId == null) {
            throw new GerbilException(
                    "The name \"" + tsvDatasetName
                            + "\" of this QALD dataset is not a value of the org.aksw.qa.commons.load.Dataset enum.",
                    ErrorTypes.DATASET_LOADING_ERROR);
        } else {
//            List<IQuestion> questions = QALD_Loader.load(datasetId);
        	
			List<IQuestion> questions = LoaderController.load(datasetId);
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
