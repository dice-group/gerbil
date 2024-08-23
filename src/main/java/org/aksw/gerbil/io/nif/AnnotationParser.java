/**
 * This file is part of NIF transfer library for the General Entity Annotator
 * Benchmark.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is free
 * software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NIF transfer library for the General Entity Annotator Benchmark.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.io.nif;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.MarkingBuilder;
import org.aksw.gerbil.transfer.nif.ProvenanceInfo;
import org.aksw.gerbil.transfer.nif.data.ProvenanceInfoImpl;
import org.aksw.gerbil.transfer.nif.vocabulary.ITSRDF;
import org.aksw.gerbil.transfer.nif.vocabulary.NIF;
import org.aksw.gerbil.transfer.nif.vocabulary.OA;
import org.aksw.gerbil.transfer.nif.vocabulary.PROV;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationParser.class);

    private boolean removeUsedProperties;

    public AnnotationParser() {
        this(false);
    }

    public AnnotationParser(final boolean removeUsedProperties) {
        this.removeUsedProperties = removeUsedProperties;
    }

    public void parseAnnotations(final Model nifModel, final Document document, final Resource documentResource) {
        // get the annotations from the model

        List<Marking> markings = document.getMarkings();
        ResIterator resIter = nifModel.listSubjectsWithProperty(RDF.type, PROV.Activity);
        Map<String, ProvenanceInfo> provenanceInfos = new HashMap<String, ProvenanceInfo>();
        Resource annotationResource;
        while (resIter.hasNext()) {
            annotationResource = resIter.next();
            Calendar startedAt = getDateTimeValue(nifModel, annotationResource, PROV.startedAtTime);
            Calendar endedAt = getDateTimeValue(nifModel, annotationResource, PROV.endedAtTime);
            Set<String> agents = null;

            resIter = nifModel.listSubjectsWithProperty(NIF.referenceContext, documentResource);
            while (resIter.hasNext()) {
                if (agents == null) {
                    agents = new HashSet<>();
                }
                agents.add(resIter.next().toString());
            }
            provenanceInfos.put(annotationResource.getURI(), new ProvenanceInfoImpl(startedAt, endedAt, agents));
        }
        // parse annotations pointing to the document
        resIter = nifModel.listSubjectsWithProperty(NIF.referenceContext, documentResource);
        parseNifAnnotations(nifModel, resIter, provenanceInfos, markings);
        // parse annotations to which the document points to
        parseNifAnnotations(nifModel, nifModel.listObjectsOfProperty(documentResource, NIF.topic), provenanceInfos,
                markings);
        parseOAAnnotations(nifModel, documentResource, provenanceInfos, markings);
    }

    protected void parseNifAnnotations(Model nifModel, ExtendedIterator<? extends RDFNode> iterator,
            Map<String, ProvenanceInfo> provenanceInfos, List<Marking> markings) {
        Resource annotationResource;
        NodeIterator nodeIter;
        MarkingBuilder builder = Marking.builder();
        while (iterator.hasNext()) {
            builder.clear();
            annotationResource = iterator.next().asResource();

            builder.setProvenance(findProvenance(nifModel, annotationResource, provenanceInfos));

            nodeIter = nifModel.listObjectsOfProperty(annotationResource, NIF.beginIndex);
            if (nodeIter.hasNext()) {
                builder.setStart(nodeIter.next().asLiteral().getInt());
            }
            nodeIter = nifModel.listObjectsOfProperty(annotationResource, NIF.endIndex);
            if (nodeIter.hasNext()) {
                builder.setEnd(nodeIter.next().asLiteral().getInt());
            }
            // nif:Word is not really used
            // boolean isWord = nifModel.contains(annotationResource, RDF.type, NIF.Word);
            nodeIter = nifModel.listObjectsOfProperty(annotationResource, ITSRDF.taIdentRef);
            if (nodeIter.hasNext()) {
                Resource meaning;
                while (nodeIter.hasNext()) {
                    meaning = nodeIter.next().asResource();
                    builder.addMeaning(meaning.getURI());
                    addTypeInformation(nifModel, meaning, builder);
                }
            }
            nodeIter = nifModel.listObjectsOfProperty(annotationResource, ITSRDF.taClassRef);
            while (nodeIter.hasNext()) {
                builder.addType(nodeIter.next().toString());
            }
            nodeIter = nifModel.listObjectsOfProperty(annotationResource, ITSRDF.taConfidence);
            if (nodeIter.hasNext()) {
                builder.setConfidence(nodeIter.next().asLiteral().getDouble());
            }
            // Check whether it is a relation
            if (nifModel.contains(annotationResource, RDF.type, RDF.Statement)) {
                nodeIter = nifModel.listObjectsOfProperty(annotationResource, RDF.subject);
                if (nodeIter.hasNext()) {
                    builder.addSubject(nodeIter.next().asNode().toString());
                }
                nodeIter = nifModel.listObjectsOfProperty(annotationResource, RDF.predicate);
                if (nodeIter.hasNext()) {
                    builder.addPredicate(nodeIter.next().asNode().toString());
                }
                nodeIter = nifModel.listObjectsOfProperty(annotationResource, RDF.object);
                if (nodeIter.hasNext()) {
                    builder.addObject(nodeIter.next().asNode().toString());
                }
            }
            Marking marking = builder.build();
            if (marking != null) {
                markings.add(marking);
            } else {
                // The annotation is incomplete
                LOGGER.warn("Found an incomplete annotation resource (\"" + annotationResource.getURI()
                        + "\"). This annotation will be ignored.");
            }
            if (removeUsedProperties) {
                nifModel.removeAll(annotationResource, null, null);
            }
        }
    }

    protected ProvenanceInfo findProvenance(Model nifModel, Resource resource,
            Map<String, ProvenanceInfo> provenanceInfos) {
        NodeIterator nodeIter = nifModel.listObjectsOfProperty(resource, PROV.wasGeneratedBy);
        if (nodeIter.hasNext()) {
            String provUri = nodeIter.next().asResource().getURI();
            if (provenanceInfos.containsKey(provUri)) {
                return provenanceInfos.get(provUri);
            } else {
                LOGGER.warn("Found a link to a non existing provenance information \"{}\". It will be ignored",
                        provUri);
            }
        }
        ResIterator resIter = nifModel.listResourcesWithProperty(PROV.generated, resource);
        if (resIter.hasNext()) {
            String provUri = resIter.next().getURI();
            if (provenanceInfos.containsKey(provUri)) {
                return provenanceInfos.get(provUri);
            } else {
                LOGGER.warn("Found a link to a non existing provenance information \"{}\". It will be ignored",
                        provUri);
            }
        }
        return null;
    }

    protected void parseOAAnnotations(Model nifModel, Resource documentResource,
            Map<String, ProvenanceInfo> provenanceInfos, List<Marking> markings) {
        ResIterator resIter = nifModel.listSubjectsWithProperty(OA.hasSource, documentResource);
        MarkingBuilder builder = Marking.builder();
        Marking marking = null;
        while (resIter.hasNext()) {
            // Subject is blank node object for hasTarget
            ResIterator sourceIter = nifModel.listSubjectsWithProperty(OA.hasTarget, resIter.next());
            while (sourceIter.hasNext()) {
                // Subject is blank node for one relation annotation
                Resource relationStmtNode = sourceIter.next();
                if (nifModel.contains(relationStmtNode, RDF.type, RDF.Statement)) {
                    builder.clear();
                    // get statements
                    builder.addSubject(
                            nifModel.listObjectsOfProperty(relationStmtNode, RDF.subject).next().asNode().toString());
                    builder.addPredicate(
                            nifModel.listObjectsOfProperty(relationStmtNode, RDF.predicate).next().asNode().toString());
                    builder.addObject(
                            nifModel.listObjectsOfProperty(relationStmtNode, RDF.object).next().asNode().toString());
                    marking = builder.build();
                    if (marking != null) {
                        markings.add(marking);
                    }
                }
            }
        }
    }

//    protected void parseDirectAnnotations(Model nifModel, Resource documentResource,
//            Map<String, ProvenanceInfo> provenanceInfos, List<Marking> markings) {
//        NodeIterator annotationIter = nifModel.listObjectsOfProperty(documentResource, NIF.topic);
//        while (annotationIter.hasNext()) {
//            marking = null;
//            localProv = null;
//            annotationResource = annotationIter.next().asResource();
//            nodeIter = nifModel.listObjectsOfProperty(annotationResource, PROV.wasGeneratedBy);
//            if (nodeIter.hasNext()) {
//                String provUri = nodeIter.next().asResource().getURI();
//                if (provenanceInfos.containsKey(provUri)) {
//                    localProv = provenanceInfos.get(provUri);
//                } else {
//                    LOGGER.warn("Found a link to a non existing provenance information \"{}\". It will be ignored",
//                            provUri);
//                }
//            }
//            nodeIter = nifModel.listObjectsOfProperty(annotationResource, ITSRDF.taIdentRef);
//            if (nodeIter.hasNext()) {
//                entityUris = new HashSet<>();
//                while (nodeIter.hasNext()) {
//                    entityUris.add(nodeIter.next().toString());
//                }
//                nodeIter = nifModel.listObjectsOfProperty(annotationResource, ITSRDF.taConfidence);
//                if (nodeIter.hasNext()) {
//                    confidence = nodeIter.next().asLiteral().getDouble();
//                    marking = new ScoredAnnotation(entityUris, confidence);
//                } else {
//                    marking = new Annotation(entityUris);
//                }
//            }
//        }
//        markings.addAll(usedProvInfos);
//    }

    protected void addTypeInformation(final Model nifModel, Resource meaning, MarkingBuilder builder) {
        NodeIterator nodeIter = nifModel.listObjectsOfProperty(meaning, RDF.type);
        while (nodeIter.hasNext()) {
            builder.addType(nodeIter.next().asResource().getURI());
        }
    }

    /**
     * Returns the object as {@link Calendar} of the first triple that has the given
     * subject and predicate and that can be found in the given model.
     *
     * @param model     the model that should contain the triple
     * @param subject   the subject of the triple. <code>null</code> works like a
     *                  wildcard.
     * @param predicate the predicate of the triple. <code>null</code> works like a
     *                  wildcard.
     * @return object of the triple as {@link Calendar} or <code>null</code> if such
     *         a triple couldn't be found or the value can not be read as XSDDate
     */
    public static Calendar getDateValue(Model model, Resource subject, Property predicate) {
        Calendar result = getCalendarValue(model, subject, predicate, XSDDatatype.XSDdate);
        if (result != null) {
            result.setTimeZone(TimeZone.getDefault());
        }
        return result;
    }

    /**
     * Returns the object as {@link Calendar} of the first triple that has the given
     * subject and predicate and that can be found in the given model.
     *
     * @param model     the model that should contain the triple
     * @param subject   the subject of the triple. <code>null</code> works like a
     *                  wildcard.
     * @param predicate the predicate of the triple. <code>null</code> works like a
     *                  wildcard.
     * @return object of the triple as {@link Calendar} or <code>null</code> if such
     *         a triple couldn't be found or the value can not be read as
     *         XSDDateTime
     */
    public static Calendar getDateTimeValue(Model model, Resource subject, Property predicate) {
        return getCalendarValue(model, subject, predicate, XSDDatatype.XSDdateTime);
    }

    protected static Calendar getCalendarValue(Model model, Resource subject, Property predicate,
            XSDDatatype dateType) {
        if (model == null) {
            return null;
        }
        Literal literal = null;
        NodeIterator nodeIter = model.listObjectsOfProperty(subject, predicate);
        if (nodeIter.hasNext()) {
            literal = nodeIter.next().asLiteral();
        }
        if (literal != null) {
            try {
                Object o = dateType.parse(literal.getString());
                if (o instanceof XSDDateTime) {
                    return ((XSDDateTime) o).asCalendar();
                }
            } catch (Exception e) {
                // nothing to do
                LOGGER.debug("Couldn't parse " + dateType.getURI() + ". Returning null.", e);
            }
        }
        return null;
    }

}
