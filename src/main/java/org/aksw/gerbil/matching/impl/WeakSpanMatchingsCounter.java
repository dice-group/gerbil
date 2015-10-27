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
package org.aksw.gerbil.matching.impl;

import java.util.List;

import org.aksw.gerbil.matching.MatchingsSearcher;
import org.aksw.gerbil.transfer.nif.Span;

import com.carrotsearch.hppc.BitSet;

public class WeakSpanMatchingsCounter<T extends Span> implements MatchingsSearcher<T> {

    @Override
    public BitSet findMatchings(T expectedElement, List<T> annotatorResult, BitSet alreadyUsedResults) {
        int eStart = expectedElement.getStartPosition();
        int eEnd = eStart + expectedElement.getLength();
        int rStart, rEnd;
        T result;
        BitSet matching = new BitSet(alreadyUsedResults.size());
        for (int i = 0; i < annotatorResult.size(); ++i) {
            if (!alreadyUsedResults.get(i)) {
                result = annotatorResult.get(i);
                rStart = result.getStartPosition();
                rEnd = rStart + result.getLength();
                if (rStart >= eStart) {
                    if (rStart < eEnd) {
                        matching.set(i);
                        // yes, we have found a matching position, but note,
                        // that we have to find all matching positions!
                    }
                } else if (eStart < rEnd) {
                    matching.set(i);
                }
            }
        }
        return matching;
    }

}
