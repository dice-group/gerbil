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
