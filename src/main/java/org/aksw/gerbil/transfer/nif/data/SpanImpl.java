/**
 * The MIT License (MIT)
 *
 * Copyright (C) ${year} Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
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
package org.aksw.gerbil.transfer.nif.data;

import org.aksw.gerbil.transfer.nif.Span;

public class SpanImpl implements Span {

    protected int startPosition;
    protected int length;

    public SpanImpl(int startPosition, int length) {
        this.startPosition = startPosition;
        this.length = length;
    }

    @Override
    public int getStartPosition() {
        return startPosition;
    }

    @Override
    public int getLength() {
        return length;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + length;
        result = prime * result + startPosition;
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
        SpanImpl other = (SpanImpl) obj;
        if (length != other.length)
            return false;
        if (startPosition != other.startPosition)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "(" + startPosition + ", " + length + ")";
    }
}
