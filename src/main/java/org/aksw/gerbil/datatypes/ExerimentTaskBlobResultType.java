package org.aksw.gerbil.datatypes;

import org.aksw.gerbil.evaluate.impl.FMeasureCalculator;

public enum ExerimentTaskBlobResultType {

    CONTINGENCY_MATRIX(FMeasureCalculator.CONTINGENCY_MATRIX_NAME, 1);
    private final String resultType;

    private final int resultId;
    ExerimentTaskBlobResultType(String resultType, int resultId) {
        this.resultType = resultType;
        this.resultId = resultId;
    }

    public static int getResultId(String resultType) {
        for (ExerimentTaskBlobResultType result : ExerimentTaskBlobResultType.values()) {
            if (result.resultType.equals(resultType)) {
                return result.resultId;
            }
        }
        return -1;
    }
}
