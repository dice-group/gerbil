/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
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
