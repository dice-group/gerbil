package org.aksw.gerbil.io.nif.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.aksw.gerbil.io.nif.NIFParser;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

public class TurtleNIFParserTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TurtleNIFParserTest.class);

    private static final String TEXTS[] = new String[] {
            "Brian Banner is a fictional villain from the Marvel Comics Universe created by Bill Mantlo and Mike Mignola and first appearing in print in late 1985.",
            "Avex Group Holdings Inc., listed in the Tokyo Stock Exchange as 7860 and abbreviated as AGHD, is the holding company for a group of entertainment-related subsidiaries based in Japan." };

    private static final String DATASET_FILE_NAME = "task2.ttl";

    @Test
    public void test() throws IOException {
        NIFParser parser = new TurtleNIFParser();

        Map<String, Document> expDocsMapping = new HashMap<String, Document>();
        expDocsMapping
                .put("http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/sentence-1",
                        new DocumentImpl(
                                TEXTS[0],
                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/sentence-1",
                                Arrays.asList(
                                        (Marking) new TypedNamedEntity(
                                                0,
                                                12,
                                                "http://dbpedia.org/resource/Brian_Banner",
                                                new HashSet<String>(
                                                        Arrays.asList(
                                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/FictionalVillain",
                                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Villain",
                                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person",
                                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Personification"))),
                                        (Marking) new TypedNamedEntity(
                                                18,
                                                17,
                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/FictionalVillain",
                                                new HashSet<String>(Arrays.asList(RDFS.Class.getURI(),
                                                        OWL.Class.getURI()))),
                                        (Marking) new TypedNamedEntity(
                                                28,
                                                7,
                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Villain",
                                                new HashSet<String>(Arrays.asList(RDFS.Class.getURI(),
                                                        OWL.Class.getURI()))))));
        expDocsMapping
                .put("http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/sentence-2",
                        new DocumentImpl(
                                TEXTS[1],
                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/sentence-2",
                                Arrays.asList(
                                        (Marking) new TypedNamedEntity(
                                                0,
                                                24,
                                                "http://dbpedia.org/resource/AVEX_Records",
                                                new HashSet<String>(
                                                        Arrays.asList(
                                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/HoldingCompany",
                                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Company",
                                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organization"))),
                                        (Marking) new TypedNamedEntity(
                                                109,
                                                7,
                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Company",
                                                new HashSet<String>(Arrays.asList(RDFS.Class.getURI(),
                                                        OWL.Class.getURI()))),
                                        (Marking) new TypedNamedEntity(
                                                101,
                                                15,
                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/HoldingCompany",
                                                new HashSet<String>(Arrays.asList(RDFS.Class.getURI(),
                                                        OWL.Class.getURI()))))));

        List<Document> documentList;
        // read the initial dataset
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(DATASET_FILE_NAME);
        Assert.assertNotNull(is);
        documentList = parser.parseNIF(is);
        IOUtils.closeQuietly(is);

        LOGGER.debug("Read {} documents from the file.", documentList.size());

        for (Document document : documentList) {
            Assert.assertTrue(
                    "The list of expected documents does not contain a document with the URI \""
                            + document.getDocumentURI() + "\".", expDocsMapping.containsKey(document.getDocumentURI()));
            checkDocuments(expDocsMapping.get(document.getDocumentURI()), document);
        }
        Assert.assertEquals(expDocsMapping.size(), documentList.size());
    }

    public void checkDocuments(Document expectedDocument, Document receivedDocument) {
        Assert.assertEquals("Documents are not the same.\ndocument 1 : " + expectedDocument.toString()
                + "\ndocument 2 : " + receivedDocument.toString(), expectedDocument.getDocumentURI(),
                receivedDocument.getDocumentURI());
        Assert.assertEquals("Documents are not the same.\ndocument 1 : " + expectedDocument.toString()
                + "\ndocument 2 : " + receivedDocument.toString(), expectedDocument.getText(),
                receivedDocument.getText());
        List<Marking> expectedMarkings = expectedDocument.getMarkings();
        List<Marking> receivedMarkings = receivedDocument.getMarkings();
        Assert.assertEquals("Documents are not the same.\ndocument 1 : " + expectedDocument.toString()
                + "\ndocument 2 : " + receivedDocument.toString(), expectedMarkings.size(), receivedMarkings.size());
        for (Marking marking : expectedMarkings) {
            Assert.assertTrue("Documents are not the same.\ndocument 1 : " + expectedDocument.toString()
                    + "\ndocument 2 : " + receivedDocument.toString() + "\nThe expected Marking " + marking
                    + " is missing.", receivedMarkings.contains(marking));
        }
    }
}
