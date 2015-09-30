/**
 * This file is part of NIF transfer library for the General Entity Annotator Benchmark.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NIF transfer library for the General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.transfer.nif;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.ScoredNamedEntity;
import org.aksw.gerbil.transfer.nif.data.ScoredTypedNamedEntity;
import org.aksw.gerbil.transfer.nif.data.SpanImpl;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class NIFTransferTest {

    @Parameters
    public static List<Object[]> data() {
        List<Object[]> tests = new ArrayList<Object[]>();
        tests.add(new Object[] { new DocumentImpl("Dies ist ein Testtext.", "http://www.aksw.org/gerbil/test-document",
                Arrays.asList((Marking) new SpanImpl(13, 8))) });
        tests.add(new Object[] { new DocumentImpl("Dies ist ein Testtext.", "http://www.aksw.org/gerbil/test-document",
                Arrays.asList((Marking) new SpanImpl(0, 4))) });
        tests.add(new Object[] { new DocumentImpl("Dies ist ein Testtext.", "http://www.aksw.org/gerbil/test-document",
                Arrays.asList(((Marking) new SpanImpl(0, 4)), (Marking) new SpanImpl(13, 8))) });
        tests.add(new Object[] { new DocumentImpl("Dies ist ein Testtext.", "http://www.aksw.org/gerbil/test-document",
                Arrays.asList((Marking) new NamedEntity(13, 8, "http://www.aksw.org/gerbil/testtext"))) });
        tests.add(new Object[] { new DocumentImpl("Dies ist ein Testtext.", "http://www.aksw.org/gerbil/test-document",
                Arrays.asList((Marking) new ScoredNamedEntity(13, 8, "http://www.aksw.org/gerbil/testtext", 0.87))) });
        tests.add(new Object[] { new DocumentImpl("<> dies ?% ist ein TästTöxt!!.",
                "http://www.aksw.org/gerbil/test-document", Arrays.asList((Marking) new SpanImpl(3, 4),
                        (Marking) new SpanImpl(19, 8))) });
        tests.add(new Object[] { new DocumentImpl(
                "Angelina, her father Jon, and her partner Brad never played together in the same movie.",
                "http://www.aksw.org/gerbil/test-document", Arrays.asList((Marking) new SpanImpl(21, 3),
                        (Marking) new SpanImpl(0, 8), (Marking) new SpanImpl(42, 4))) });
        tests.add(new Object[] { new DocumentImpl(
                "Angelina, her father Jon, and her partner Brad never played together in the same movie.",
                "http://www.aksw.org/gerbil/test-document", Arrays.asList((Marking) new Annotation(
                        "http://www.aksw.org/gerbil/testtext"), (Marking) new Annotation(
                        "http://www.aksw.org/gerbil/testtext2"), (Marking) new Annotation(
                        "http://www.aksw.org/gerbil/testtext"))) });
        tests.add(new Object[] { new DocumentImpl(
                "Angelina, her father Jon, and her partner Brad never played together in the same movie.",
                "http://www.aksw.org/gerbil/test-document",
                Arrays.asList(
                        (Marking) new TypedNamedEntity(21, 3, "http://www.aksw.org/notInWiki/Jon", new HashSet<String>(
                                Arrays.asList("http://www.aksw.org/notInWiki/Person"))),
                        (Marking) new TypedNamedEntity(0, 8, new HashSet<String>(Arrays.asList(
                                "http://www.aksw.org/notInWiki/Angelina", "http://www.aksw.org/notInWiki/Angelina2")),
                                new HashSet<String>(Arrays.asList("http://www.aksw.org/notInWiki/Person",
                                        "http://www.aksw.org/notInWiki/Actor", "http://www.aksw.org/notInWiki/Mother"))),
                        (Marking) new ScoredTypedNamedEntity(42, 4, "http://www.aksw.org/notInWiki/Brad",
                                new HashSet<String>(Arrays.asList("http://www.aksw.org/notInWiki/Person")), 0.25))) });
        tests.add(new Object[] { new DocumentImpl(
                "Štvrtok is a village in Trenčín District in the Trenčín Region of north-western Slovakia.",
                "http://www.aksw.org/gerbil/test-document",
                Arrays.asList(
                        (Marking) new TypedNamedEntity(0, 7, "http://dbpedia.org/resource/Štvrtok", new HashSet<String>(
                                Arrays.asList("http://www.aksw.org/notInWiki/Village"))),
                        (Marking) new TypedNamedEntity(14, 7, new HashSet<String>(Arrays.asList(
                                "http://www.aksw.org/notInWiki/village")),
                                new HashSet<String>(Arrays.asList("http://www.aksw.org/notInWiki/Location"))),
                        (Marking) new ScoredTypedNamedEntity(79, 8, "http://dbpedia.org/resource/Slovakia",
                                new HashSet<String>(Arrays.asList("http://www.aksw.org/notInWiki/Country")), 0.25))) });
        return tests;
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
