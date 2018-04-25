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

import java.util.Arrays;
import java.util.Set;

import org.aksw.gerbil.io.nif.utils.NIFUriHelper;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.ProvenanceInfo;
import org.aksw.gerbil.transfer.nif.Relation;
import org.aksw.gerbil.transfer.nif.ScoredMarking;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TypedMarking;
import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.aksw.gerbil.transfer.nif.data.ScoredAnnotation;
import org.aksw.gerbil.transfer.nif.vocabulary.ITSRDF;
import org.aksw.gerbil.transfer.nif.vocabulary.NIF;
import org.aksw.gerbil.transfer.nif.vocabulary.PROV;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;

public class AnnotationWriter {

    // FIXME this method is not used in the DocumentWriter
    public void writeMarkingToModel(final Model nifModel, final Resource documentResource, final String text,
            final String documentURI, final Marking marking, final int markingId) {
        if (marking instanceof Span) {
            addSpan(nifModel, documentResource, text, documentURI, (Span) marking);
        } else if (marking instanceof Relation) {
            addRelation(nifModel, documentResource, documentURI, (Relation) marking, markingId);
        } else if (marking instanceof Meaning) {
            addAnnotation(nifModel, documentResource, documentURI, (Annotation) marking, markingId);
        } else if (marking instanceof ProvenanceInfo) {
            addProvenanceInfo(nifModel, documentResource, documentURI, (ProvenanceInfo) marking);
        }
    }

    public void addAnnotation(final Model nifModel, final Resource documentAsResource, final String documentURI,
            final Annotation annotation, final int annotationId) {
        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(documentURI);
        uriBuilder.append("#annotation");
        uriBuilder.append(annotationId);

        Resource annotationAsResource = nifModel.createResource(uriBuilder.toString());
        nifModel.add(annotationAsResource, RDF.type, NIF.Annotation);
        nifModel.add(documentAsResource, NIF.topic, annotationAsResource);
        for (String meainingUri : annotation.getUris()) {
            nifModel.add(annotationAsResource, ITSRDF.taIdentRef, nifModel.createResource(meainingUri));
        }

        if (annotation instanceof ScoredAnnotation) {
            nifModel.add(annotationAsResource, NIF.confidence,
                    Double.toString(((ScoredAnnotation) annotation).getConfidence()), XSDDatatype.XSDstring);
        }

        addProvenanceInfoLink(annotation, annotationAsResource, nifModel, documentURI);
    }

    public void addProvenanceInfo(Model nifModel, Resource documentResource, String documentURI,
            ProvenanceInfo provenance) {
        String provUri = generateProvenanceInfoUri(provenance, documentURI);
        Resource provenanceAsResource = nifModel.createResource(provUri);
        nifModel.add(provenanceAsResource, RDF.type, PROV.Activity);
        if(provenance.getStartedAt() != null) {
            nifModel.add(provenanceAsResource, PROV.startedAtTime, nifModel.createTypedLiteral(provenance.getStartedAt()));
        }
        if(provenance.getEndedAt() != null) {
            nifModel.add(provenanceAsResource, PROV.endedAtTime, nifModel.createTypedLiteral(provenance.getEndedAt()));
        }
        if(provenance.getAssociatedAgents() != null) {
            for (String agent : provenance.getAssociatedAgents()) {
                Resource agentAsResource = nifModel.createResource(agent);
                nifModel.add(agentAsResource, RDF.type, PROV.Agent);
                nifModel.add(provenanceAsResource, PROV.wasAssociatedWith, agentAsResource);
            }
        }
    }

    public String generateProvenanceInfoUri(ProvenanceInfo provenance, String documentURI) {
        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(documentURI);
        uriBuilder.append("#activity-");
        uriBuilder.append((provenance.getEndedAt() != null) ? provenance.getStartedAt().getTimeInMillis() : "null");
        uriBuilder.append('-');
        uriBuilder.append((provenance.getEndedAt() != null) ? provenance.getEndedAt().getTimeInMillis() : "null");
        uriBuilder.append('-');
        if(provenance.getAssociatedAgents() != null) {
            String[] agents = provenance.getAssociatedAgents().toArray(new String[provenance.getAssociatedAgents().size()]);
            Arrays.sort(agents);
            uriBuilder.append(Arrays.hashCode(agents));
        } else {
            uriBuilder.append("null");
        }
        return uriBuilder.toString();
    }

    public void addRelation(Model nifModel, Resource documentAsResource, String documentURI, Relation relation,
            int markingId) {
        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(documentURI);
        uriBuilder.append("#relation");
        uriBuilder.append(markingId);

        Resource relationAsResource = nifModel.createResource(uriBuilder.toString());
        nifModel.add(relationAsResource, RDF.type, RDF.Statement);
        nifModel.add(relationAsResource, RDF.subject, ResourceFactory.createResource(relation.getSubject().getUris().iterator().next()));
        nifModel.add(relationAsResource, RDF.predicate, ResourceFactory.createResource(relation.getPredicate().getUris().iterator().next()));
        nifModel.add(relationAsResource, RDF.object, ResourceFactory.createResource(relation.getObject().getUris().iterator().next()));
        nifModel.add(relationAsResource, OA.hasTarget,  OA.SpecificResource);
        nifModel.add(relationAsResource, OA.hasSource, "");
        nifModel.add(relationAsResource, NIF.referenceContext, documentAsResource);
        if (relation instanceof ScoredMarking) {
            nifModel.add(relationAsResource, ITSRDF.taConfidence,
                    nifModel.createTypedLiteral(((ScoredMarking) relation).getConfidence(), XSDDatatype.XSDdouble));
        }

        addProvenanceInfoLink(relation, relationAsResource, nifModel, documentURI);
    }

    public void addSpan(final Model nifModel, final Resource documentAsResource, final String text,
            final String documentURI, final Span span) {
        int startInJavaText = span.getStartPosition();
        int endInJavaText = startInJavaText + span.getLength();
        int start = text.codePointCount(0, startInJavaText);
        int end = start + text.codePointCount(startInJavaText, endInJavaText);

        String spanUri = NIFUriHelper.getNifUri(documentURI, start, end);
        Resource spanAsResource = nifModel.createResource(spanUri);
        nifModel.add(spanAsResource, RDF.type, NIF.Phrase);
        nifModel.add(spanAsResource, RDF.type, NIF.String);
        nifModel.add(spanAsResource, RDF.type, NIF.RFC5147String);
        if (span.getIsWord()) {
            nifModel.add(spanAsResource, RDF.type, NIF.Word);
        }
        // TODO add language to String
        nifModel.add(spanAsResource, NIF.anchorOf,
                nifModel.createTypedLiteral(text.substring(startInJavaText, endInJavaText), XSDDatatype.XSDstring));
        nifModel.add(spanAsResource, NIF.beginIndex,
                nifModel.createTypedLiteral(start, XSDDatatype.XSDnonNegativeInteger));
        nifModel.add(spanAsResource, NIF.endIndex, nifModel.createTypedLiteral(end, XSDDatatype.XSDnonNegativeInteger));
        nifModel.add(spanAsResource, NIF.referenceContext, documentAsResource);

        if (span instanceof Meaning) {
            for (String meainingUri : ((Meaning) span).getUris()) {
                nifModel.add(spanAsResource, ITSRDF.taIdentRef, nifModel.createResource(meainingUri));
            }
        }
        if (span instanceof ScoredMarking) {
            nifModel.add(spanAsResource, ITSRDF.taConfidence,
                    nifModel.createTypedLiteral(((ScoredMarking) span).getConfidence(), XSDDatatype.XSDdouble));
        }
        if (span instanceof TypedMarking) {
            Set<String> types = ((TypedMarking) span).getTypes();
            for (String type : types) {
                nifModel.add(spanAsResource, ITSRDF.taClassRef, nifModel.createResource(type));
            }
        }
        
        addProvenanceInfoLink(span, spanAsResource, nifModel, documentURI);
    }
    
    protected void addProvenanceInfoLink(Marking marking, Resource markingResource, Model nifModel, String documentURI) {
        if (marking.getProvenanceInfo() != null) {
            Resource provInfo = nifModel.getResource(generateProvenanceInfoUri(marking.getProvenanceInfo(), documentURI));
            nifModel.add(markingResource, PROV.wasGeneratedBy, provInfo);
            nifModel.add(provInfo, PROV.generated, markingResource);
            nifModel.add(markingResource, RDF.type, PROV.Entity);
        }
    }
}
