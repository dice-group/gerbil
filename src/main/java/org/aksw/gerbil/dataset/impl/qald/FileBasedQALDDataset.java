package org.aksw.gerbil.dataset.impl.qald;

import java.io.File;
import java.io.FileInputStream;
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
import org.aksw.gerbil.qa.QALDStreamType;
import org.aksw.gerbil.qa.QALDStreamUtils;
import org.aksw.gerbil.qa.QAUtils;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.LoaderController;
import org.aksw.qa.commons.load.json.EJQuestionFactory;
import org.aksw.qa.commons.load.json.ExtendedQALDJSONLoader;
import org.apache.commons.io.IOUtils;

/**
 * This {@link Dataset} represents a QALD dataset.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class FileBasedQALDDataset extends AbstractDataset implements InitializableDataset {

    private static final String QUESTION_LANGUAGE_KEY = "org.aksw.gerbil.dataset.question.language";
	protected List<Document> instances;
    protected String file;
    protected QALDStreamType fileType;

    public FileBasedQALDDataset(String file){
    	this.file=file;
    	initLanguage();
    }
    public FileBasedQALDDataset(String name, String file){
    	super(name);
    	this.file=file;
    	initLanguage();
    }
    
    public FileBasedQALDDataset(String file, QALDStreamType fileType) {
        this.file = file;
        this.fileType = fileType;
        initLanguage();
    }

    public FileBasedQALDDataset(String name, String file, QALDStreamType fileType) {
        super(name);
        this.file = file;
        this.fileType = fileType;
        initLanguage();
    }
    public FileBasedQALDDataset(String name, String file, String questionLanguage) {
        super(name);
        this.file = file;
        this.fileType = fileType;
        this.qLang=questionLanguage;
    }
    
    private void initLanguage(){
    	this.qLang = "en";
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
            if(fileType!=null){
            	instances = QALDStreamUtils.parseDocument(fin, fileType, getName());
            }
            else{
        		List<IQuestion> questions;
        		//JSON 
        		questions = EJQuestionFactory.getQuestionsFromJson(ExtendedQALDJSONLoader.readJson(new File(file)));
        		if(questions==null){
        			//XML
        			questions = LoaderController.loadXML(fin, null, qLang);
        		}
        		instances = generateInstancesFromQuestions(getName(), questions, qLang);
            }
        } catch (Exception e) {
        	IOUtils.closeQuietly(fin);
        	throw new GerbilException("Couldn't load the given QALD file.", e, ErrorTypes.DATASET_LOADING_ERROR);
        }
        IOUtils.closeQuietly(fin);
    }

    
    private static List<Document> generateInstancesFromQuestions(String adapterName, List<IQuestion> questions, String questionLanguage){
    	String questionUriPrefix;
		try {
			questionUriPrefix = "http://qa.gerbil.aksw.org/"
					+ URLEncoder.encode(adapterName, "UTF-8") + "/question#";
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(
					"Severe error while trying to encode adapter name.", e);
		}

		List<Document> instances = new ArrayList<Document>(questions.size());
		for (IQuestion question : questions) {
			instances.add(QAUtils.translateQuestion(question, questionUriPrefix
					+ question.getId(), questionLanguage));
		}
		return instances;
    }
}