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
        testConfigs.add(new Object[] { new DocumentImpl("a", Arrays.asList(new SpanImpl(0, 1))),
                new DocumentImpl("                                          a", Arrays.asList(new SpanImpl(42, 1))) });

        testConfigs.add(new Object[] { new DocumentImpl("ab", Arrays.asList(new SpanImpl(0, 2))),
                new DocumentImpl("a", Arrays.asList(new SpanImpl(0, 1))) });
        testConfigs.add(new Object[] { new DocumentImpl("a", Arrays.asList(new SpanImpl(0, 1))),
                new DocumentImpl("ab", Arrays.asList(new SpanImpl(0, 1))) });
        testConfigs.add(new Object[] { new DocumentImpl("a b", Arrays.asList(new SpanImpl(0, 3))),
                new DocumentImpl("a  b", Arrays.asList(new SpanImpl(0, 4))) });
        testConfigs.add(new Object[] { new DocumentImpl("a  b", Arrays.asList(new SpanImpl(0, 4))),
                new DocumentImpl("a b", Arrays.asList(new SpanImpl(0, 3))) });

        // example from https://github.com/dice-group/gerbil/issues/208
        testConfigs.add(new Object[] {
                new DocumentImpl(
                        "From BBC News in London, I am Gregor Cragy for The World. The US Supreme Court has held a hearing to decide whether to intervene in the dispute over the presidential election results. Judith Spencer reports on the legal wrangling that is underway. Inside the stately Supreme Court building, lawyers for Republican George W. Bush and Democrat AL Gore argued over the validity of hand re-counts in the crucial state of Florida, just as Ruth Bater Ginsburg pressed Bush's lawyer to explain why the Federal High Court should intervene in the Florida Supreme Court's move to extend the deadline for finishing the re-counts. When we read a State court decision, we should read it in the light most favorable to the integrity of the State Supreme Court. But later on, Justice Sandra Day O Connor suggested the State court might have been out of line. The legislature had very clearly said, you know, 7 days after, that's the date and it just does look like very dramatic change made by the Florida court. The US Supreme Court could rule on the matter as early as tomorrow. For The World, I am Judith Spencer in Washington. Meanwhile, the Florida Supreme Court has rejected an emergency appeal from Al Gore for an immediate re-count of thousands of disputed ballots from Palm Beach and Miami Dade counties.",
                        Arrays.asList(new SpanImpl(5, 8), new SpanImpl(17, 6), new SpanImpl(30, 12),
                                new SpanImpl(51, 5), new SpanImpl(62, 16), new SpanImpl(184, 14), new SpanImpl(267, 13),
                                new SpanImpl(314, 14), new SpanImpl(342, 7), new SpanImpl(417, 7),
                                new SpanImpl(434, 19), new SpanImpl(462, 4), new SpanImpl(495, 18),
                                new SpanImpl(538, 21), new SpanImpl(726, 19), new SpanImpl(769, 19),
                                new SpanImpl(983, 7), new SpanImpl(1002, 16), new SpanImpl(1074, 5),
                                new SpanImpl(1086, 14), new SpanImpl(1104, 10), new SpanImpl(1131, 21),
                                new SpanImpl(1191, 7), new SpanImpl(1263, 10), new SpanImpl(1278, 10))),
                new DocumentImpl(
                        " From BBC News in London, I am Gregor Cragy for The World. The US Supreme Court has held a hearing to decide whether to intervene in the dispute over the presidential election results. Judith Spencer reports on the legal wrangling that is underway.   Inside the stately Supreme Court building, lawyers for Republican George W. Bush and Democrat AL Gore argued over the validity of hand re-counts in the crucial state of Florida, just as Ruth Bater Ginsburg pressed Bush's lawyer to explain why the Federal High Court should intervene in the Florida Supreme Court's move to extend the deadline for finishing the re-counts.   When we read a State court decision, we should read it in the light most favorable to the integrity of the State Supreme Court.   But later on, Justice Sandra Day O'Connor suggested the State court might have been out of line.   The legislature had very clearly said, you know, 7 days after, that's the date and it just does look like very dramatic change made by the Florida court.   The US Supreme Court could rule on the matter as early as tomorrow. For The World, I am Judith Spencer in Washington.   Meanwhile, the Florida Supreme Court has rejected an emergency appeal from Al Gore for an immediate re-count of thousands of disputed ballots from Palm Beach and Miami Dade counties.  \n",
                        Arrays.asList(new SpanImpl(6, 8), new SpanImpl(18, 6), new SpanImpl(31, 12),
                                new SpanImpl(52, 5), new SpanImpl(63, 16), new SpanImpl(185, 14), new SpanImpl(270, 13),
                                new SpanImpl(317, 14), new SpanImpl(345, 7), new SpanImpl(420, 7),
                                new SpanImpl(437, 19), new SpanImpl(465, 4), new SpanImpl(498, 18),
                                new SpanImpl(541, 21), new SpanImpl(731, 19), new SpanImpl(776, 19),
                                new SpanImpl(992, 7), new SpanImpl(1013, 16), new SpanImpl(1085, 5),
                                new SpanImpl(1097, 14), new SpanImpl(1115, 10), new SpanImpl(1144, 21),
                                new SpanImpl(1204, 7), new SpanImpl(1276, 10), new SpanImpl(1291, 10))) });

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
