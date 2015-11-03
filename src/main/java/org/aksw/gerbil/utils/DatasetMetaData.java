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
