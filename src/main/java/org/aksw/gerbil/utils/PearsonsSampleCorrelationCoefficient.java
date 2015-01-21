/**
 * Copyright (C) 2014 Michael Röder (michael.roeder@unister.de)
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
package org.aksw.gerbil.utils;

public class PearsonsSampleCorrelationCoefficient {

    public static double calculateRankCorrelation(final double x[], final double y[]) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("The x and y array must have the same size!");
        }
        if (x.length < 2) {
            throw new IllegalArgumentException("The x and y array must have a minimum size of 2!");
        }

        double avgX = 0, avgY = 0;
        int pairs = 0;
        for (int i = 0; i < x.length; ++i) {
            if ((!Double.isNaN(x[i])) && (!Double.isNaN(y[i]))) {
                avgX += x[i];
                avgY += y[i];
                ++pairs;
            }
        }
        // If there are no valid pairs
        if (pairs == 0) {
            return 0;
        }
        avgX /= pairs;
        avgY /= pairs;

        double tempX, tempY, varianceX = 0, varianceY = 0, covarianceXY = 0;
        for (int i = 0; i < x.length; ++i) {
            if ((!Double.isNaN(x[i])) && (!Double.isNaN(y[i]))) {
                tempX = x[i] - avgX;
                varianceX += tempX * tempX;
                tempY = y[i] - avgY;
                varianceY += tempY * tempY;
                covarianceXY += tempX * tempY;
            }
        }
        if (varianceX == 0) {
            if (varianceY == 0) {
                return 1;
            } else {
                return 0;
            }
        }
        if (varianceY == 0) {
            return 0;
        }
        double corr = covarianceXY / (Math.sqrt(varianceX) * Math.sqrt(varianceY));
        return corr;
    }
}
