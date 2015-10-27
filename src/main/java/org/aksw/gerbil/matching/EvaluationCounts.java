package org.aksw.gerbil.matching;

public class EvaluationCounts {

    public int truePositives = 0;
    public int falsePositives = 0;
    public int falseNegatives = 0;

    public EvaluationCounts() {
    }

    public EvaluationCounts(int truePositives, int falsePositives, int falseNegatives) {
        this.truePositives = truePositives;
        this.falsePositives = falsePositives;
        this.falseNegatives = falseNegatives;
    }

    public void add(EvaluationCounts counts) {
        this.truePositives += counts.truePositives;
        this.falsePositives += counts.falsePositives;
        this.falseNegatives += counts.falseNegatives;
    }

    public int getTruePositives() {
        return truePositives;
    }

    public void setTruePositives(int truePositives) {
        this.truePositives = truePositives;
    }

    public int getFalsePositives() {
        return falsePositives;
    }

    public void setFalsePositives(int falsePositives) {
        this.falsePositives = falsePositives;
    }

    public int getFalseNegatives() {
        return falseNegatives;
    }

    public void setFalseNegatives(int falseNegatives) {
        this.falseNegatives = falseNegatives;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + falseNegatives;
        result = prime * result + falsePositives;
        result = prime * result + truePositives;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EvaluationCounts other = (EvaluationCounts) obj;
        if (falseNegatives != other.falseNegatives)
            return false;
        if (falsePositives != other.falsePositives)
            return false;
        if (truePositives != other.truePositives)
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EvaluationCounts [tp=");
        builder.append(truePositives);
        builder.append(", fp=");
        builder.append(falsePositives);
        builder.append(", fn=");
        builder.append(falseNegatives);
        builder.append("]");
        return builder.toString();
    }

}
