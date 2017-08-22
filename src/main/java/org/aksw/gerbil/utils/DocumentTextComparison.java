package org.aksw.gerbil.utils;

import java.util.Arrays;
import java.util.List;

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
        int expectedSteps = Math.max(nLength, oLength);
        // For all i and j, d[i,j] will hold the Levenshtein distance between
        // the first i characters of s and the first j characters of t.
        // Note that d has (m+1) x (n+1) values.
        DocumentTextComparisonResult oldLine[] = new DocumentTextComparisonResult[oLength + 1];
        DocumentTextComparisonResult currentLine[] =  new DocumentTextComparisonResult[oLength + 1];
        DocumentTextComparisonResult temp[];

//        // the distance of any first string to an empty second string
//        // (transforming the string of the first i characters of s into
//        // the empty string requires i deletions)
//        for (int i = 0; i <= nLength; ++i) {
//            matrix[i][0] = DocumentTextComparisonResult.create(i, DocumentTextEdits.DELETE, expectedSteps);
//        }

        // the distance of any second string to an empty first string
        for (int i = 0; i <= oLength; ++i) {
            oldLine[i] = DocumentTextComparisonResult.create(i, DocumentTextEdits.INSERT, expectedSteps);
        }

        // Fill in the rest of the matrix
        for (int i = 1; i <= nLength; ++i) {
            currentLine[0] = DocumentTextComparisonResult.create(i, DocumentTextEdits.DELETE, expectedSteps);
            for (int j = 1; j <= oLength; ++j) {
                if (nText.charAt(i - 1) == oText.charAt(j - 1)) {
                    // no operation required
                    currentLine[j] = DocumentTextComparisonResult.create(oldLine[j - 1], DocumentTextEdits.NONE);
                } else {
                    currentLine[j] = determineOperation(currentLine, oldLine, i, j);
                }
            }
            temp = oldLine;
                    oldLine = currentLine;
            currentLine = temp;
        }

        return oldLine[oLength];
    }

    private static DocumentTextComparisonResult determineOperation(DocumentTextComparisonResult[] currentLine,
            DocumentTextComparisonResult[] oldLine, int i, int j) {
        DocumentTextEdits editType;
        DocumentTextComparisonResult formerResult;
        // matrix[i - 1][j - 1] + 1 --> subtitution
        // matrix[i][j - 1] + 1 --> insertion
        // matrix[i - 1][j] + 1 --> deletion
        if (currentLine[j - 1].editDistance < oldLine[j - 1].editDistance) {
            if (currentLine[j - 1].editDistance < oldLine[j].editDistance) {
                formerResult = currentLine[j - 1];
                editType = DocumentTextEdits.INSERT;
            } else {
                formerResult = oldLine[j];
                editType = DocumentTextEdits.DELETE;
            }
        } else {
            if (oldLine[j - 1].editDistance < oldLine[j].editDistance) {
                formerResult = oldLine[j - 1];
                editType = DocumentTextEdits.SUBSTITUTE;
            } else {
                formerResult = oldLine[j];
                editType = DocumentTextEdits.DELETE;
            }
        }
        return DocumentTextComparisonResult.create(formerResult, editType);
    }

    public static enum DocumentTextEdits {
        NONE, INSERT, DELETE, SUBSTITUTE
    }

    /**
     * 
     * <p>
     * Internally, it uses the following encoding.<br>
     * {@code NONE -> 00}<br>
     * {@code INSERT -> 01}<br>
     * {@code DELETE -> 10}<br>
     * {@code SUBSTITUTE -> 11}<br>
     * </p>
     * 
     * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
     *
     */
    public static class DocumentTextComparisonResult {
        protected int editDistance = 0;
        protected int numberOfSteps = 0;
        protected long steps[];

        public static DocumentTextComparisonResult create(int length, DocumentTextEdits editType,
                int overallExpNumberOfEdits) {
            DocumentTextComparisonResult result = new DocumentTextComparisonResult(overallExpNumberOfEdits);
            addEditNTimes(result, editType, length);
            return result;
        }

        public static DocumentTextComparisonResult create(int length, DocumentTextEdits editType) {
            DocumentTextComparisonResult result = new DocumentTextComparisonResult(length);
            addEditNTimes(result, editType, length);
            return result;
        }

        public static DocumentTextComparisonResult addEditNTimes(DocumentTextComparisonResult result,
                DocumentTextEdits editType, int n) {
            for (int j = 0; j < n; ++j) {
                result.addStep(editType);
            }
            return result;
        }

        public static DocumentTextComparisonResult create(DocumentTextComparisonResult formerResult,
                DocumentTextEdits newStep) {
            DocumentTextComparisonResult newResult = new DocumentTextComparisonResult(formerResult);
            newResult.addStep(newStep);
            return newResult;
        }

        public DocumentTextComparisonResult(List<DocumentTextEdits> steps) {
            this(steps.size());
            for (DocumentTextEdits step : steps) {
                addStep(step);
            }
        }

        public DocumentTextComparisonResult(int expectedNumberOfSteps) {
            this.steps = new long[(expectedNumberOfSteps / 32) + ((expectedNumberOfSteps % 32) == 0 ? 0 : 1)];
        }

        public DocumentTextComparisonResult() {
            this(0);
        }

        public DocumentTextComparisonResult(DocumentTextComparisonResult o) {
            this.editDistance = o.editDistance;
            this.numberOfSteps = o.numberOfSteps;
            this.steps = Arrays.copyOf(o.steps, o.steps.length);
        }

        public void addStep(DocumentTextEdits newStep) {
            int currentCell = numberOfSteps / 32;
            if (currentCell >= steps.length) {
                steps = Arrays.copyOf(steps, currentCell + 1);
            }
            if (newStep != DocumentTextEdits.NONE) {
                ++this.editDistance;
                long mask = 0;
                switch (newStep) {
                case SUBSTITUTE:
                    mask = 3;
                    break;
                case DELETE:
                    mask = 2;
                    break;
                case INSERT:
                    mask = 1;
                    break;
                case NONE: // not possible here
                    break;
                }
                int currentBit = (numberOfSteps % 32) * 2;
                steps[currentCell] |= mask << currentBit;
            }
            ++numberOfSteps;
        }

        public DocumentTextEdits getStep(int index) {
            if (index < 0) {
                throw new IllegalArgumentException("Index has to be >= 0");
            }
            if (index >= numberOfSteps) {
                throw new IllegalArgumentException(
                        "Index was " + index + " while there are only " + numberOfSteps + " available.");
            }
            int currentCell = index / 32;
            int currentBit = (index % 32) * 2;
            int value = (int) ((steps[currentCell] & (3L << currentBit)) >>> currentBit);
            switch (value) {
            case 0:
                return DocumentTextEdits.NONE;
            case 1:
                return DocumentTextEdits.INSERT;
            case 2:
                return DocumentTextEdits.DELETE;
            case 3:
                return DocumentTextEdits.SUBSTITUTE;
            default:
                throw new IllegalStateException("This shouldn't be possible. Programming error.");
            }
        }

        public int getNumberOfSteps() {
            return numberOfSteps;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + editDistance;
            result = prime * result + numberOfSteps;
            result = prime * result + Arrays.hashCode(steps);
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
            if (numberOfSteps != other.numberOfSteps)
                return false;
            if (!Arrays.equals(steps, other.steps))
                return false;
            return true;
        }
    }
}
