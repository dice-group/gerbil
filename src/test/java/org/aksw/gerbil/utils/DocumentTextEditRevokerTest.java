package org.aksw.gerbil.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.SpanImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DocumentTextEditRevokerTest {

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        
        testConfigs.add(new Object[] { new DocumentImpl("a", Arrays.asList(new SpanImpl(0, 1))),
                new DocumentImpl(" a", Arrays.asList(new SpanImpl(1, 1))) });
        testConfigs.add(new Object[] { new DocumentImpl(" a", Arrays.asList(new SpanImpl(1, 1))),
                new DocumentImpl("a", Arrays.asList(new SpanImpl(0, 1))) });
        
        testConfigs.add(new Object[] { new DocumentImpl("ab", Arrays.asList(new SpanImpl(0, 2))),
                new DocumentImpl("a", Arrays.asList(new SpanImpl(0, 1))) });
        testConfigs.add(new Object[] { new DocumentImpl("a", Arrays.asList(new SpanImpl(0, 1))),
                new DocumentImpl("ab", Arrays.asList(new SpanImpl(0, 1))) });
        testConfigs.add(new Object[] { new DocumentImpl("a b", Arrays.asList(new SpanImpl(0, 3))),
                new DocumentImpl("a  b", Arrays.asList(new SpanImpl(0, 4))) });
        testConfigs.add(new Object[] { new DocumentImpl("a  b", Arrays.asList(new SpanImpl(0, 4))),
                new DocumentImpl("a b", Arrays.asList(new SpanImpl(0, 3))) });
        
        // example from https://github.com/dice-group/gerbil/issues/208
        testConfigs
                .add(new Object[] {
                        new DocumentImpl(
                                "From BBC News in London, I am Gregor Cragy for The World. The US Supreme Court has held a hearing to decide whether to intervene in the dispute over the presidential election results.",
                                Arrays.asList(new SpanImpl(5, 8), new SpanImpl(17, 6), new SpanImpl(30, 12),
                                        new SpanImpl(51, 5), new SpanImpl(62, 16))),
                        new DocumentImpl(
                                " From BBC News in London, I am Gregor Cragy for The World. The US Supreme Court has held a hearing to decide whether to intervene in the dispute over the presidential election results.",
                                Arrays.asList(new SpanImpl(6, 8), new SpanImpl(18, 6), new SpanImpl(31, 12),
                                        new SpanImpl(52, 5), new SpanImpl(63, 16))) });

        return testConfigs;
    }

    private Document annotatorResult;
    private Document documentWithOrigText;

    public DocumentTextEditRevokerTest(Document annotatorResult, Document documentWithOrigText) {
        super();
        this.annotatorResult = annotatorResult;
        this.documentWithOrigText = documentWithOrigText;
    }

    @Test
    public void testLevenstein() {
        annotatorResult = DocumentTextEditRevoker.revokeTextEdits(annotatorResult, documentWithOrigText.getText());
        Assert.assertEquals(documentWithOrigText, annotatorResult);
    }
}
