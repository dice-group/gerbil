package org.aksw.gerbil.matching.impl;

import org.aksw.gerbil.transfer.nif.data.NamedEntity;

public class CompoundMatchingsCounterTest extends AbstractMatchingsCounterTest<NamedEntity> {

    @SuppressWarnings("rawtypes")
    private static final MatchingTestExample EXAMPLES[] = new MatchingTestExample[] {
            // empty test case
            new MatchingTestExample<NamedEntity>(new NamedEntity[] {}, new NamedEntity[] {}, new int[] { 0, 0, 0 }),
            // test case with empty annotator results
            new MatchingTestExample<NamedEntity>(new NamedEntity[] { new NamedEntity(0, 10, "http://kb/1") },
                    new NamedEntity[] {}, new int[] { 0, 1, 0 }),
            // test case with empty gold standard
            new MatchingTestExample<NamedEntity>(new NamedEntity[] {}, new NamedEntity[] { new NamedEntity(0, 10,
                    "http://kb/1") }, new int[] { 0, 0, 1 }),
            // test case with single exact matching NamedEntitys
            new MatchingTestExample<NamedEntity>(new NamedEntity[] { new NamedEntity(1, 10, "http://kb/1") },
                    new NamedEntity[] { new NamedEntity(1, 10, "http://kb/1") }, new int[] { 1, 0, 0 }),
            // test case with several exact matching NamedEntitys
            new MatchingTestExample<NamedEntity>(new NamedEntity[] { new NamedEntity(0, 10, "http://kb/1"),
                    new NamedEntity(20, 10, "http://kb/2") }, new NamedEntity[] {
                    new NamedEntity(0, 10, "http://kb/1"), new NamedEntity(20, 10, "http://kb/2") }, new int[] { 2, 0,
                    0 }),
            // test case with one matching pair and another not matching pair
            // (pos)
            new MatchingTestExample<NamedEntity>(new NamedEntity[] { new NamedEntity(60, 10, "http://kb/1"),
                    new NamedEntity(20, 10, "http://kb/2") }, new NamedEntity[] {
                    new NamedEntity(0, 10, "http://kb/1"), new NamedEntity(20, 10, "http://kb/2") }, new int[] { 1, 1,
                    1 }),
            // test case with one matching pair and another not matching pair
            // (meaning)
            new MatchingTestExample<NamedEntity>(new NamedEntity[] { new NamedEntity(0, 10, "http://kb/1"),
                    new NamedEntity(20, 10, "http://kb/2") }, new NamedEntity[] {
                    new NamedEntity(0, 10, "http://kb/999"), new NamedEntity(20, 10, "http://kb/2") }, new int[] { 1,
                    1, 1 }),
            // test case with partly overlapping NamedEntitys
            new MatchingTestExample<NamedEntity>(new NamedEntity[] { new NamedEntity(0, 10, "http://kb/1"),
                    new NamedEntity(20, 10, "http://kb/1"), new NamedEntity(40, 10, "http://kb/1"),
                    new NamedEntity(62, 3, "http://kb/1") }, new NamedEntity[] { new NamedEntity(2, 10, "http://kb/1"),
                    new NamedEntity(16, 10, "http://kb/1"), new NamedEntity(42, 4, "http://kb/1"),
                    new NamedEntity(60, 10, "http://kb/1") }, new int[] { 0, 4, 4 }),
            // test case with overlapping NamedEntitys in the annotator result
            // matching a single NamedEntity of the gold standard
            new MatchingTestExample<NamedEntity>(new NamedEntity[] { new NamedEntity(0, 10, "http://kb/1"),
                    new NamedEntity(6, 10, "http://kb/1") },
                    new NamedEntity[] { new NamedEntity(2, 10, "http://kb/1") }, new int[] { 0, 2, 1 }),
            // test case with several exact matching Meanings
            new MatchingTestExample<NamedEntity>(new NamedEntity[] { new NamedEntity(0, 10, "http://kb/1"),
                    new NamedEntity(20, 10, "http://kb/2"), new NamedEntity(40, 10, "http://kb/3") },
                    new NamedEntity[] { new NamedEntity(0, 10, "http://kb/1"), new NamedEntity(20, 10, "http://kb/2"),
                            new NamedEntity(40, 10, "http://kb/3") }, new int[] { 3, 0, 0 }),
            // test case with several exact matching Meanings with a different
            // order
            new MatchingTestExample<NamedEntity>(new NamedEntity[] { new NamedEntity(0, 10, "http://kb/1"),
                    new NamedEntity(20, 10, "http://kb/2"), new NamedEntity(40, 10, "http://kb/3") },
                    new NamedEntity[] { new NamedEntity(20, 10, "http://kb/2"), new NamedEntity(40, 10, "http://kb/3"),
                            new NamedEntity(0, 10, "http://kb/1") }, new int[] { 3, 0, 0 }),
            // test case with one exact matching but two other meanings at the
            // same position
            new MatchingTestExample<NamedEntity>(new NamedEntity[] { new NamedEntity(0, 10, "http://kb/1"),
                    new NamedEntity(0, 10, "http://kb/2"), new NamedEntity(0, 10, "http://kb/3") },
                    new NamedEntity[] { new NamedEntity(0, 10, "http://kb/2") }, new int[] { 1, 2, 0 }),
            // test case with the same meanings but the positions are matching
            // the wrong meaning
            new MatchingTestExample<NamedEntity>(new NamedEntity[] { new NamedEntity(0, 10, "http://kb/1"),
                    new NamedEntity(20, 10, "http://kb/2"), new NamedEntity(40, 10, "http://kb/3") },
                    new NamedEntity[] { new NamedEntity(0, 10, "http://kb/2"), new NamedEntity(20, 10, "http://kb/3"),
                            new NamedEntity(40, 10, "http://kb/1") }, new int[] { 0, 3, 3 }) };

    @SuppressWarnings("unchecked")
    public CompoundMatchingsCounterTest() {
        super(new CompoundMatchingsCounter<NamedEntity>(new StrongSpanMatchingsCounter<NamedEntity>(),
                new MeaningMatchingsCounter<NamedEntity>()), EXAMPLES);
    }
}