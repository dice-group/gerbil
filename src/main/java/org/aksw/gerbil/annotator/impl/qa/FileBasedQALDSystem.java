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
import org.aksw.qa.commons.load.json.EJQuestionFactory;
import org.aksw.qa.commons.load.json.ExtendedQALDJSONLoader;

public class FileBasedQALDSystem extends InstanceListBasedAnnotator implements QASystem {

	protected static List<Document> loadInstances(final List<String> qaldFiles, final List<String> questionUriPrefixes, final String qLang) throws GerbilException {
		List<Document> instances = new ArrayList<>();
		for (int i = 0; i < qaldFiles.size(); ++i) {
			loadInstances(qaldFiles.get(i), questionUriPrefixes.get(i), instances, qLang);
		}
		return instances;
	}

	protected static void loadInstances(final String qaldFile, final String questionUriPrefix, final List<Document> instances, final String qLang) throws GerbilException {

		List<IQuestion> questions = null;
		try {
			Object json = ExtendedQALDJSONLoader.readJson(new File(qaldFile));
			questions = EJQuestionFactory.getQuestionsFromJson(json);
		} catch (Exception e) {
			throw new GerbilException(e, ErrorTypes.UNEXPECTED_EXCEPTION);
		}

		for (IQuestion question : questions) {
			instances.add(QAUtils.translateQuestion(question, questionUriPrefix + question.getId(), qLang));
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
	public FileBasedQALDSystem(final List<String> qaldFiles, final List<String> questionUriPrefixes, final String questionLanguage) throws GerbilException {
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
	public FileBasedQALDSystem(final String name, final List<String> qaldFiles, final List<String> questionUriPrefixes, final String questionLanguage) throws GerbilException {
		super(name, loadInstances(qaldFiles, questionUriPrefixes, questionLanguage), questionLanguage);
	}

}
