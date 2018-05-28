package org.aksw.gerbil.io.nif.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningEqualityChecker;
import org.aksw.gerbil.transfer.nif.Relation;

public class NIFSemanticsChecker {

    /**
     * Checks the given document for relations that are expressed multiple times.
     * 
     * @param document
     * @return the list of relations that are expressed multiple times
     */
    public List<Relation> checkForMultipleRelations(Document document) {
        Set<Relation> relations = new HashSet<Relation>();
        List<Relation> duplicates = new ArrayList<Relation>();
        for (Relation r : document.getMarkings(Relation.class)) {
            if (relations.contains(r)) {
                duplicates.add(r);
            }
        }
        return duplicates;
    }

    /**
     * Checks whether all meanings that are used by the relations of this document
     * are also available as annotations in the document.
     * 
     * @param document
     * @return the list of {@link Meaning} instances that are missing.
     */
    public List<Meaning> checkForMeaningsOfRelation(Document document) {
        Set<String> meaningUris = new HashSet<String>();
        for (Meaning m : document.getMarkings(Meaning.class)) {
            meaningUris.addAll(m.getUris());
        }
        List<Meaning> missingMeanings = new ArrayList<Meaning>();
        Meaning m;
        for (Relation r : document.getMarkings(Relation.class)) {
            m = r.getSubject();
            if (m != null) {
                if (!MeaningEqualityChecker.overlaps(meaningUris, m.getUris())) {
                    missingMeanings.add(m);
                }
            }
            m = r.getPredicate();
            if (m != null) {
                if (!MeaningEqualityChecker.overlaps(meaningUris, m.getUris())) {
                    missingMeanings.add(m);
                }
            }
            m = r.getObject();
            if (m != null) {
                if (!MeaningEqualityChecker.overlaps(meaningUris, m.getUris())) {
                    missingMeanings.add(m);
                }
            }
        }
        return missingMeanings;
    }
    
    public List<Meaning> checkKBUniqueness(Document document) {
        List<Meaning> faultyMeanings = new ArrayList<Meaning>();
        Set<String> namespaces = new HashSet<String>();
        String namespace;
        for (Meaning m : document.getMarkings(Meaning.class)) {
            namespaces.clear();
            for(String uri : m.getUris()) {
                namespace = getNamespace(uri);
                if(!namespaces.add(namespace)) {
                    faultyMeanings.add(m);
                }
            }
        }
        return faultyMeanings;
    }
    
    private static String getNamespace(String uri) {
        int pos = uri.indexOf('#');
        if(pos < 0) {
            pos = uri.lastIndexOf('/');
            if(pos < 0) {
                return uri;
            }
        }
        return uri.substring(0, pos + 1);
    }
}
