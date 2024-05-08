package org.aksw.gerbil.evaluate;

import org.aksw.gerbil.matching.EvaluationCounts;

public class ExtendedMacros {
    private String id;
    private EvaluationCounts count;
    private double precision;
    private double recall;
    private double f1Score;

    public ExtendedMacros(String id, EvaluationCounts count, double precision, double recall, double f1Score) {
        this.id = id;
        this.count = count;
        this.precision = precision;
        this.recall = recall;
        this.f1Score = f1Score;
    }

    public String getId() {
        return id;
    }

    public EvaluationCounts getCount() {
        return count;
    }

    public double getPrecision() {
        return precision;
    }

    public double getRecall() {
        return recall;
    }

    public double getF1Score() {
        return f1Score;
    }
}
