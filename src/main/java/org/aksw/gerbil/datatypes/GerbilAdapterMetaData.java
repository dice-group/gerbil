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
package org.aksw.gerbil.datatypes;


public interface GerbilAdapterMetaData {

    /**
     * Getter of the adapters name.
     * 
     * @return The name of the adapter.
     */
    public String getName();

    /**
     * Setter of the adapters name.
     * 
     * @param name
     *            The name of the adapter.
     */
    public void setName(String name);

    /**
     * Returns true if the system is allowed to cache the results of experiments
     * in which this adapter has been involved.
     * 
     * @return true if the results could be cached inside the database.
     *         Otherwise false is returned.
     */
    public boolean couldBeCached();

    /**
     * Setter for the caching flag which should be set to true if the system is
     * allowed to cache the results of experiments in which this adapter has
     * been involved.
     * 
     * @param couldBeCached
     */
    public void setCouldBeCached(boolean couldBeCached);

    /**
     * Returns true if this adapter can be used for an experiment of the given
     * type.
     * 
     * @param type
     *            the experiment type that should be checked
     * @return true if this adapter can be used for an experiment of the given
     *         type.
     */
    public boolean isApplicableForExperiment(ExperimentType type);
}
