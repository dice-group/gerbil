/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.dataset.impl.t4n;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.gerbil.dataset.impl.xml.XMLDataUtil;
import org.aksw.gerbil.dataset.impl.xml.XMLNamedEntity;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class Tweet4NeedDatasetTest {

	/*
	 * The test file contains two special cases. One marking with the URI "*null*"
	 * and two markings that are overlapping.
	 */
	private static final String TEST_TEXT_DIR = "src/test/resources/datasets/twitterneed/";
	private static final String DATASET_NAME = "t4nTestDataset";

	private static final String EXPCTD_DOC1_ID = "100000000001";
	private static final String EXPCTD_DOC1_TEXT = "John Oliver is the best anchor ever. #lwn";
	private static final String EXPCTD_DOC1_ENTITY1_URI = "http://dbpedia.org/resource/John_Oliver";
	private static final Integer EXPCTD_DOC1_ENTITY1_STARTPOS = 0;
	private static final Integer EXPCTD_DOC1_ENTITY1_LEN = 11;

	private static final String EXPCTD_DOC2_ID = "100000000002";
	private static final String EXPCTD_DOC2_TEXT = "Gerbil is the best tool ever. #qabenchmarking";
	private static final String EXPCTD_DOC2_ENTITY1_URI = "http://dbpedia.org/resource/Gerbil";
	private static final Integer EXPCTD_DOC2_ENTITY1_STARTPOS = 0;
	private static final Integer EXPCTD_DOC2_ENTITY1_LEN = 6;

	private static final String EXPCTD_DOC3_ID = "100000000003";
	private static final String EXPCTD_DOC3_TEXT = "I believe in Cristiano Ronaldo #CR7Forever";
	private static final String EXPCTD_DOC3_ENTITY1_URI = "http://dbpedia.org/resource/Cristiano_Ronaldo";
	private static final Integer EXPCTD_DOC3_ENTITY1_STARTPOS = 13;
	private static final Integer EXPCTD_DOC3_ENTITY1_LEN = 17;

	private static final Map<String, Document> DOC_MAP = new HashMap<>();
	static {
		// Generate Documents
		// Doc1
		Document doc = createTestDocument(DATASET_NAME, EXPCTD_DOC1_ID, EXPCTD_DOC1_TEXT, EXPCTD_DOC1_ENTITY1_URI,
				EXPCTD_DOC1_ENTITY1_STARTPOS, EXPCTD_DOC1_ENTITY1_LEN);
		DOC_MAP.put(doc.getDocumentURI(), doc);
		// Doc2
		doc = createTestDocument(DATASET_NAME, EXPCTD_DOC2_ID, EXPCTD_DOC2_TEXT, EXPCTD_DOC2_ENTITY1_URI,
				EXPCTD_DOC2_ENTITY1_STARTPOS, EXPCTD_DOC2_ENTITY1_LEN);
		DOC_MAP.put(doc.getDocumentURI(), doc);
		// Doc3
		doc = createTestDocument(DATASET_NAME, EXPCTD_DOC3_ID, EXPCTD_DOC3_TEXT, EXPCTD_DOC3_ENTITY1_URI,
				EXPCTD_DOC3_ENTITY1_STARTPOS, EXPCTD_DOC3_ENTITY1_LEN);
		DOC_MAP.put(doc.getDocumentURI(), doc);
	}

	public static Document createTestDocument(String dsName, String docId, String docText, String entityUri,
			Integer startPos, Integer length) {
		XMLNamedEntity entity = new XMLNamedEntity();
		entity.addUri(entityUri);
		entity.setStartPosition(startPos);
		entity.setLength(length);
		List<XMLNamedEntity> entityList = new ArrayList<>();
		entityList.add(entity);
		Document doc1 = XMLDataUtil.createDocument(dsName, docId, docText, entityList);
		return doc1;
	}

	@Test
	public void test() throws GerbilException {
		Tweet4NeedDataset dataset = new Tweet4NeedDataset(TEST_TEXT_DIR);
		dataset.setName(DATASET_NAME);
		dataset.init();
		// clone docmap
		Map<String, Document> docMap = new HashMap<>(DOC_MAP);
		Document expectedDoc;
		for (Document document : dataset.getInstances()) {
			expectedDoc = docMap.remove(document.getDocumentURI());
			if (expectedDoc != null) {
				performTest(document, expectedDoc);
			}
		}
		Assert.assertEquals(0, docMap.size());
		IOUtils.closeQuietly(dataset);
	}

	private void performTest(Document doc1, Document doc2) {
		Assert.assertEquals(doc2.getText(), doc1.getText());

		List<Marking> expectedNEs = doc2.getMarkings();
		for (Marking marking : doc1.getMarkings()) {
			Assert.assertTrue("Couldn't find " + marking.toString() + " inside " + expectedNEs.toString(),
					expectedNEs.contains(marking));
		}
		Assert.assertEquals(expectedNEs.size(), doc1.getMarkings().size());
	}

	public static void main(String[] args) throws GerbilException {
		Tweet4NeedDatasetTest test = new Tweet4NeedDatasetTest();
		test.test();
	}
}
