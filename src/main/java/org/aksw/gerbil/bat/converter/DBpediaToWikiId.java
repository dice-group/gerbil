package org.aksw.gerbil.bat.converter;

import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.shared.PrefixMapping;

public class DBpediaToWikiId {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBpediaToWikiId.class);

    @Deprecated
    private static String fileName = "dbpediaids.ttl";

    @Deprecated
    private static Model model;

    @Deprecated
    private static PrefixMapping prefixes;

    // static {
    // File f = new File(fileName);
    // if (f.exists()) {
    //
    // model = RDFDataMgr.loadModel(fileName);
    // } else {
    // model = ModelFactory.createDefaultModel();
    // }
    // prefixes = new PrefixMappingImpl()
    // .withDefaultMappings(PrefixMapping.Extended);
    // prefixes.setNsPrefix("nif",
    // "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#");
    // prefixes.setNsPrefix("dbo", "http://dbpedia.org/ontology/");
    // prefixes.setNsPrefix("itsrdf", "http://www.w3.org/2005/11/its/rdf#");
    // }

    /**
     * The Wikipedia Id or -1 if the Id couldn't be retrieved.
     * 
     * @param wikiApi
     *            The API used to retrieve the id
     * @param dbpediaUri
     *            URI for which the id should be retrieved
     * @return Wikipedia Id or -1
     */
    public static int getId(WikipediaApiInterface wikiApi, String dbpediaUri) {
        if (dbpediaUri != null) {
            int pos = dbpediaUri.indexOf("/resource/");
            if (pos >= 0) {
                String title = dbpediaUri.substring(pos + 10);
                try {
                    return wikiApi.getIdByTitle(title);
                } catch (Exception e) {
                    LOGGER.error("Error while trying to get the ID for the title {}. Returning -1.", title, e);
                }
            }
        }
        return -1;
    }

    /**
     * The Wikipedia Id or -1 if the Id couldn't be retrieved.
     * 
     * FIXME The method throws an exception for "http://DBpedia.org/resource/Origin_of_the_name_"Empire_State"". this
     * might be happen because of the quotes inside the URI.
     * 
     * @param dbpediaUri
     * @return
     */
    @Deprecated
    public static int getIdFromDBpedia(String dbpediaUri) {
        int id = -1;
        ParameterizedSparqlString query = new ParameterizedSparqlString(
                "SELECT ?id WHERE { ?dbpedia dbo:wikiPageID ?id .}", prefixes);
        query.setIri("dbpedia", dbpediaUri);
        QueryExecution qexec = null;
        try {
            qexec = QueryExecutionFactory.create(query.asQuery(),
                    model);
        } catch (QueryParseException e) {
            LOGGER.error("Got a bad dbpediaUri \"" + dbpediaUri
                    + "\" which couldn't be parse inside of a SPARQL query. Returning -1.", e);
            return id;
        }
        ResultSet result = qexec.execSelect();
        if (result.hasNext()) {
            id = result.next().get("id").asLiteral().getInt();
            return id;
        }
        qexec = QueryExecutionFactory.sparqlService(
                "http://dbpedia.org/sparql", query.asQuery());
        result = qexec.execSelect();
        if (result.hasNext()) {
            id = result.next().get("id").asLiteral().getInt();
            model.add(new StatementImpl(model.createResource(dbpediaUri), model
                    .createProperty("http://dbpedia.org/ontology/wikiPageID"),
                    model.createTypedLiteral(id)));
            return id;
        }

        model.add(new StatementImpl(model.createResource(dbpediaUri), model
                .createProperty("http://dbpedia.org/ontology/wikiPageID"),
                model.createTypedLiteral(id)));
        return id;
    }

    @Deprecated
    public static void write() {
        try {
            model.write(new FileOutputStream(fileName), "TTL");
        } catch (FileNotFoundException e) {
            LOGGER.error("Exception while writing model to file.", e);
        }
    }
}
