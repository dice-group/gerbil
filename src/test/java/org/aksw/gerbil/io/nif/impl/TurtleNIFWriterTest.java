package org.aksw.gerbil.io.nif.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.gerbil.io.nif.NIFParser;
import org.aksw.gerbil.io.nif.NIFWriter;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the TurtleNIFWriter (assumes, that the {@link TurtleNIFParser} works
 * correctly).
 * 
 * @author Michael R&ouml;der <roeder@informatik.uni-leipzig.de>
 * 
 */
@RunWith(Parameterized.class)
public class TurtleNIFWriterTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TurtleNIFWriterTest.class);

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();

        testConfigs.add(new Object[] { "task1.ttl" });
        testConfigs.add(new Object[] { "task2.ttl" });
        return testConfigs;
    }

    private String datasetResourceName;

    public TurtleNIFWriterTest(String datasetResourceName) {
        this.datasetResourceName = datasetResourceName;
    }

    @Test
    public void test() throws IOException {
        NIFParser parser = new TurtleNIFParser();
        NIFWriter writer = new TurtleNIFWriter();

        List<Document> documentList;
        // read the initial dataset
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(datasetResourceName);
        Assert.assertNotNull(is);
        documentList = parser.parseNIF(is);
        IOUtils.closeQuietly(is);

        LOGGER.debug("Read {} documents from the file.", documentList.size());

        Map<String, Document> expDocsMapping = new HashMap<String, Document>();
        for (Document document : documentList) {
            expDocsMapping.put(document.getDocumentURI(), document);
        }

        // write the documents to a temporary file
        File tmpFile = File.createTempFile("tempTestFile_", ".ttl");
        OutputStream os = new FileOutputStream(tmpFile);
        writer.writeNIF(documentList, os);
        IOUtils.closeQuietly(os);

        // read the documents again
        is = new FileInputStream(tmpFile);
        documentList = parser.parseNIF(is);
        IOUtils.closeQuietly(is);

        for (Document document : documentList) {
            Assert.assertTrue(expDocsMapping.containsKey(document.getDocumentURI()));
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
                    + "\ndocument 2 : " + receivedDocument.toString(), receivedMarkings.contains(marking));
        }
    }
}
