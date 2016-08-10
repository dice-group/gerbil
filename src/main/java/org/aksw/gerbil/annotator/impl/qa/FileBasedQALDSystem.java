package org.aksw.gerbil.annotator.impl.qa;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.annotator.QASystem;
import org.aksw.gerbil.annotator.impl.instance.InstanceListBasedAnnotator;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.qa.QAUtils;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.datastructure.Question;
import org.aksw.qa.commons.load.json.EJAnswers;
import org.aksw.qa.commons.load.json.EJQuestionEntry;
import org.aksw.qa.commons.load.json.EJQuestionFactory;
import org.aksw.qa.commons.load.json.ExtendedJson;
import org.aksw.qa.commons.load.json.ExtendedQALDJSONLoader;
import org.aksw.qa.commons.load.json.QaldJson;
import org.openrdf.http.protocol.error.ErrorType;

public class FileBasedQALDSystem extends InstanceListBasedAnnotator implements
		QASystem {

	protected static List<Document> loadInstances(List<String> qaldFiles,
			List<String> questionUriPrefixes) throws GerbilException {
		List<Document> instances = new ArrayList<Document>();
		for (int i = 0; i < qaldFiles.size(); ++i) {
			loadInstances(qaldFiles.get(i), questionUriPrefixes.get(i),
					instances);
		}
		return instances;
	}

	protected static void loadInstances(String qaldFile,
			String questionUriPrefix, List<Document> instances)
			throws GerbilException {

		List<IQuestion> questions = null;

		ExtendedJson exJson = (ExtendedJson) ExtendedQALDJSONLoader.readJson(
				new File(qaldFile), ExtendedJson.class);
		if (exJson != null) {
			// In extended Json format
			questions = EJQuestionFactory.getQuestionsFromExtendedJson(exJson);
		} else {
			//Not in extended Json Format, try QALD
			QaldJson json = (QaldJson) ExtendedQALDJSONLoader.readJson(
					new File(qaldFile), QaldJson.class);
			if (json == null) {
				throw new GerbilException("Unkown file format!", ErrorTypes.UNEXPECTED_EXCEPTION);
			}
			questions = EJQuestionFactory.getQuestionsFromQaldJson(json);
		}

		for (IQuestion question : questions) {
			instances.add(QAUtils.translateQuestion(question, questionUriPrefix
					+ question.getId()));
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
			List<String> questionUriPrefixes) throws GerbilException {
		this(null, qaldFiles, questionUriPrefixes);
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
			List<String> questionUriPrefixes) throws GerbilException {
		super(name, loadInstances(qaldFiles, questionUriPrefixes));
	}

}
