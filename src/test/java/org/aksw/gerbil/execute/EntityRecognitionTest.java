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
package org.aksw.gerbil.execute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.annotator.TestAnnotatorConfiguration;
import org.aksw.gerbil.annotator.decorator.ErrorCountingAnnotatorDecorator;
import org.aksw.gerbil.database.SimpleLoggingResultStoringDAO4Debugging;
import org.aksw.gerbil.dataset.InstanceListBasedDataset;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.evaluate.impl.ConfidenceBasedFMeasureCalculator;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.matching.impl.MatchingsCounterImpl;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.SpanImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class EntityRecognitionTest extends AbstractExperimentTaskTest {

    @BeforeClass
    public static void setMatchingsCounterDebugFlag() {
        MatchingsCounterImpl.setPrintDebugMsg(true);
        ConfidenceBasedFMeasureCalculator.setPrintDebugMsg(true);
        ErrorCountingAnnotatorDecorator.setPrintDebugMsg(true);

    }

    private static final String TEXTS[] = new String[] { "Amy Winehouse is dead after a suspected drug overdose",
            "Angelina, her father Jon, and her partner Brad never played together in the same movie." };
    private static final Document GOLD_STD[] = new Document[] {
            new DocumentImpl(TEXTS[0], "doc-0",
                    Arrays.asList((Marking) new NamedEntity(0, 13,
                            "http://www.aksw.org/gerbil/test-document/Amy_Winehouse"))),
            new DocumentImpl(TEXTS[1], "doc-1",
                    Arrays.asList((Marking) new NamedEntity(21, 3, "http://www.aksw.org/gerbil/test-document/Jon"),
                            (Marking) new NamedEntity(0, 8, "http://www.aksw.org/gerbil/test-document/Angelina"),
                            (Marking) new NamedEntity(42, 4, "http://www.aksw.org/gerbil/test-document/Brad"))) };

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        // The recognizer found everything, but marked the word "Movie"
        // additionally.
        testConfigs.add(new Object[] {
                new Document[] { new DocumentImpl(TEXTS[0], "doc-0", Arrays.asList((Marking) new SpanImpl(0, 13))),
                        new DocumentImpl(TEXTS[1], "doc-1",
                                Arrays.asList((Marking) new SpanImpl(0, 8), (Marking) new SpanImpl(21, 3),
                                        (Marking) new SpanImpl(42, 4), (Marking) new SpanImpl(81, 5))) },
                GOLD_STD, Matching.STRONG_ANNOTATION_MATCH,
                new double[] { 1.75 / 2.0, 1.0, ((1.5 / 1.75) + 1.0) / 2.0, 0.8, 1.0, (1.6 / 1.8), 0 } });
        testConfigs.add(new Object[] {
                new Document[] { new DocumentImpl(TEXTS[0], "doc-0", Arrays.asList((Marking) new SpanImpl(0, 13))),
                        new DocumentImpl(TEXTS[1], "doc-1",
                                Arrays.asList((Marking) new SpanImpl(0, 8), (Marking) new SpanImpl(21, 3),
                                        (Marking) new SpanImpl(42, 4), (Marking) new SpanImpl(81, 5))) },
                GOLD_STD, Matching.WEAK_ANNOTATION_MATCH,
                new double[] { 1.75 / 2.0, 1.0, ((1.5 / 1.75) + 1.0) / 2.0, 0.8, 1.0, (1.6 / 1.8), 0 } });
        // The Recognizer couldn't find "Amy Winehouse" but "Winehouse". In the
        // second sentence it coudn't identify Angelina.
        testConfigs.add(new Object[] {
                new Document[] { new DocumentImpl(TEXTS[0], "doc-0", Arrays.asList((Marking) new SpanImpl(4, 9))),
                        new DocumentImpl(TEXTS[1], "doc-1",
                                Arrays.asList((Marking) new SpanImpl(21, 3), (Marking) new SpanImpl(42, 4))) },
                GOLD_STD, Matching.STRONG_ANNOTATION_MATCH,
                new double[] { 0.5, 1.0 / 3.0, (4.0 / 5.0) / 2.0, 2.0 / 3.0, 0.5, (4.0 / 7.0), 0 } });
        testConfigs.add(new Object[] {
                new Document[] { new DocumentImpl(TEXTS[0], "doc-0", Arrays.asList((Marking) new SpanImpl(4, 9))),
                        new DocumentImpl(TEXTS[1], "doc-1",
                                Arrays.asList((Marking) new SpanImpl(21, 3), (Marking) new SpanImpl(42, 4))) },
                GOLD_STD, Matching.WEAK_ANNOTATION_MATCH,
                new double[] { 1.0, 5.0 / 6.0, (1.0 + (4.0 / 5.0)) / 2.0, 1.0, 0.75, (1.5 / 1.75), 0 } });
        return testConfigs;
    }

    private Document annotatorResults[];
    private Document goldStandards[];
    private double expectedResults[];
    private Matching matching;

    public EntityRecognitionTest(Document[] annotatorResults, Document[] goldStandards, Matching matching,
            double[] expectedResults) {
        this.annotatorResults = annotatorResults;
        this.goldStandards = goldStandards;
        this.expectedResults = expectedResults;
        this.matching = matching;
    }


}
