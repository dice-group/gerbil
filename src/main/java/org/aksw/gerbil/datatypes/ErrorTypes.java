/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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

    /**
     * The dataset does not support the experiment type.
     */
    DATASET_DOES_NOT_SUPPORT_EXPERIMENT(-101, "The dataset does not support the experiment type."),
    /**
     * The dataset couldn't be loaded.
     */
    DATASET_LOADING_ERROR(-104, "The dataset couldn't be loaded."),

    /**
     * The annotator does not support the experiment type.
     */
    ANNOTATOR_DOES_NOT_SUPPORT_EXPERIMENT(-102, "The annotator does not support the experiment type."),
    /**
     * The annotator couldn't be loaded.
     */
    ANNOTATOR_LOADING_ERROR(-105, "The annotator couldn't be loaded."),

    /**
     * The matching does not support the experiment type.d
     */
    MATCHING_DOES_NOT_SUPPORT_EXPERIMENT(-103, "The matching does not support the experiment type."),

    /**
     * Got an unexpected exception while running the experiment.
     */
    UNEXPECTED_EXCEPTION(-106, "Got an unexpected exception while running the experiment."),
    /**
     * The GERBIL server has been stopped while the experiment was running.
     */
    SERVER_STOPPED_WHILE_PROCESSING(-107, "The GERBIL server has been stopped while the experiment was running."),
    /**
     * The annotator caused too many single errors.
     */
    TOO_MANY_SINGLE_ERRORS(-108, "The annotator caused too many single errors."),
    /**
     * The annotator needed too much time and has been interrupted.
     */
    ANNOTATOR_NEEDED_TOO_MUCH_TIME(-109, "The annotator needed too much time and has been interrupted.");

    private ErrorTypes(int errorCode, String description) {
        this.errorCode = errorCode;
        this.description = description;
    }

    public static final int HIGHEST_ERROR_CODE = -100;

    private int errorCode;
    private String description;

    public int getErrorCode() {
        return errorCode;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Checks whether the given code is an error code and returns the error
     * type. Otherwise, null is returned.
     * 
     * @param errorCode
     *            the error code for which the error type should be returned.
     * @return the error type for the given error code or null if no such error
     *         exists.
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
