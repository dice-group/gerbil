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

import org.aksw.gerbil.utils.ExperimentTypeComparator;

public abstract class AbstractAdapterConfiguration implements AdapterConfiguration {

    protected String name;
    protected boolean couldBeCached;
    protected ExperimentType applicableForExperiment;

    public AbstractAdapterConfiguration(String name, boolean couldBeCached, ExperimentType applicableForExperiment) {
        this.name = name;
        this.couldBeCached = couldBeCached;
        this.applicableForExperiment = applicableForExperiment;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean couldBeCached() {
        return couldBeCached;
    }

    @Override
    public void setCouldBeCached(boolean couldBeCached) {
        this.couldBeCached = couldBeCached;
    }

    @Override
    public boolean isApplicableForExperiment(ExperimentType type) {
        // for (int i = 0; i < applicableForExperiments.length; i++) {
        // if (applicableForExperiments[i].equalsOrContainsType(type)) {
        // return true;
        // }
        // }
        // return false;
        return applicableForExperiment.equalsOrContainsType(type);
    }

    @Override
    public ExperimentType getExperimentType() {
        return applicableForExperiment;
    }

    @Override
    public int compareTo(AdapterConfiguration o) {
        int result = (new ExperimentTypeComparator()).compare(this.applicableForExperiment, o.getExperimentType());
        if (result == 0) {
            return this.getName().compareTo(o.getName());
        } else {
            return result;
        }
    }
}
