package org.aksw.gerbil.annotator.impl.qa;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.qa.datatypes.AnswerSet;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.LoaderController;
import org.aksw.qa.commons.load.json.EJQuestionFactory;
import org.aksw.qa.commons.load.json.ExtendedJson;
import org.aksw.qa.commons.load.json.ExtendedQALDJSONLoader;
import org.junit.Assert;
import org.junit.Test;

public class QanaryWebServiceTest {

	private static final String QANARY_URI = "https://wdaqua-qanary.univ-st-etienne.fr/gerbil-execute/wdaqua-core0,%20QueryExecuter/";

	// protected Semaphore taskEndedMutex = new Semaphore(0);

	protected ExtendedQALDBasedWebService service = new ExtendedQALDBasedWebService(QANARY_URI);

	private void answer(final List<IQuestion> iquestions) throws GerbilException {

		for (IQuestion iquestion : iquestions) {
			Set<String> goldenStandard = iquestion.getGoldenAnswers();
			Iterator it = goldenStandard.iterator();
			String question = iquestion.getLanguageToQuestion().get("en");

			Document document = new DocumentImpl();
			document.setText(question);
			List<Marking> annotationAnswers = service.answerQuestion(document, "en");
			AnswerSet<String> answers = null;
			for (Marking m : annotationAnswers) {
				if (m instanceof AnswerSet<?>) {
					answers = (AnswerSet) m;
					System.out.println("Question : " + question);
					System.out.println("Annotator answer: " + answers.getAnswers().toString());
					System.out.println("Golden Std: " + goldenStandard.toString());
					Assert.assertTrue("Annotator answers and golden answers dont match ", answers.getAnswers().equals(goldenStandard));
				}

			}
		}
	}

	public void answerXML(final InputStream fis) throws GerbilException {
		List<IQuestion> iquestions = LoaderController.loadXML(fis, "en");
		answer(iquestions);
	}

	public void answerJSON(final InputStream fis) throws GerbilException {
		ExtendedJson exJson;
		try {
			exJson = (ExtendedJson) ExtendedQALDJSONLoader.readJson(fis, ExtendedJson.class);
			List<IQuestion> iquestions = EJQuestionFactory.getQuestionsFromExtendedJson(exJson);
			answer(iquestions);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void answerSetXMLBoolean() throws GerbilException, IOException {
		try (FileInputStream fis = new FileInputStream(new File("src/test/resources/QALD_answersSets/QALD_Boolean.xml"))) {
			answerXML(fis);
		}
	}

	@Test
	public void answerSetXMLDate() throws GerbilException, IOException {
		try (FileInputStream fis = new FileInputStream(new File("src/test/resources/QALD_answersSets/QALD_Date.xml"))) {
			answerXML(fis);
		}
	}

	@Test
	public void answerSetXMLNumber() throws GerbilException, IOException {
		try (FileInputStream fis = new FileInputStream(new File("src/test/resources/QALD_answersSets/QALD_Number.xml"))) {
			answerXML(fis);
		}
	}

	@Test
	public void answerSetXMLString() throws GerbilException, IOException {
		try (FileInputStream fis = new FileInputStream(new File("src/test/resources/QALD_answersSets/QALD_String.xml"))) {
			answerXML(fis);
		}
	}

	@Test
	public void answerSetXMLResource() throws GerbilException, IOException {
		try (FileInputStream fis = new FileInputStream(new File("src/test/resources/QALD_answersSets/QALD_Resources.xml"))) {
			answerXML(fis);
		}
	}

	// @Test
	public void answerSetJSONBoolean() throws GerbilException, IOException {
		try (FileInputStream fis = new FileInputStream(new File("src/test/resources/QALD_answersSets/QALD_Boolean.json"))) {
			answerJSON(fis);
		}
	}

	// @Test
	public void answerSetJSONDate() throws GerbilException, IOException {
		try (FileInputStream fis = new FileInputStream(new File("src/test/resources/QALD_answersSets/QALD_Date.json"))) {
			answerJSON(fis);
		}
	}

	// @Test
	public void answerSetJSONNumber() throws GerbilException, IOException {
		try (FileInputStream fis = new FileInputStream(new File("src/test/resources/QALD_answersSets/QALD_Number.json"))) {
			answerJSON(fis);
		}
	}

	// @Test
	public void answerSetJSONString() throws GerbilException, IOException {
		try (FileInputStream fis = new FileInputStream(new File("src/test/resources/QALD_answersSets/QALD_String.json"))) {
			answerJSON(fis);
		}
	}

	// @Test
	public void answerSetJSONResource() throws GerbilException, IOException {
		try (FileInputStream fis = new FileInputStream(new File("src/test/resources/QALD_answersSets/QALD_Resources.json"))) {
			answerJSON(fis);
		}
	}

	//
	// @Override
	// public void reportTaskFinished(Task task) {
	// taskEndedMutex.release();
	// }
	//
	// @Override
	// public void reportTaskThrowedException(Task task, Throwable t) {
	// taskEndedMutex.release();
	// }

}
