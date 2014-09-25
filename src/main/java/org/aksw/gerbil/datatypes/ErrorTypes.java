package org.aksw.gerbil.datatypes;

/**
 * Every error has an error code (which is a negative int) and a description.
 * 
 * @author m.roeder
 * 
 */
public enum ErrorTypes {
    /*
     * ALL ERROR TYPES SHOULD HAVE A _NEGATIVE_ ERROR CODE STARTING BELOW -100!
     */

    DATASET_DOES_NOT_SUPPORT_EXPERIMENT(-101, "The dataset does not support the experiment type."),
    DATASET_LOADING_ERROR(-104, "The dataset couldn't be loaded."),

    ANNOTATOR_DOES_NOT_SUPPORT_EXPERIMENT(-102, "The annotator does not support the experiment type."),
    ANNOTATOR_LOADING_ERROR(-105, "The annotator couldn't be loaded."),

    MATCHING_DOES_NOT_SUPPORT_EXPERIMENT(-103, "The matching does not support the experiment type"),

    UNEXPECTED_EXCEPTION(-106, "Got an unexpected exception while running the experiment.")

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
