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
package org.aksw.gerbil.utils;

/**
 * Simple structure that contains some meta data of a dataset.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * 
 */
public class DatasetMetaData {

    public double entitiesPerDoc;
    public double entitiesPerToken;
    public double avgDocumentLength;
    public int numberOfDocuments;
    public int numberOfEntities;
    public double amountOfPersons;
    public double amountOfOrganizations;
    public double amountOfLocations;
    public double amountOfOthers;

    public DatasetMetaData() {
    }

    public DatasetMetaData(double entitiesPerDoc, double entitiesPerToken, double avgDocumentLength,
            int numberOfDocuments, int numberOfEntities, double amountOfPersons, double amountOfOrganizations,
            double amountOfLocations, double amountOfOthers) {
        super();
        this.entitiesPerDoc = entitiesPerDoc;
        this.entitiesPerToken = entitiesPerToken;
        this.avgDocumentLength = avgDocumentLength;
        this.numberOfDocuments = numberOfDocuments;
        this.numberOfEntities = numberOfEntities;
        this.amountOfPersons = amountOfPersons;
        this.amountOfOrganizations = amountOfOrganizations;
        this.amountOfLocations = amountOfLocations;
        this.amountOfOthers = amountOfOthers;
    }

}
