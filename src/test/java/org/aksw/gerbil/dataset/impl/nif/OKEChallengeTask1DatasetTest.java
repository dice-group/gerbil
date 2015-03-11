package org.aksw.gerbil.dataset.impl.nif;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.datasets.DatasetConfiguration;
import org.aksw.gerbil.datasets.NIFFileDatasetConfig;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.junit.Test;

public class OKEChallengeTask1DatasetTest {

    private static final String TASK1_FILE = "src/test/resources/OKE_Challenge/example_data/task1.ttl";
    private static final Document EXPECTED_DOCUMENTS[] = new Document[] {
            new DocumentImpl(
                    "Florence May Harding studied at a school in Sydney, and with Douglas Robert Dundas , but in effect had no formal training in either botany or art.",
                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-1",
                    Arrays.asList(
                            (Marking) new NamedEntity(0, 20,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Florence_May_Harding"),
                            (Marking) new NamedEntity(34, 6,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/National_Art_School"),
                            (Marking) new NamedEntity(44, 6,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Sydney"),
                            (Marking) new NamedEntity(61, 21,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Douglas_Robert_Dundas"))),
            new DocumentImpl(
                    "Such notables include James Carville, who was the senior political adviser to Bill Clinton, and Donna Brazile, the campaign manager of the 2000 presidential campaign of Vice-President Al Gore.",
                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-2",
                    Arrays.asList(
                            (Marking) new NamedEntity(22, 14,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/James_Carville"),
                            (Marking) new NamedEntity(57, 17,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Political_adviser"),
                            (Marking) new NamedEntity(78, 12,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Bill_Clinton"),
                            (Marking) new NamedEntity(96, 13,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Donna_Brazile"),
                            (Marking) new NamedEntity(115, 16,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Campaign_manager"),
                            (Marking) new NamedEntity(184, 7,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Al_Gore"))),

            new DocumentImpl(
                    "The senator received a Bachelor of Laws from the Columbia University.",
                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-3",
                    Arrays.asList(
                            (Marking) new NamedEntity(4, 7,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Senator_1"),
                            (Marking) new NamedEntity(49, 19,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Columbia_University"))) };

    @Test
    public void test() throws GerbilException {
        DatasetConfiguration datasetConfig = new NIFFileDatasetConfig("OKE_Task1", TASK1_FILE, false,
                ExperimentType.EExt);
        Dataset dataset = datasetConfig.getDataset(ExperimentType.EExt);

        Map<String, Document> uriInstanceMapping = new HashMap<String, Document>(EXPECTED_DOCUMENTS.length);
        for (Document document : EXPECTED_DOCUMENTS) {
            uriInstanceMapping.put(document.getDocumentURI(), document);
        }

        Document expectedDoc;
        Set<Marking> expectedMarkings;
        for (Document document : dataset.getInstances()) {
            Assert.assertTrue(uriInstanceMapping.containsKey(document.getDocumentURI()));
            expectedDoc = uriInstanceMapping.get(document.getDocumentURI());

            // check the text
            Assert.assertEquals(expectedDoc.getText(), document.getText());
            // check the markings
            Assert.assertEquals("encountered different lengths of expectedMarkings ("
                    + expectedDoc.getMarkings().toString() + ") and the markings got from the reader ("
                    + document.getMarkings().toString() + ").", expectedDoc.getMarkings().size(), document.getMarkings().size());
            expectedMarkings = new HashSet<Marking>(expectedDoc.getMarkings());
            for (Marking marking : document.getMarkings()) {
                Assert.assertTrue("Couldn't find the read marking (" + marking + ") in the list of expected markings ("
                        + expectedMarkings.toString() + ").", expectedMarkings.contains(marking));
            }
        }
    }
}
