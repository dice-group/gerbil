package org.aksw.gerbil.dataset.impl.qald;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aksw.gerbil.annotator.decorator.ErrorCountingAnnotatorDecorator;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.dataset.DatasetConfigurationImpl;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.impl.ConfidenceBasedFMeasureCalculator;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.matching.impl.MatchingsCounterImpl;
import org.aksw.gerbil.qa.datatypes.AnswerSet;
import org.aksw.gerbil.qa.datatypes.Property;
import org.aksw.gerbil.qa.datatypes.Relation;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class FileBasedQALDDatasetTest {

    @BeforeClass
    public static void setMatchingsCounterDebugFlag() {
        MatchingsCounterImpl.setPrintDebugMsg(false);
        ConfidenceBasedFMeasureCalculator.setPrintDebugMsg(false);
        ErrorCountingAnnotatorDecorator.setPrintDebugMsg(false);

    }

    private static final String DATASET_FILE = "src/test/resources/QALD_Datasets/QALD_examples.json";
    private static final String DATASET_NAME = "test-dataset";
    private static final Document EXPECTED_DOCUMENTS[] = new Document[] {
            new DocumentImpl("Which American presidents were in office during the Vietnam War?",
                    "http://qa.gerbil.aksw.org/" + DATASET_NAME + "/question#84", Arrays.asList(
                            // Answer set containing answers
                            (Marking) new AnswerSet<Annotation>(new HashSet<Annotation>(Arrays.asList(
                                    new Annotation(new HashSet<String>(
                                            Arrays.asList("http://dbpedia.org/resource/John_F._Kennedy"))),
                                    new Annotation(new HashSet<String>(
                                            Arrays.asList("http://dbpedia.org/resource/Lyndon_B._Johnson"))),
                                    new Annotation(new HashSet<String>(
                                            Arrays.asList("http://dbpedia.org/resource/Richard_Nixon")))))),
                            // Annotations from the SPARQL query
                            (Marking) new Annotation(
                                    "http://dbpedia.org/resource/Category:Presidents_of_the_United_States"),
                            (Marking) new Annotation("http://dbpedia.org/resource/Vietnam_War"),
                            // Properties from the SPARQL query
                            (Marking) new Property("http://purl.org/dc/terms/subject"),
                            (Marking) new Property("http://dbpedia.org/ontology/commander"),
                            // Relations from the SPARQL query
                            (Marking) new Relation(null, new Property("http://purl.org/dc/terms/subject"),
                                    new Annotation(
                                            "http://dbpedia.org/resource/Category:Presidents_of_the_United_States")),
                            (Marking) new Relation(new Annotation("http://dbpedia.org/resource/Vietnam_War"),
                                    new Property("http://dbpedia.org/ontology/commander"), (Annotation) null))),
            new DocumentImpl("How many gold medals did Michael Phelps win at the 2008 Olympics?",
                    "http://qa.gerbil.aksw.org/" + DATASET_NAME + "/question#73", Arrays.asList(
                            // Answer set containing answers
                            (Marking) new AnswerSet<String>(new HashSet<String>(Arrays.asList("8"))),
                            // Annotations from the SPARQL query
                            (Marking) new Annotation("http://dbpedia.org/resource/Michael_Phelps"),
                            // Properties from the SPARQL query
                            (Marking) new Property("http://dbpedia.org/ontology/goldMedalist"),
                            // Relations from the SPARQL query
                            (Marking) new Relation(null, new Property("http://dbpedia.org/ontology/goldMedalist"),
                                    new Annotation("http://dbpedia.org/resource/Michael_Phelps")))),
            new DocumentImpl("Are there any castles in the United States?",
                    "http://qa.gerbil.aksw.org/" + DATASET_NAME + "/question#79", Arrays.asList(
                            // Answer set containing answers
                            (Marking) new AnswerSet<String>(new HashSet<String>(Arrays.asList("true"))),
                            // Annotations from the SPARQL query
                            (Marking) new Annotation(
                                    "http://dbpedia.org/resource/Category:Castles_in_the_United_States"),
                            // Properties from the SPARQL query
                            (Marking) new Property("http://purl.org/dc/terms/subject"),
                            // Relations from the SPARQL query
                            (Marking) new Relation(null, new Property("http://purl.org/dc/terms/subject"),
                                    new Annotation(
                                            "http://dbpedia.org/resource/Category:Castles_in_the_United_States")))) };

    @Test
    public void test() throws GerbilException, NoSuchMethodException, SecurityException {
        DatasetConfiguration datasetConfig = new DatasetConfigurationImpl(DATASET_NAME, false,
                FileBasedQALDDataset.class.getConstructor(String.class, String.class),
                new Object[] { DATASET_NAME, DATASET_FILE }, ExperimentType.QA, null, null);
        datasetConfig.setQuestionLanguage("en");
        Dataset dataset = datasetConfig.getDataset(ExperimentType.QA);

        Map<String, Document> uriInstanceMapping = new HashMap<String, Document>(EXPECTED_DOCUMENTS.length);
        for (Document document : EXPECTED_DOCUMENTS) {
            uriInstanceMapping.put(document.getDocumentURI(), document);
        }

        Document expectedDoc;
        Set<Marking> expectedMarkings;
        for (Document document : dataset.getInstances()) {
            Assert.assertTrue("Unknown document URI: " + document.getDocumentURI(),
                    uriInstanceMapping.containsKey(document.getDocumentURI()));
            expectedDoc = uriInstanceMapping.get(document.getDocumentURI());

            // check the text
            Assert.assertEquals(expectedDoc.getText(), document.getText());
            // check the markings
            Assert.assertEquals(
                    "encountered different lengths of expectedMarkings (" + expectedDoc.getMarkings().toString()
                            + ") and the markings got from the reader (" + document.getMarkings().toString() + ").",
                    expectedDoc.getMarkings().size(), document.getMarkings().size());
            expectedMarkings = new HashSet<Marking>(expectedDoc.getMarkings());
            for (Marking marking : document.getMarkings()) {
                Assert.assertTrue("Couldn't find the read marking (" + marking + ") in the list of expected markings ("
                        + expectedMarkings.toString() + ").", expectedMarkings.contains(marking));
            }
        }
    }
}
