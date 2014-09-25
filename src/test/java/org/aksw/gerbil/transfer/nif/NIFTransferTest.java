package org.aksw.gerbil.transfer.nif;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.aksw.gerbil.transfer.nif.data.AnnotatedDocumentImpl;
import org.aksw.gerbil.transfer.nif.data.AnnotationImpl;
import org.aksw.gerbil.transfer.nif.data.DisambiguatedAnnotation;
import org.aksw.gerbil.transfer.nif.data.ScoredDisambigAnnotation;
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
                        { new AnnotatedDocumentImpl("Dies ist ein Testtext.",
                                "http://www.aksw.org/gerbil/test-document",
                                Arrays.asList((Annotation) new AnnotationImpl(13, 8))) },
                        { new AnnotatedDocumentImpl("Dies ist ein Testtext.",
                                "http://www.aksw.org/gerbil/test-document",
                                Arrays.asList((Annotation) new AnnotationImpl(0, 4))) },
                        { new AnnotatedDocumentImpl("Dies ist ein Testtext.",
                                "http://www.aksw.org/gerbil/test-document",
                                Arrays.asList(((Annotation) new AnnotationImpl(0, 4)),
                                        (Annotation) new AnnotationImpl(13, 8))) },
                        { new AnnotatedDocumentImpl("Dies ist ein Testtext.",
                                "http://www.aksw.org/gerbil/test-document",
                                Arrays.asList((Annotation) new DisambiguatedAnnotation(13, 8,
                                        "http://www.aksw.org/gerbil/testtext"))) },
                        { new AnnotatedDocumentImpl("Dies ist ein Testtext.",
                                "http://www.aksw.org/gerbil/test-document",
                                Arrays.asList((Annotation) new ScoredDisambigAnnotation(13, 8,
                                        "http://www.aksw.org/gerbil/testtext", 0.87))) },
                        { new AnnotatedDocumentImpl("<> dies ?% ist ein TästTöxt!!.",
                                "http://www.aksw.org/gerbil/test-document",
                                Arrays.asList((Annotation) new AnnotationImpl(3, 4),
                                        (Annotation) new ScoredDisambigAnnotation(19, 8,
                                                "http://www.aksw.org/gerbil/testtext", 0.87))) } });
    }

    private AnnotatedDocument document;

    public NIFTransferTest(AnnotatedDocument document) {
        this.document = document;
    }

    @Test
    public void test() throws Exception {
        NIFDocumentCreator creator = new TurtleNIFDocumentCreator();
        String nifDocument = creator.getDocumentAsNIFString(document);

        NIFDocumentParser parser = new TurtleNIFDocumentParser();
        AnnotatedDocument newDocument = parser.getDocumentFromNIFString(nifDocument);

        Assert.assertEquals("Documents are not the same. NIF:\n" + nifDocument, document, newDocument);
    }
}
