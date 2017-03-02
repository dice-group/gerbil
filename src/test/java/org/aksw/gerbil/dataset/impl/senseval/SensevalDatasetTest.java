package org.aksw.gerbil.dataset.impl.senseval;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SensevalDatasetTest {

	@Parameters
	public static Collection<Object[]> data() {
		List<Object[]> testConfigs = new ArrayList<Object[]>();
		testConfigs
				.add(new Object[] {0, "src/test/resources/datasets/senseval/test.xml", "The art of change-ringing is peculiar to the English, and, like most English peculiarities, unintelligible to the rest of the world."
						, new String[]{"art", "change-ringing", "is", "peculiar", "English", "most", "English", "peculiarities", "unintelligible", "rest", "world"}});
		testConfigs
		.add(new Object[] {1, "src/test/resources/datasets/senseval/test.xml", "-- Dorothy L. Sayers, `` The Nine Tailors ``"
				, new String[]{"Tailors"}});
		
		testConfigs
		.add(new Object[] {2, "src/test/resources/datasets/senseval/test.xml", "ASLACTON, England"
				, new String[]{"England"}});

		return testConfigs;
	}

	private String file;
	private int docIndex;
	private String expectedSentence;
	private String[] expectedMarkings;

	public SensevalDatasetTest(int docIndex, String file,
			String expectedSentence, String[] expectedMarkings) {
		this.file = file;
		this.docIndex= docIndex;
		this.expectedSentence=expectedSentence;
		this.expectedMarkings=expectedMarkings;
	}

	@Test
	public void test() throws GerbilException, IOException {
		SensevalDataset data = new SensevalDataset(this.file);
		data.init();
		List<Document> documents = data.getInstances();
		Document doc = documents.get(docIndex);
		assertEquals(expectedSentence, doc.getText());
		List<Marking> markings = doc.getMarkings();
		String[] marks = new String[markings.size()];
		for(int i=0; i<markings.size();i++){
			NamedEntity entity = ((NamedEntity)markings.get(i));
			marks[i]=doc.getText().substring(entity.getStartPosition(), 
					entity.getStartPosition()+entity.getLength());
		}
		assertArrayEquals(expectedMarkings, 
				marks);
		data.close();

	}

}
