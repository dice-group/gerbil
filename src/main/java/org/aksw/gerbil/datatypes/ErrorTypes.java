package org.aksw.gerbil.datatypes;

/**
 * Every error has an error code (which is a negative int) and a description.
 * 
 * @author m.roeder
 * 
 */
public enum ErrorTypes {
    /*
     * ALL ERROR TYPES SHOULD HAVE A _NEGATIVE_ ERROR CODE!
     */

    DATASET_DOES_NOT_SUPPORT_EXPERIMENT(-1, "The dataset does not support the experiment type."),
    ANNOTATOR_DOES_NOT_SUPPORT_EXPERIMENT(-2, "The annotator does not support the experiment type."),
    MATCHING_DOES_NOT_SUPPORT_EXPERIEMNT(-3, "The matching does not support the experiment type")

    // FIXME add error types
    ;

    private ErrorTypes(int errorCode, String description) {
        this.errorCode = errorCode;
        this.description = description;
    }

    private int errorCode;
    private String description;

    public int getErrorCode() {
        return errorCode;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Checks whether the given code is an error code and returns the error type. Otherwise, null is returned.
     * 
     * @param errorCode
     *            the error code for which the error type should be returned.
     * @return the error type for the given error code or null if no such error exists.
     */
    public static ErrorTypes getErrorType(int errorCode) {
        if (errorCode >= 0) {
            return null;
        }
        // search for the error type
        ErrorTypes types[] = ErrorTypes.values();
        for (int i = 0; i < types.length; ++i) {
            if (types[i].errorCode == errorCode) {
                return types[i];
            }
        }
        return null;
    }
}
