package org.aksw.gerbil.annotator.impl.qa;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.annotator.QASystem;
import org.aksw.gerbil.annotator.impl.instance.InstanceListBasedAnnotator;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.qa.QAUtils;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.json.EJQuestionFactory;
import org.aksw.qa.commons.load.json.ExtendedQALDJSONLoader;

public class FileBasedQALDSystem extends InstanceListBasedAnnotator implements
		QASystem {

	protected static List<Document> loadInstances(List<String> qaldFiles,
			List<String> questionUriPrefixes, String qLang) throws GerbilException {
		List<Document> instances = new ArrayList<Document>();
		for (int i = 0; i < qaldFiles.size(); ++i) {
			loadInstances(qaldFiles.get(i), questionUriPrefixes.get(i),
					instances, qLang);
		}
		return instances;
	}

	protected static void loadInstances(String qaldFile,
			String questionUriPrefix, List<Document> instances, String qLang)
			throws GerbilException {

		List<IQuestion> questions = null;
		Object json;
		try {
			json = ExtendedQALDJSONLoader.readJson(new File(qaldFile));
		} catch (IOException e) {
			throw new GerbilException(ErrorTypes.DATASET_LOADING_ERROR);
		}
		questions = EJQuestionFactory.getQuestionsFromJson(json);
		
		for (IQuestion question : questions) {
			instances.add(QAUtils.translateQuestion(question, questionUriPrefix
					+ question.getId(), qLang));
		}
	}


	/**
	 * Constructor.
	 * 
	 * @param qaldFiles
	 *            the QALD files that contain the responses of this annotator
	 * @param questionUriPrefixes
	 *            the URI prefixes that are used for the single QALD files
	 * @throws GerbilException
	 *             if one of the given files can not be loaded correctly
	 */
	public FileBasedQALDSystem(List<String> qaldFiles,
			List<String> questionUriPrefixes, String questionLanguage) throws GerbilException {
		this(null, qaldFiles, questionUriPrefixes, questionLanguage);
	}

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            the name of the annotator
	 * @param qaldFiles
	 *            the QALD files that contain the responses of this annotator
	 * @param questionUriPrefixes
	 *            the URI prefixes that are used for the single QALD files
	 * @throws GerbilException
	 *             if one of the given files can not be loaded correctly
	 */
	public FileBasedQALDSystem(String name, List<String> qaldFiles,
			List<String> questionUriPrefixes, String questionLanguage) throws GerbilException {
		super(name, loadInstances(qaldFiles, questionUriPrefixes, questionLanguage), questionLanguage);
	}


}
