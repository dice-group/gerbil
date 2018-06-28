package org.aksw.gerbil.io.nif.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningEqualityChecker;
import org.aksw.gerbil.transfer.nif.Relation;
import org.aksw.gerbil.transfer.nif.TypedMarking;

public class NIFSemanticsChecker {

    /**
     * Checks the given document for relations that are expressed multiple times.
     * 
     * @param document
     * @return the list of relations that are expressed multiple times
     */
    public static List<Relation> checkForMultipleRelations(Document document) {
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
    public static List<Meaning> checkForMeaningsOfRelation(Document document) {
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
            // m = r.getPredicate();
            // if (m != null) {
            // if (!MeaningEqualityChecker.overlaps(meaningUris, m.getUris())) {
            // missingMeanings.add(m);
            // }
            // }
            m = r.getObject();
            if (m != null) {
                if (!MeaningEqualityChecker.overlaps(meaningUris, m.getUris())) {
                    missingMeanings.add(m);
                }
            }
        }
        return missingMeanings;
    }

    public static List<Meaning> checkKBUniqueness(Document document) {
        List<Meaning> faultyMeanings = new ArrayList<Meaning>();
        Set<String> namespaces = new HashSet<String>();
        String namespace;
        for (Meaning m : document.getMarkings(Meaning.class)) {
            namespaces.clear();
            for (String uri : m.getUris()) {
                namespace = getNamespace(uri);
                if (!namespaces.add(namespace)) {
                    faultyMeanings.add(m);
                }
            }
        }
        return faultyMeanings;
    }

    public static String getNamespace(String uri) {
        int pos = uri.indexOf('#');
        if (pos < 0) {
            pos = uri.lastIndexOf('/');
            if (pos < 0) {
                return uri;
            }
        }
        return uri.substring(0, pos + 1);
    }

    public static List<Relation> checkDomainRange(Document document, Map<String, DomainRange> domainRangeDef) {
        Map<Meaning, Set<String>> typeCache = new HashMap<Meaning, Set<String>>();
        boolean success;
        List<Relation> drErrors = new ArrayList<Relation>();
        DomainRange domainRange;
        for (Relation r : document.getMarkings(Relation.class)) {
            if (domainRangeDef.containsKey(r.getPredicate().getUri())) {
                domainRange = domainRangeDef.get(r.getPredicate().getUri());
                success = true;
                if (domainRange.getDomain() != null) {
                    success = checkType(document, r.getSubject(), domainRange.getDomain(), typeCache);
                }
                if (success && (domainRange.getRange() != null)) {
                    success = checkType(document, r.getObject(), domainRange.getRange(), typeCache);
                }
                if (!success) {
                    drErrors.add(r);
                }
            }
        }
        return drErrors;
    }

    private static boolean checkType(Document document, Meaning meaning, Set<String> expectedTypes,
            Map<Meaning, Set<String>> typeCache) {
        Set<String> types;
        if (typeCache.containsKey(meaning)) {
            types = typeCache.get(meaning);
        } else {
            types = findTypes(document, meaning);
            typeCache.put(meaning, types);
        }
        return types.isEmpty() || MeaningEqualityChecker.overlaps(types, expectedTypes);
    }

    private static Set<String> findTypes(Document document, Meaning meaning) {
        Set<String> types = new HashSet<>();
        for (TypedMarking typed : document.getMarkings(TypedMarking.class)) {
            if ((typed instanceof Meaning) && (MeaningEqualityChecker.overlaps(meaning, (Meaning) typed))) {
                types.addAll(typed.getTypes());
            }
        }
        return types;
    }
    
    public static class DomainRange {
        private Set<String> domain;
        private Set<String> range;
        public DomainRange(Set<String> domain, Set<String> range) {
            super();
            this.domain = domain;
            this.range = range;
        }
        /**
         * @return the domain
         */
        public Set<String> getDomain() {
            return domain;
        }
        /**
         * @param domain the domain to set
         */
        public void setDomain(Set<String> domain) {
            this.domain = domain;
        }
        /**
         * @return the range
         */
        public Set<String> getRange() {
            return range;
        }
        /**
         * @param range the range to set
         */
        public void setRange(Set<String> range) {
            this.range = range;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("DomainRange [domain=");
            builder.append(domain);
            builder.append(", range=");
            builder.append(range);
            builder.append("]");
            return builder.toString();
        }
        
    }
}
