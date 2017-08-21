package org.aksw.gerbil.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

public class DocumentTextComparison {

    /**
     * Implemented as Wagner-Fischer algorithm
     * (https://en.wikipedia.org/wiki/Wagner%E2%80%93Fischer_algorithm)
     * 
     * @param nText
     *            the new text, e.g., received from an annotation system.
     * @param oText
     *            the original text, e.g., the text that was read from a gold
     *            standard dataset.
     * @return The result of the comparison including the Levenstein distance as
     *         well as the changes that would have to be applied to the new text
     *         to become the original text.
     */
    public static DocumentTextComparisonResult getLevensteinDistance(String nText, String oText) {
        // Compute the edit distance between the two given strings
        int nLength = nText.length();
        int oLength = oText.length();
        if (nLength == 0) {
            return DocumentTextComparisonResult.create(oLength, DocumentTextEdits.INSERT);
        }
        if (oLength == 0) {
            return DocumentTextComparisonResult.create(nLength, DocumentTextEdits.DELETE);
        }
        // For all i and j, d[i,j] will hold the Levenshtein distance between
        // the first i characters of s and the first j characters of t.
        // Note that d has (m+1) x (n+1) values.
        DocumentTextComparisonResult matrix[][] = new DocumentTextComparisonResult[nLength + 1][oLength + 1];

        // the distance of any first string to an empty second string
        // (transforming the string of the first i characters of s into
        // the empty string requires i deletions)
        for (int i = 0; i <= nLength; ++i) {
            matrix[i][0] = DocumentTextComparisonResult.create(i, DocumentTextEdits.DELETE);
        }

        // the distance of any second string to an empty first string
        for (int i = 0; i <= oLength; ++i) {
            matrix[0][i] = DocumentTextComparisonResult.create(i, DocumentTextEdits.INSERT);
        }

        // Fill in the rest of the matrix
        for (int i = 1; i <= nLength; ++i) {
            for (int j = 1; j <= oLength; ++j) {
                if (nText.charAt(i - 1) == oText.charAt(j - 1)) {
                    // no operation required
                    matrix[i][j] = DocumentTextComparisonResult.create(matrix[i - 1][j - 1], DocumentTextEdits.NONE);
                } else {
                    matrix[i][j] = determineOperation(matrix, i, j);
                }
            }
        }

        return matrix[nLength][oLength];
    }

    private static DocumentTextComparisonResult determineOperation(DocumentTextComparisonResult[][] matrix, int i, int j) {
        DocumentTextEdits editType;
        DocumentTextComparisonResult formerResult;
        // matrix[i - 1][j - 1] + 1 --> subtitution
        // matrix[i][j - 1] + 1 --> insertion
        // matrix[i - 1][j] + 1 --> deletion
        if (matrix[i][j - 1].editDistance < matrix[i - 1][j - 1].editDistance) {
            if (matrix[i][j - 1].editDistance < matrix[i - 1][j].editDistance) {
                formerResult = matrix[i][j - 1];
                editType = DocumentTextEdits.INSERT;
            } else {
                formerResult = matrix[i - 1][j];
                editType = DocumentTextEdits.DELETE;
            }
        } else {
            if (matrix[i - 1][j - 1].editDistance < matrix[i - 1][j].editDistance) {
                formerResult = matrix[i - 1][j - 1];
                editType = DocumentTextEdits.SUBSTITUTE;
            } else {
                formerResult = matrix[i - 1][j];
                editType = DocumentTextEdits.DELETE;
            }
        }
        return DocumentTextComparisonResult.create(formerResult, editType);
    }

    public static enum DocumentTextEdits {
        NONE, INSERT, DELETE, SUBSTITUTE
    }

    public static class DocumentTextComparisonResult {
        public int editDistance = 0;
        public List<DocumentTextEdits> steps;

        public static DocumentTextComparisonResult create(int length, DocumentTextEdits editType) {
            List<DocumentTextEdits> steps = Lists.newArrayList();
            for (int j = 0; j < length; ++j) {
                steps.add(editType);
            }
            return new DocumentTextComparisonResult(length, steps);
        }

        public static DocumentTextComparisonResult create(DocumentTextComparisonResult formerResult, DocumentTextEdits newStep) {
            DocumentTextComparisonResult newResult = new DocumentTextComparisonResult(formerResult);
            newResult.addStep(newStep);
            return newResult;
        }

        public DocumentTextComparisonResult(int editDistance, List<DocumentTextEdits> steps) {
            this.editDistance = editDistance;
            this.steps = steps;
        }

        public DocumentTextComparisonResult(DocumentTextComparisonResult o) {
            this.editDistance = o.editDistance;
            this.steps = new ArrayList<>(o.steps);
        }

        public void addStep(DocumentTextEdits newStep) {
            this.steps.add(newStep);
            if (newStep != DocumentTextEdits.NONE) {
                ++this.editDistance;
            }
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + editDistance;
            result = prime * result + ((steps == null) ? 0 : steps.hashCode());
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
            DocumentTextComparisonResult other = (DocumentTextComparisonResult) obj;
            if (editDistance != other.editDistance)
                return false;
            if (steps == null) {
                if (other.steps != null)
                    return false;
            } else if (!steps.equals(other.steps))
                return false;
            return true;
        }

    }
}
