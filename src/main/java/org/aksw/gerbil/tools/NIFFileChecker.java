package org.aksw.gerbil.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.gerbil.io.nif.impl.TurtleNIFParser;
import org.aksw.gerbil.io.nif.utils.NIFSemanticsChecker;
import org.aksw.gerbil.io.nif.utils.NIFSemanticsChecker.DomainRange;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.Relation;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NIFFileChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(NIFFileChecker.class);

    public static void main(String[] args) {
        if (args.length < 1) {
            LOGGER.error(
                    "Wrong usage. Correct usage: '<path-to-nif-file(s)> [path-to-file-defining-domain-and-range]'.");
            return;
        }
        Map<String, DomainRange> domainRangeDef = null;
        if (args.length > 1) {
            domainRangeDef = readDomainRangeDef(args[1]);
        }
        NIFFileChecker checker = new NIFFileChecker(domainRangeDef);
        checker.check(new File(args[0]));
    }

    private TurtleNIFParser parser = new TurtleNIFParser();
    private Map<String, DomainRange> domainRangeDef;

    public NIFFileChecker(Map<String, DomainRange> domainRangeDef) {
        super();
        this.domainRangeDef = domainRangeDef;
    }

    public void check(File file) {
        if (!file.exists()) {
            LOGGER.error("File {} does not exist.", file);
            return;
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                check(f);
            }
        } else {
            if (file.getName().endsWith(".ttl")) {
                checkFile(file);
            } else {
                LOGGER.info("{} is ignored.", file.getName());
            }
        }
    }

    private void checkFile(File file) {
        LOGGER.info("Checking {}...", file.getAbsolutePath());
        List<Document> documents = readDocuments(file);
        if (documents == null) {
            return;
        }

        String docUri;
        for (Document document : documents) {
            docUri = document.getDocumentURI();
            List<Relation> relations = NIFSemanticsChecker.checkForMultipleRelations(document);
            if (!relations.isEmpty()) {
                for (Relation relation : relations) {
                    LOGGER.warn("Problem in {}: Found relation {} mutliple times.", docUri, relation);
                }
            }
            List<Meaning> meanings = NIFSemanticsChecker.checkForMeaningsOfRelation(document);
            if (!meanings.isEmpty()) {
                for (Meaning meaning : meanings) {
                    LOGGER.warn(
                            "Problem in {}: meanings {} is referred to by a relation but is not available in the document.",
                            docUri, meaning);
                }
            }
            meanings = NIFSemanticsChecker.checkKBUniqueness(document);
            if (!meanings.isEmpty()) {
                for (Meaning meaning : meanings) {
                    LOGGER.warn("Problem in {}: the following meaning has mutliple URIs of the same namespace: {}.",
                            docUri, meaning);
                }
            }
            if (domainRangeDef != null) {
                relations = NIFSemanticsChecker.checkDomainRange(document, domainRangeDef);
                if (!relations.isEmpty()) {
                    for (Relation relation : relations) {
                        LOGGER.warn(
                                "Problem in {}: Found relation {} where the domain and/or range of the property are violated.",
                                docUri, relation);
                    }
                }
            }
            Set<TypedNamedEntity> tnes = new HashSet<TypedNamedEntity>(document.getMarkings(TypedNamedEntity.class));
            List<NamedEntity> nes = document.getMarkings(NamedEntity.class);
            for(NamedEntity ne : nes) {
                if(!tnes.contains(ne)) {
                    LOGGER.warn(
                            "Problem in {}: Found named entity {} without a type.",
                            docUri, ne);
                }
            }
        }
    }

    private List<Document> readDocuments(File file) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            return parser.parseNIF(is);
        } catch (Exception e) {
            LOGGER.error("Exception while reading the file.", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
        return null;
    }

    private static Map<String, DomainRange> readDomainRangeDef(String file) {
        Model model = ModelFactory.createDefaultModel();
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            model.read(is, "", "TURTLE");
            StmtIterator iterator;
            Statement statement;
            DomainRange domainRange;
            Map<String, DomainRange> domainRangeDef = new HashMap<>();
            Set<String> types;
            iterator = model.listStatements(null, RDFS.domain, (RDFNode) null);
            while (iterator.hasNext()) {
                statement = iterator.next();
                if (domainRangeDef.containsKey(statement.getSubject().getURI())) {
                    domainRange = domainRangeDef.get(statement.getSubject().getURI());
                } else {
                    domainRange = new DomainRange(null, null);
                    domainRangeDef.put(statement.getSubject().getURI(), domainRange);
                }
                types = domainRange.getDomain();
                if (types == null) {
                    types = new HashSet<String>();
                    domainRange.setDomain(types);
                }
                if (statement.getObject().isResource()) {
                    types.add(statement.getObject().asResource().getURI());
                }
            }
            iterator = model.listStatements(null, RDFS.range, (RDFNode) null);
            while (iterator.hasNext()) {
                statement = iterator.next();
                if (domainRangeDef.containsKey(statement.getSubject().getURI())) {
                    domainRange = domainRangeDef.get(statement.getSubject().getURI());
                } else {
                    domainRange = new DomainRange(null, null);
                    domainRangeDef.put(statement.getSubject().getURI(), domainRange);
                }
                types = domainRange.getRange();
                if (types == null) {
                    types = new HashSet<String>();
                    domainRange.setRange(types);
                }
                if (statement.getObject().isResource()) {
                    types.add(statement.getObject().asResource().getURI());
                }
            }
            return domainRangeDef;
        } catch (Exception e) {
            LOGGER.error("Exception while reading the file.", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
        return null;
    }
}
