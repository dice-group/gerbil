package org.aksw.gerbil.transfer.nif;

import java.util.Arrays;
import java.util.List;

import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.SpanImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.ScoredNamedEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class NIFTransferTest {

    @Parameters
    public static List<Object[]> data() {
        return Arrays
                .asList(new Object[][] {
                        { new DocumentImpl("Dies ist ein Testtext.", "http://www.aksw.org/gerbil/test-document", Arrays
                                .asList((Marking) new SpanImpl(13, 8))) },
                        { new DocumentImpl("Dies ist ein Testtext.", "http://www.aksw.org/gerbil/test-document", Arrays
                                .asList((Marking) new SpanImpl(0, 4))) },
                        { new DocumentImpl("Dies ist ein Testtext.", "http://www.aksw.org/gerbil/test-document", Arrays
                                .asList(((Marking) new SpanImpl(0, 4)), (Marking) new SpanImpl(13, 8))) },
                        { new DocumentImpl("Dies ist ein Testtext.", "http://www.aksw.org/gerbil/test-document", Arrays
                                .asList((Marking) new NamedEntity(13, 8, "http://www.aksw.org/gerbil/testtext"))) },
                        { new DocumentImpl("Dies ist ein Testtext.", "http://www.aksw.org/gerbil/test-document", Arrays
                                .asList((Marking) new ScoredNamedEntity(13, 8, "http://www.aksw.org/gerbil/testtext",
                                        0.87))) },
                        { new DocumentImpl("<> dies ?% ist ein TästTöxt!!.",
                                "http://www.aksw.org/gerbil/test-document", Arrays.asList((Marking) new SpanImpl(3, 4),
                                        (Marking) new SpanImpl(19, 8))) },
                        { new DocumentImpl(
                                "Angelina, her father Jon, and her partner Brad never played together in the same movie.",
                                "http://www.aksw.org/gerbil/test-document", Arrays.asList(
                                        (Marking) new SpanImpl(21, 3), (Marking) new SpanImpl(0, 8),
                                        (Marking) new SpanImpl(42, 4))) },
                        { new DocumentImpl(
                                "Angelina, her father Jon, and her partner Brad never played together in the same movie.",
                                "http://www.aksw.org/gerbil/test-document", Arrays.asList((Marking) new Annotation(
                                        "http://www.aksw.org/gerbil/testtext"), (Marking) new Annotation(
                                        "http://www.aksw.org/gerbil/testtext2"), (Marking) new Annotation(
                                        "http://www.aksw.org/gerbil/testtext"))) } });
    }

    private Document document;

    public NIFTransferTest(Document document) {
        this.document = document;
    }

    @Test
    public void test() throws Exception {
        NIFDocumentCreator creator = new TurtleNIFDocumentCreator();
        String nifDocument = creator.getDocumentAsNIFString(document);

        NIFDocumentParser parser = new TurtleNIFDocumentParser();
        Document newDocument = parser.getDocumentFromNIFString(nifDocument);

        Assert.assertEquals("Documents are not the same.\ndocument 1 : " + document.toString() + "\ndocument 2 : "
                + newDocument.toString() + " NIF:\n" + nifDocument, document.getDocumentURI(),
                newDocument.getDocumentURI());
        Assert.assertEquals("Documents are not the same.\ndocument 1 : " + document.toString() + "\ndocument 2 : "
                + newDocument.toString() + " NIF:\n" + nifDocument, document.getText(), newDocument.getText());
        List<Marking> expectedMarkings = document.getMarkings();
        List<Marking> receivedMarkings = newDocument.getMarkings();
        Assert.assertEquals("Documents are not the same.\ndocument 1 : " + document.toString() + "\ndocument 2 : "
                + newDocument.toString() + " NIF:\n" + nifDocument, expectedMarkings.size(), receivedMarkings.size());
        for (Marking marking : expectedMarkings) {
            Assert.assertTrue("Documents are not the same.\ndocument 1 : " + document.toString() + "\ndocument 2 : "
                    + newDocument.toString() + " NIF:\n" + nifDocument, receivedMarkings.contains(marking));
        }
    }
}
