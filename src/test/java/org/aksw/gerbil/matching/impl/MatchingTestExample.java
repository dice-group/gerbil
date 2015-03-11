package org.aksw.gerbil.matching.impl;

import java.util.Arrays;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Marking;
import org.junit.Ignore;

@Ignore
public class MatchingTestExample<T extends Marking> {
    public List<T> annotatorResult;
    public List<T> goldStandard;
    public int expectedCounts[];

    public MatchingTestExample(T annotatorResult[], T goldStandard[], int[] expectedCounts) {
        this.annotatorResult = Arrays.asList(annotatorResult);
        this.goldStandard = Arrays.asList(goldStandard);
        this.expectedCounts = expectedCounts;
    }

}
