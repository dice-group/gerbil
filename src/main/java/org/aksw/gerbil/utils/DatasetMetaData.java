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
