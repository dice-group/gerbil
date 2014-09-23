package org.aksw.gerbil.bat.annotator.nif;

import it.acubelab.batframework.data.Mention;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.transfer.nif.AnnotatedDocument;
import org.aksw.gerbil.transfer.nif.Annotation;
import org.aksw.gerbil.transfer.nif.data.AnnotatedDocumentImpl;
import org.aksw.gerbil.transfer.nif.data.AnnotationImpl;
import org.aksw.gerbil.transfer.nif.data.DisambiguatedAnnotation;
import org.aksw.gerbil.transfer.nif.data.ScoredDisambigAnnotation;

public class BAT2NIF_TranslationHelper {

	public static AnnotatedDocument createAnnotatedDocument(String text,
			HashSet<Mention> mentions) {
		List<Annotation> annotations = new ArrayList<Annotation>();
		for (Mention mention : mentions) {
			annotations.add(translateMention2Annotation(mention));
		}
		return new AnnotatedDocumentImpl(text, annotations);
	}

	public static Annotation translateMention2Annotation(Mention mention) {
		return new AnnotationImpl(mention.getPosition(), mention.getLength());
	}

	public static HashSet<it.acubelab.batframework.data.Annotation> createAnnotations(
			AnnotatedDocument document) {
		List<Annotation> annotations = document.getAnnotations();
		HashSet<it.acubelab.batframework.data.Annotation> batAnnotations = new HashSet<it.acubelab.batframework.data.Annotation>();
		for (Annotation annotation : annotations) {
			batAnnotations.add(translateAnnotation2BatAnnotation(annotation));
		}
		return null;
	}

	public static it.acubelab.batframework.data.Annotation translateAnnotation2BatAnnotation(
			Annotation annotation) {
		// if this is a scored annotation
		if (annotation instanceof ScoredDisambigAnnotation) {
			return translateScoredAnnotation2BatAnnotation((ScoredDisambigAnnotation) annotation);
		} else if (annotation instanceof DisambiguatedAnnotation) {
			DisambiguatedAnnotation disAnnotation = (DisambiguatedAnnotation) annotation;
			// FIXME here we need to retrieve the Wikipedia Id for the DBPedia
			// URI (maybe take a look into the spotlight annotator
		}
		return null;
	}

	public static it.acubelab.batframework.data.Annotation translateScoredAnnotation2BatAnnotation(
			ScoredDisambigAnnotation annotation) {
		// FIXME Here we could get a problem if there are multiple scored
		// annotations for the same mention, because the current algorithm would
		// only set the first (or the last) one.
		return null;
	}
}
