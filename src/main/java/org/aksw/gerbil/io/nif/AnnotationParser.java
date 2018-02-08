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
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Relation;
import org.aksw.gerbil.transfer.nif.ProvenanceInfo;
import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.RelationImpl;
import org.aksw.gerbil.transfer.nif.data.ProvenanceInfoImpl;
import org.aksw.gerbil.transfer.nif.data.RelationImpl;
import org.aksw.gerbil.transfer.nif.data.ScoredAnnotation;
import org.aksw.gerbil.transfer.nif.data.ScoredNamedEntity;
import org.aksw.gerbil.transfer.nif.data.ScoredRelationImpl;
import org.aksw.gerbil.transfer.nif.data.ScoredTypedNamedEntity;
import org.aksw.gerbil.transfer.nif.data.SpanImpl;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.aksw.gerbil.transfer.nif.data.TypedSpanImpl;
import org.aksw.gerbil.transfer.nif.vocabulary.ITSRDF;
import org.aksw.gerbil.transfer.nif.vocabulary.NIF;
import org.aksw.gerbil.transfer.nif.vocabulary.OA;
import org.aksw.gerbil.transfer.nif.vocabulary.PROV;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
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
		NodeIterator nodeIter;
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

		Set<ProvenanceInfo> usedProvInfos = new HashSet<>();
		int start, end;
		Set<String> entityUris;
		double confidence;
		resIter = nifModel.listSubjectsWithProperty(NIF.referenceContext, documentResource);
		ProvenanceInfo localProv;
		Marking marking;
		while (resIter.hasNext()) {

			annotationResource = resIter.next();

			marking = null;
			localProv = null;
			nodeIter = nifModel.listObjectsOfProperty(annotationResource, PROV.wasGeneratedBy);
			if (nodeIter.hasNext()) {
				String provUri = nodeIter.next().asResource().getURI();
				if (provenanceInfos.containsKey(provUri)) {
					localProv = provenanceInfos.get(provUri);
				} else {
					LOGGER.warn("Found a link to a non existing provenance information \"{}\". It will be ignored",
							provUri);
				}
			}
			if (localProv == null) {
				ResIterator resIter2 = nifModel.listResourcesWithProperty(PROV.generated, annotationResource);
				if (resIter2.hasNext()) {
					String provUri = resIter2.next().getURI();
					if (provenanceInfos.containsKey(provUri)) {
						localProv = provenanceInfos.get(provUri);
					} else {
						LOGGER.warn("Found a link to a non existing provenance information \"{}\". It will be ignored",
								provUri);
					}
				}
			}

			start = end = -1;
			nodeIter = nifModel.listObjectsOfProperty(annotationResource, NIF.beginIndex);
			if (nodeIter.hasNext()) {
				start = nodeIter.next().asLiteral().getInt();
			}
			nodeIter = nifModel.listObjectsOfProperty(annotationResource, NIF.endIndex);
			if (nodeIter.hasNext()) {
				end = nodeIter.next().asLiteral().getInt();
			}
			if ((start >= 0) && (end >= 0)) {
				boolean isWord = nifModel.contains(annotationResource, RDF.type, NIF.Word);

				nodeIter = nifModel.listObjectsOfProperty(annotationResource, ITSRDF.taIdentRef);
				if (nodeIter.hasNext()) {
					entityUris = new HashSet<>();
					while (nodeIter.hasNext()) {
						entityUris.add(nodeIter.next().toString());
					}
					nodeIter = nifModel.listObjectsOfProperty(annotationResource, ITSRDF.taClassRef);
					if (nodeIter.hasNext()) {
						Set<String> types = new HashSet<>();
						while (nodeIter.hasNext()) {
							types.add(nodeIter.next().toString());
						}
						nodeIter = nifModel.listObjectsOfProperty(annotationResource, ITSRDF.taConfidence);
						if (nodeIter.hasNext()) {
							confidence = nodeIter.next().asLiteral().getDouble();
							marking = addTypeInformation(new ScoredTypedNamedEntity(start, end - start, entityUris,
									types, confidence, isWord), nifModel);
						} else {
							// It has been typed without a confidence
							marking = addTypeInformation(
									new TypedNamedEntity(start, end - start, entityUris, types, isWord), nifModel);
						}
					} else {
						nodeIter = nifModel.listObjectsOfProperty(annotationResource, ITSRDF.taConfidence);
						if (nodeIter.hasNext()) {
							confidence = nodeIter.next().asLiteral().getDouble();
							marking = addTypeInformationIfPossible(
									new ScoredNamedEntity(start, end - start, entityUris, confidence, isWord),
									nifModel);
						} else {
							// It has been disambiguated without a confidence
							marking = addTypeInformationIfPossible(
									new NamedEntity(start, end - start, entityUris, isWord), nifModel);
						}
					}
				} else {
					nodeIter = nifModel.listObjectsOfProperty(annotationResource, ITSRDF.taClassRef);
					if (nodeIter.hasNext()) {
						Set<String> types = new HashSet<>();
						while (nodeIter.hasNext()) {
							types.add(nodeIter.next().toString());
						}
						// It has been typed without a confidence
						marking = new TypedSpanImpl(start, end - start, types, isWord);
					} else {
						// It is a named entity that hasn't been disambiguated
						marking = new SpanImpl(start, end - start, isWord);
					}
				}
				// FIXME scored Span is missing
			} else {
				// Check whether it is a relation
				if (nifModel.contains(annotationResource, RDF.type, RDF.Statement)) {
					Node s = null, p = null, o = null;
					nodeIter = nifModel.listObjectsOfProperty(annotationResource, RDF.subject);
					if (nodeIter.hasNext()) {
						s = nodeIter.next().asNode();
					}
					nodeIter = nifModel.listObjectsOfProperty(annotationResource, RDF.predicate);
					if (nodeIter.hasNext()) {
						p = nodeIter.next().asNode();
					}
					nodeIter = nifModel.listObjectsOfProperty(annotationResource, RDF.object);
					if (nodeIter.hasNext()) {
						o = nodeIter.next().asNode();
					}
					if ((s != null) && (p != null) && (o != null)) {
						nodeIter = nifModel.listObjectsOfProperty(annotationResource, ITSRDF.taConfidence);
						if (nodeIter.hasNext()) {
							confidence = nodeIter.next().asLiteral().getDouble();
							marking = new ScoredRelationImpl(new Triple(s, p, o), confidence);
						} else {
							// It has been disambiguated without a confidence
							marking = new RelationImpl(s.toString(), p.toString(), o.toString());
						}
					} else {
						// The relation is incomplete
						LOGGER.warn("Found an incomplete relation resource (\"" + annotationResource.getURI()
								+ "\") with a missing subject, predicate or object. This annotation will be ignored.");
					}
				} else {
					LOGGER.warn("Found an annotation resource (\"" + annotationResource.getURI()
							+ "\") without a start or end index. This annotation will be ignored.");
				}
			}
			if (marking != null) {
				if (localProv != null) {
					marking.setProvenanceInfo(localProv);
					usedProvInfos.add(localProv);
				}
				markings.add(marking);
			}
			if (removeUsedProperties) {
				nifModel.removeAll(annotationResource, null, null);
			}
		}

		NodeIterator annotationIter = nifModel.listObjectsOfProperty(documentResource, NIF.topic);
		while (annotationIter.hasNext()) {
			marking = null;
			localProv = null;
			annotationResource = annotationIter.next().asResource();
			nodeIter = nifModel.listObjectsOfProperty(annotationResource, PROV.wasGeneratedBy);
			if (nodeIter.hasNext()) {
				String provUri = nodeIter.next().asResource().getURI();
				if (provenanceInfos.containsKey(provUri)) {
					localProv = provenanceInfos.get(provUri);
				} else {
					LOGGER.warn("Found a link to a non existing provenance information \"{}\". It will be ignored",
							provUri);
				}
			}
			nodeIter = nifModel.listObjectsOfProperty(annotationResource, ITSRDF.taIdentRef);
			if (nodeIter.hasNext()) {
				entityUris = new HashSet<>();
				while (nodeIter.hasNext()) {
					entityUris.add(nodeIter.next().toString());
				}
				nodeIter = nifModel.listObjectsOfProperty(annotationResource, ITSRDF.taConfidence);
				if (nodeIter.hasNext()) {
					confidence = nodeIter.next().asLiteral().getDouble();
					marking = new ScoredAnnotation(entityUris, confidence);
				} else {
					marking = new Annotation(entityUris);
				}
			}
			if (marking != null) {
				if (localProv != null) {
					marking.setProvenanceInfo(localProv);
					usedProvInfos.add(localProv);
				}
				markings.add(marking);
			}
		}
		resIter = nifModel.listSubjectsWithProperty(OA.hasSource, documentResource);
		while (resIter.hasNext()) {
			// Subject is blank node object for hasTarget
			ResIterator sourceIter = nifModel.listSubjectsWithProperty(OA.hasTarget, resIter.next());
			while (sourceIter.hasNext()) {
				// Subject is blank node for one relation annotation
				Resource relationStmtNode = sourceIter.next();
				if (nifModel.contains(relationStmtNode, ResourceFactory.createProperty("http://www.w3.org/2005/Atom"),
						RDF.Statement)) {
					//get statements
					Node subject = nifModel.listObjectsOfProperty(relationStmtNode, RDF.subject).next().asNode();
					Node predicate = nifModel.listObjectsOfProperty(relationStmtNode, RDF.predicate).next().asNode();
					Node object = nifModel.listObjectsOfProperty(relationStmtNode, RDF.object).next().asNode();
					Relation relation = new RelationImpl(subject.toString(), predicate.toString(), object.toString(true));
					markings.add(relation);
				}
				
			}
		}
		markings.addAll(usedProvInfos);
	}

	private MeaningSpan addTypeInformationIfPossible(final NamedEntity ne, final Model nifModel) {
		TypedNamedEntity typedNE = new TypedNamedEntity(ne.getStartPosition(), ne.getLength(), ne.getUris(),
				new HashSet<String>(), ne.getIsWord());
		addTypeInformation(typedNE, nifModel);
		if (typedNE.getTypes().size() > 0) {
			return typedNE;
		} else {
			return ne;
		}
	}

	private MeaningSpan addTypeInformationIfPossible(final ScoredNamedEntity ne, final Model nifModel) {
		ScoredTypedNamedEntity typedNE = new ScoredTypedNamedEntity(ne.getStartPosition(), ne.getLength(), ne.getUris(),
				new HashSet<String>(), ne.getConfidence(), ne.getIsWord());
		addTypeInformation(typedNE, nifModel);
		if (typedNE.getTypes().size() > 0) {
			return typedNE;
		} else {
			return ne;
		}
	}

	private TypedNamedEntity addTypeInformation(final TypedNamedEntity typedNE, final Model nifModel) {
		for (String uri : typedNE.getUris()) {
			NodeIterator nodeIter = nifModel.listObjectsOfProperty(nifModel.getResource(uri), RDF.type);
			Set<String> types = typedNE.getTypes();
			while (nodeIter.hasNext()) {
				types.add(nodeIter.next().asResource().getURI());
			}
		}
		return typedNE;
	}

	/**
	 * Returns the object as {@link Calendar} of the first triple that has the given
	 * subject and predicate and that can be found in the given model.
	 *
	 * @param model
	 *            the model that should contain the triple
	 * @param subject
	 *            the subject of the triple. <code>null</code> works like a
	 *            wildcard.
	 * @param predicate
	 *            the predicate of the triple. <code>null</code> works like a
	 *            wildcard.
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
	 * @param model
	 *            the model that should contain the triple
	 * @param subject
	 *            the subject of the triple. <code>null</code> works like a
	 *            wildcard.
	 * @param predicate
	 *            the predicate of the triple. <code>null</code> works like a
	 *            wildcard.
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
