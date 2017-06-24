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
package org.aksw.gerbil.matching.scored;

import java.util.Arrays;

import com.carrotsearch.hppc.DoubleOpenHashSet;

/**
 * This structure contains the evaluation counts of several documents with
 * several confidence thresholds. The main mapping is from an array of
 * confidence {@link #scores} (= thresholds) to the results. The results are
 * available as sums, e.g., {@link #truePositiveSums}, or as document-wise
 * counts, e.g., {@link #truePositives}.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class ScoredEvaluationCountsArray {

    /**
     * This method creates a single {@link ScoredEvaluationCountsArray} instance
     * that summarizes the numbers of the given {@link ScoredEvaluationCounts}
     * array.
     * 
     * @param scoredCounts
     * @return
     */
    public static ScoredEvaluationCountsArray create(ScoredEvaluationCounts scoredCounts[][]) {
        DoubleOpenHashSet singleScores = new DoubleOpenHashSet();
        for (int i = 0; i < scoredCounts.length; ++i) {
            for (int j = 0; j < scoredCounts[i].length; ++j) {
                singleScores.add(scoredCounts[i][j].confidenceThreshould);
            }
        }
        double scoresArray[] = singleScores.toArray();
        Arrays.sort(scoresArray);
        ScoredEvaluationCountsArray array = new ScoredEvaluationCountsArray(scoresArray.length, scoredCounts.length);
        int currentPosInDocArray[] = new int[scoredCounts.length];
        ScoredEvaluationCounts currentCounts;
        // go through all scores
        for (int i = 0; i < scoresArray.length; ++i) {
            array.scores[i] = scoresArray[i];
            // go over all documents
            for (int d = 0; d < scoredCounts.length; ++d) {
                // check whether we are at the correct position in the
                // document's array. Make sure that the threshold at the
                // next position is larger than the current threshold. Otherwise
                // we have to increase the count
                if ((scoredCounts[d].length > (currentPosInDocArray[d] + 1))
                        && (scoresArray[i] >= scoredCounts[d][currentPosInDocArray[d] + 1].confidenceThreshould)) {
                    ++currentPosInDocArray[d];
                }
                // collect the counts (if the current threshold is not to high =
                // we are not at the end of the array)
                if (scoredCounts[d].length > currentPosInDocArray[d]) {
                    currentCounts = scoredCounts[d][currentPosInDocArray[d]];
                    array.truePositives[i][d] = currentCounts.truePositives;
                    array.falseNegatives[i][d] = currentCounts.falseNegatives;
                    array.falsePositives[i][d] = currentCounts.falsePositives;
                }
            }
        }
        // generate sums
        for (int i = 0; i < scoresArray.length; ++i) {
            for (int d = 0; d < scoredCounts.length; ++d) {
                array.truePositiveSums[i] += array.truePositives[i][d];
                array.falseNegativeSums[i] += array.falseNegatives[i][d];
                array.falsePositiveSums[i] += array.falsePositives[i][d];
            }
        }
        return array;
    }

    /**
     * Confidence thresholds.
     */
    public double scores[];

    /**
     * Document-wise true positive counts for the different thresholds. The
     * first dimension is the threshold while the second dimension is the
     * document id.
     */
    public int truePositives[][];

    /**
     * Document-wise false negative counts for the different thresholds. The
     * first dimension is the threshold while the second dimension is the
     * document id.
     */
    public int falseNegatives[][];

    /**
     * Document-wise false positive counts for the different thresholds. The
     * first dimension is the threshold while the second dimension is the
     * document id.
     */
    public int falsePositives[][];

    /**
     * The true positive sums for the single thresholds.
     */
    public int truePositiveSums[];

    /**
     * The false negative sums for the single thresholds.
     */
    public int falseNegativeSums[];

    /**
     * The false positive sums for the single thresholds.
     */
    public int falsePositiveSums[];

    /**
     * Constructor that allocates the needed arrays with the given size
     * parameters.
     * 
     * @param numberOfScores
     * @param numberOfDocuments
     */
    public ScoredEvaluationCountsArray(int numberOfScores, int numberOfDocuments) {
        scores = new double[numberOfScores];
        truePositives = new int[numberOfScores][numberOfDocuments];
        falseNegatives = new int[numberOfScores][numberOfDocuments];
        falsePositives = new int[numberOfScores][numberOfDocuments];
        truePositiveSums = new int[numberOfScores];
        falseNegativeSums = new int[numberOfScores];
        falsePositiveSums = new int[numberOfScores];
    }
}
