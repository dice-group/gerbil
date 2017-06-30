package org.aksw.gerbil.annotator.impl.qa;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.http.HttpManagement;
import org.aksw.gerbil.qa.QAUtils;
import org.aksw.gerbil.qa.datatypes.AnswerSet;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.LoaderController;
import org.aksw.qa.commons.load.json.EJQuestionFactory;
import org.aksw.qa.commons.load.json.ExtendedJson;
import org.aksw.qa.commons.load.json.ExtendedQALDJSONLoader;
import org.aksw.simba.topicmodeling.concurrent.tasks.Task;
import org.aksw.simba.topicmodeling.concurrent.tasks.TaskObserver;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.connect.SocketConnection;

public class QanaryWebServiceTest {

	private static final String QANARY_URI = "http://wdaqua-qanary.univ-st-etienne.fr/gerbil";

	// protected Semaphore taskEndedMutex = new Semaphore(0);

	protected ExtendedQALDBasedWebService service = new ExtendedQALDBasedWebService(QANARY_URI);

	private void answer(List<IQuestion> iquestions) throws GerbilException {
		for (IQuestion iquestion : iquestions) {
			Set<String> goldenStandard = iquestion.getGoldenAnswers();
			Iterator it = goldenStandard.iterator();
			String question = iquestion.getLanguageToQuestion().get("en");

			Document document = new DocumentImpl();
			document.setText(question);
			List<Marking> annotationAnswers = service.answerQuestion(document, "en");
			for (Marking m : annotationAnswers) {
				AnswerSet a = (AnswerSet) m;
				String sA = (String) a.getAnswers().iterator().next();
				String sG = (String) it.next();
				System.out.println("Annotator answer: " + sA);
				System.out.println("Golden Std: " + sG);
				assertTrue(sA.equals(sG));
			}
		}
	}

	public void answerXML(InputStream fis) throws GerbilException {
		List<IQuestion> iquestions = LoaderController.loadXML(fis, "en");
		answer(iquestions);
	}

	public void answerJSON(InputStream fis) throws GerbilException {
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
