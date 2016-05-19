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
package org.aksw.gerbil.dataset.impl.nif;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aksw.gerbil.annotator.decorator.ErrorCountingAnnotatorDecorator;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.impl.ConfidenceBasedFMeasureCalculator;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.matching.impl.MatchingsCounterImpl;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class OKEChallengeTask1DatasetTest {

    @BeforeClass
    public static void setMatchingsCounterDebugFlag() {
        MatchingsCounterImpl.setPrintDebugMsg(false);
        ConfidenceBasedFMeasureCalculator.setPrintDebugMsg(false);
        ErrorCountingAnnotatorDecorator.setPrintDebugMsg(false);
    
    }

    private static final String TASK1_FILE = "src/test/resources/OKE_Challenge/example_data/task1.ttl";
    private static final Document EXPECTED_DOCUMENTS[] = new Document[] {
            new DocumentImpl(
                    "Florence May Harding studied at a school in Sydney, and with Douglas Robert Dundas , but in effect had no formal training in either botany or art.",
                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-1",
                    Arrays.asList(
                            (Marking) new TypedNamedEntity(
                                    0,
                                    20,
                                    new HashSet<String>(
                                            Arrays.asList("http://dbpedia.org/resource/Florence_May_Harding",
                                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Florence_May_Harding")),
                                    new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
                            (Marking) new NamedEntity(34, 6,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/National_Art_School"),
                            (Marking) new TypedNamedEntity(44, 6, new HashSet<String>(Arrays.asList(
                                    "http://dbpedia.org/resource/Sydney",
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Sydney")),
                                    new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                            "http://ontologydesignpatterns.org/ont/wikipedia/d0.owl#Location"))),
                            (Marking) new TypedNamedEntity(
                                    61,
                                    21,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Douglas_Robert_Dundas",
                                    new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))))),
            new DocumentImpl(
                    "Such notables include James Carville, who was the senior political adviser to Bill Clinton, and Donna Brazile, the campaign manager of the 2000 presidential campaign of Vice-President Al Gore.",
                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-2",
                    Arrays.asList(
                            (Marking) new TypedNamedEntity(22, 14, new HashSet<String>(Arrays.asList(
                                    "http://dbpedia.org/resource/James_Carville",
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/James_Carville")),
                                    new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
                            (Marking) new TypedNamedEntity(
                                    57,
                                    17,
                                    new HashSet<String>(
                                            Arrays.asList("http://dbpedia.org/resource/Political_consulting",
                                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Political_adviser")),
                                    new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Role"))),
                            (Marking) new TypedNamedEntity(78, 12, new HashSet<String>(Arrays.asList(
                                    "http://dbpedia.org/resource/Bill_Clinton",
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Bill_Clinton")),
                                    new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
                            (Marking) new TypedNamedEntity(96, 13, new HashSet<String>(Arrays.asList(
                                    "http://dbpedia.org/resource/Donna_Brazile",
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Donna_Brazile")),
                                    new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
                            (Marking) new TypedNamedEntity(
                                    115,
                                    16,
                                    new HashSet<String>(
                                            Arrays.asList("http://dbpedia.org/resource/Campaign_manager",
                                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Campaign_manager")),
                                    new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Role"))),
                            (Marking) new TypedNamedEntity(184, 7, new HashSet<String>(Arrays.asList(
                                    "http://dbpedia.org/resource/Al_Gore",
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Al_Gore")),
                                    new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))))),
            new DocumentImpl(
                    "The senator received a Bachelor of Laws from the Columbia University.",
                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-3",
                    Arrays.asList(
                            (Marking) new TypedNamedEntity(
                                    4,
                                    7,
                                    new HashSet<String>(
                                            Arrays.asList("http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Senator_1")),
                                    new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
                            (Marking) new TypedNamedEntity(
                                    49,
                                    19,
                                    new HashSet<String>(
                                            Arrays.asList("http://dbpedia.org/resource/Columbia_University",
                                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Columbia_University")),
                                    new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organization"))))) };

    @Test
    public void test() throws GerbilException {
        DatasetConfiguration datasetConfig = new NIFFileDatasetConfig("OKE_Task1", TASK1_FILE, false,
                ExperimentType.A2KB, null, null);
        Dataset dataset = datasetConfig.getDataset(ExperimentType.A2KB);

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
                    + document.getMarkings().toString() + ").", expectedDoc.getMarkings().size(), document
                    .getMarkings().size());
            expectedMarkings = new HashSet<Marking>(expectedDoc.getMarkings());
            for (Marking marking : document.getMarkings()) {
                Assert.assertTrue("Couldn't find the read marking (" + marking + ") in the list of expected markings ("
                        + expectedMarkings.toString() + ").", expectedMarkings.contains(marking));
            }
        }
    }
}
