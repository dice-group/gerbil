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
     * The dataset appears not .
     */
    DATASET_EMPTY_ERROR(-110, "The dataset appeared to be empty."),

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
