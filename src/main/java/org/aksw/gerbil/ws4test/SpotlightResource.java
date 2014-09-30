package org.aksw.gerbil.ws4test;

import java.io.IOException;
import java.io.Reader;

import org.aksw.gerbil.transfer.nif.AnnotatedDocument;
import org.aksw.gerbil.transfer.nif.TurtleNIFDocumentCreator;
import org.aksw.gerbil.transfer.nif.TurtleNIFDocumentParser;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpotlightResource extends ServerResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotlightResource.class);

    private static final String DBPEDIA_SPOTLIGHT_ENDPOINT = "spotlight.dbpedia.org:80";

    private TurtleNIFDocumentParser parser = new TurtleNIFDocumentParser();
    private TurtleNIFDocumentCreator creator = new TurtleNIFDocumentCreator();
    private DBpediaSpotlightClient client = new DBpediaSpotlightClient(DBPEDIA_SPOTLIGHT_ENDPOINT);

    @Post
    public String accept(Representation request) {
        Reader inputReader;
        try {
            inputReader = request.getReader();
        } catch (IOException e) {
            LOGGER.error("Exception while reading request.", e);
            return "";
        }
        AnnotatedDocument document;
        try {
            document = parser.getDocumentFromNIFReader(inputReader);
        } catch (Exception e) {
            LOGGER.error("Exception while reading request.", e);
            return "";
        }
        LOGGER.debug("Request: " + document.toString());
        document.setAnnotations(client.annotate(document.getText()));
        LOGGER.debug("Result: " + document.toString());
        String nifDocument = creator.getDocumentAsNIFString(document);
        return nifDocument;
    }
}
