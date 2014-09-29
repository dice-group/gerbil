package org.aksw.gerbil.bat.datasets;

import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.data.Mention;
import it.acubelab.batframework.data.Tag;
import it.acubelab.batframework.problems.A2WDataset;
import it.acubelab.batframework.utils.ProblemReduction;

import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.bat.converter.DBpediaToWikiId;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;

public abstract class AbstractNIFDataset implements A2WDataset {

    private static final transient Logger logger = LoggerFactory
            .getLogger(AbstractNIFDataset.class);

    private List<HashSet<Annotation>> annotationsList;
    private List<String> texts;

    private String name;

    public AbstractNIFDataset(String name) {
        texts = new LinkedList<String>();
        annotationsList = new LinkedList<HashSet<Annotation>>();
        this.name = name;
    }

    /**
     * This method returns an opened InputStream from which the NIF data will be read. If an error occurs while opening
     * the stream, null should be returned. <b>Note</b> that for closing the stream
     * {@link #closeInputStream(InputStream)} is called. If there are other resources related to this stream that have
     * to be closed, this method can be overwritten to free these resources, too.
     * 
     * @return an opened InputStream or null if an error occurred.
     */
    protected abstract InputStream getDataAsInputStream();

    /**
     * This method returns the language of the NIF data.
     * 
     * @return the language of the NIF data
     */
    protected abstract Lang getDataLanguage();

    /**
     * This method is called for closing the input stream that has been returned by {@link #getDataAsInputStream()}.If
     * there are other resources related to this stream that have to be closed, this method can be overwritten to free
     * these resources, too.
     * 
     * @param inputStream
     *            the input stream which should be closed
     */
    protected void closeInputStream(InputStream inputStream) {
        try {
            inputStream.close();
        } catch (Exception e) {
        }
    }

    protected void init() {
        Model dataset = ModelFactory.createDefaultModel();
        // dataset = RDFDataMgr.loadModel(rdfpath);
        InputStream inputStream = getDataAsInputStream();
        if (inputStream == null) {
            // FIXME better error handling
            return;
        }
        RDFDataMgr.read(dataset, inputStream, getDataLanguage());
        closeInputStream(inputStream);

        PrefixMapping prefixes = new PrefixMappingImpl()
                .withDefaultMappings(PrefixMapping.Extended);
        prefixes.setNsPrefix("nif",
                "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#");
        prefixes.setNsPrefix("dbo", "http://dbpedia.org/ontology/");
        prefixes.setNsPrefix("itsrdf", "http://www.w3.org/2005/11/its/rdf#");

        Query query = QueryFactory
                .create("prefix nif: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#>"
                        + " SELECT distinct ?context ?string WHERE {?context a nif:Context . ?context nif:isString ?string .}");
        QueryExecution exec = QueryExecutionFactory.create(query, dataset);
        ResultSet result = exec.execSelect();
        ParameterizedSparqlString annotationQuery = new ParameterizedSparqlString(
                "SELECT distinct ?annotation ?begin ?end ?dbpedia ?entity WHERE {?annotation nif:referenceContext ?context . "
                        + "?annotation nif:beginIndex ?begin . "
                        + "?annotation nif:endIndex ?end . "
                        + "?annotation itsrdf:taIdentRef ?dbpedia . ?annotation nif:anchorOf ?entity }",
                prefixes);

        // ParameterizedSparqlString dbpediaQuery = new
        // ParameterizedSparqlString(
        // "SELECT ?id WHERE { ?dbpedia dbo:wikiPageID ?id .}", prefixes);
        while (result.hasNext()) {
            QuerySolution solution = result.next();
            RDFNode rdfNode = solution.get("context");
            String context = rdfNode.asResource().toString();
            logger.info("processing text {}", context);
            annotationQuery.clearParams();
            annotationQuery.setIri("context", context);
            QueryExecution qAnn = QueryExecutionFactory.create(
                    annotationQuery.asQuery(), dataset);
            ResultSet annResult = qAnn.execSelect();

            texts.add(solution.get("string").asLiteral().getString());
            HashSet<Annotation> annotations = new HashSet<Annotation>();
            annotationsList.add(annotations);
            while (annResult.hasNext()) {

                QuerySolution annSolution = annResult.next();
                String page = annSolution.get("dbpedia").asResource().getURI();
                // logger.info("processing annotation {}", page);
                // dbpediaQuery.setIri("dbpedia", page);

                int id = DBpediaToWikiId.getId(page);
                int position = annSolution.get("begin").asLiteral().getInt();
                int length = annSolution.get("end").asLiteral().getInt()
                        - position;
                if (id != -1) {
                    annotations.add(new Annotation(position, length, id));
                    logger.debug("Annotation: text:{} begin:{} lenght:{}",
                            new Object[] {
                                    annSolution.get("entity").asLiteral()
                                            .getString(), position, length });
                }
            }
        }
        logger.info("{} dataset initialized", name);
        DBpediaToWikiId.write();
    }

    public int getTagsCount() {
        int count = 0;
        for (Set<Annotation> s : annotationsList)
            count += s.size();
        return count;
    }

    public List<HashSet<Tag>> getC2WGoldStandardList() {
        return ProblemReduction.A2WToC2WList(this.getA2WGoldStandardList());
    }

    public int getSize() {
        return this.texts.size();
    }

    public String getName() {
        return name;
    }

    public List<String> getTextInstanceList() {
        return texts;
    }

    public List<HashSet<Mention>> getMentionsInstanceList() {
        return ProblemReduction
                .A2WToD2WMentionsInstance(getA2WGoldStandardList());
    }

    public List<HashSet<Annotation>> getD2WGoldStandardList() {
        return this.annotationsList;
    }

    public List<HashSet<Annotation>> getA2WGoldStandardList() {
        return this.getD2WGoldStandardList();
    }

}
