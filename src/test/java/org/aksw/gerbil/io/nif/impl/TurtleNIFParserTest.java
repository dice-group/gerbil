package org.aksw.gerbil.io.nif.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.aksw.gerbil.io.nif.NIFParser;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

@RunWith(Parameterized.class)
public class TurtleNIFParserTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TurtleNIFParserTest.class);

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();

        testConfigs
                .add(new Object[] {
                        "task1.ttl",
                        new Document[] {
                                new DocumentImpl(
                                        "Florence May Harding studied at a school in Sydney, and with Douglas Robert Dundas , but in effect had no formal training in either botany or art.",
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-1",
                                        Arrays.asList(
                                                (Marking) new TypedNamedEntity(
                                                        0,
                                                        20,
                                                        new HashSet<String>(
                                                                Arrays.asList(
                                                                        "http://dbpedia.org/resource/Florence_May_Harding",
                                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Florence_May_Harding")),
                                                        new HashSet<String>(
                                                                Arrays.asList(
                                                                        "http://www.w3.org/2002/07/owl#Individual",
                                                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
                                                (Marking) new NamedEntity(34, 6,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/National_Art_School"),
                                                (Marking) new TypedNamedEntity(
                                                        44,
                                                        6,
                                                        new HashSet<String>(
                                                                Arrays.asList("http://dbpedia.org/resource/Sydney",
                                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Sydney")),
                                                        new HashSet<String>(
                                                                Arrays.asList(
                                                                        "http://www.w3.org/2002/07/owl#Individual",
                                                                        "http://ontologydesignpatterns.org/ont/wikipedia/d0.owl#Location"))),
                                                (Marking) new TypedNamedEntity(
                                                        61,
                                                        21,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Douglas_Robert_Dundas",
                                                        new HashSet<String>(
                                                                Arrays.asList(
                                                                        "http://www.w3.org/2002/07/owl#Individual",
                                                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))))),
                                new DocumentImpl(
                                        "Such notables include James Carville, who was the senior political adviser to Bill Clinton, and Donna Brazile, the campaign manager of the 2000 presidential campaign of Vice-President Al Gore.",
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-2",
                                        Arrays.asList(
                                                (Marking) new TypedNamedEntity(
                                                        22,
                                                        14,
                                                        new HashSet<String>(
                                                                Arrays.asList(
                                                                        "http://dbpedia.org/resource/James_Carville",
                                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/James_Carville")),
                                                        new HashSet<String>(
                                                                Arrays.asList(
                                                                        "http://www.w3.org/2002/07/owl#Individual",
                                                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
                                                (Marking) new TypedNamedEntity(
                                                        57,
                                                        17,
                                                        new HashSet<String>(
                                                                Arrays.asList(
                                                                        "http://dbpedia.org/resource/Political_consulting",
                                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Political_adviser")),
                                                        new HashSet<String>(
                                                                Arrays.asList(
                                                                        "http://www.w3.org/2002/07/owl#Individual",
                                                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Role"))),
                                                (Marking) new TypedNamedEntity(
                                                        78,
                                                        12,
                                                        new HashSet<String>(
                                                                Arrays.asList(
                                                                        "http://dbpedia.org/resource/Bill_Clinton",
                                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Bill_Clinton")),
                                                        new HashSet<String>(
                                                                Arrays.asList(
                                                                        "http://www.w3.org/2002/07/owl#Individual",
                                                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
                                                (Marking) new TypedNamedEntity(
                                                        96,
                                                        13,
                                                        new HashSet<String>(
                                                                Arrays.asList(
                                                                        "http://dbpedia.org/resource/Donna_Brazile",
                                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Donna_Brazile")),
                                                        new HashSet<String>(
                                                                Arrays.asList(
                                                                        "http://www.w3.org/2002/07/owl#Individual",
                                                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
                                                (Marking) new TypedNamedEntity(
                                                        115,
                                                        16,
                                                        new HashSet<String>(
                                                                Arrays.asList(
                                                                        "http://dbpedia.org/resource/Campaign_manager",
                                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Campaign_manager")),
                                                        new HashSet<String>(
                                                                Arrays.asList(
                                                                        "http://www.w3.org/2002/07/owl#Individual",
                                                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Role"))),
                                                (Marking) new TypedNamedEntity(
                                                        184,
                                                        7,
                                                        new HashSet<String>(
                                                                Arrays.asList("http://dbpedia.org/resource/Al_Gore",
                                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Al_Gore")),
                                                        new HashSet<String>(
                                                                Arrays.asList(
                                                                        "http://www.w3.org/2002/07/owl#Individual",
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
                                                        new HashSet<String>(
                                                                Arrays.asList(
                                                                        "http://www.w3.org/2002/07/owl#Individual",
                                                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
                                                (Marking) new TypedNamedEntity(
                                                        49,
                                                        19,
                                                        new HashSet<String>(
                                                                Arrays.asList(
                                                                        "http://dbpedia.org/resource/Columbia_University",
                                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Columbia_University")),
                                                        new HashSet<String>(
                                                                Arrays.asList(
                                                                        "http://www.w3.org/2002/07/owl#Individual",
                                                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organization"))))) } });
        testConfigs
                .add(new Object[] {
                        "task2.ttl",
                        new Document[] {
                                new DocumentImpl(
                                        "Brian Banner is a fictional villain from the Marvel Comics Universe created by Bill Mantlo and Mike Mignola and first appearing in print in late 1985.",
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
                                                                OWL.Class.getURI()))))),
                                new DocumentImpl(
                                        "Avex Group Holdings Inc., listed in the Tokyo Stock Exchange as 7860 and abbreviated as AGHD, is the holding company for a group of entertainment-related subsidiaries based in Japan.",
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
                                                                OWL.Class.getURI()))))) } });
        return testConfigs;
    }

    private String datasetResourceName;
    private Document expectedDocuments[];

    public TurtleNIFParserTest(String datasetResourceName, Document[] expectedDocuments) {
        this.datasetResourceName = datasetResourceName;
        this.expectedDocuments = expectedDocuments;
    }

    @Test
    public void test() throws IOException {
        NIFParser parser = new TurtleNIFParser();

        Map<String, Document> expDocsMapping = new HashMap<String, Document>();
        for (int i = 0; i < expectedDocuments.length; ++i) {
            expDocsMapping.put(expectedDocuments[i].getDocumentURI(), expectedDocuments[i]);
        }

        List<Document> documentList;
        // read the initial dataset
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(datasetResourceName);
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
