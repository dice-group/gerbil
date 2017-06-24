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
