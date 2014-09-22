package org.aksw.gerbil.exceptions;

import org.aksw.gerbil.datatypes.ErrorTypes;

public class GerbilException extends Exception {

    private static final long serialVersionUID = 2095715226837298382L;

    private ErrorTypes errorType;

    public GerbilException(ErrorTypes errorType) {
        super();
        this.errorType = errorType;
    }

    public GerbilException(String msg, ErrorTypes errorType) {
        super(msg);
        this.errorType = errorType;
    }

    public GerbilException(Throwable cause, ErrorTypes errorType) {
        super(cause);
        this.errorType = errorType;
    }

    public GerbilException(String msg, Throwable cause, ErrorTypes errorType) {
        super(msg, cause);
        this.errorType = errorType;
    }

    public ErrorTypes getErrorType() {
        return errorType;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append(": ");
        builder.append(getLocalizedMessage());
        builder.append(" (error type ");
        builder.append(errorType.getErrorCode());
        builder.append(": ");
        builder.append(errorType.getDescription());
        builder.append(')');
        return builder.toString();
    }
}
