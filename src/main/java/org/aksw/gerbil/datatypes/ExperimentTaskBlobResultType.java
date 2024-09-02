package org.aksw.gerbil.datatypes;

import org.aksw.gerbil.evaluate.impl.FMeasureCalculator;

public enum ExperimentTaskBlobResultType {

    CONTINGENCY_MATRIX(FMeasureCalculator.CONTINGENCY_MATRIX_NAME, 1);

    private final String resultType;

    private final int resultId;

    ExperimentTaskBlobResultType(String resultType, int resultId) {
        this.resultType = resultType;
        this.resultId = resultId;
    }

    public static int getResultId(String resultType) {
        for (ExperimentTaskBlobResultType result : ExperimentTaskBlobResultType.values()) {
            if (result.resultType.equals(resultType)) {
                return result.resultId;
            }
        }
        return -1;
    }
}
