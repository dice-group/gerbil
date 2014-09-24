package org.aksw.gerbil.transfer.nif.data;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.transfer.nif.AnnotatedDocument;
import org.aksw.gerbil.transfer.nif.Annotation;

public class AnnotatedDocumentImpl implements AnnotatedDocument {

	private String uri;
	private String text;
	private List<Annotation> annotations;

	public AnnotatedDocumentImpl() {
		annotations = new ArrayList<Annotation>();
	}

	public AnnotatedDocumentImpl(String text) {
		this.text = text;
		annotations = new ArrayList<Annotation>();
	}

	public AnnotatedDocumentImpl(String text, List<Annotation> annotations) {
		this.text = text;
		this.annotations = annotations;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public List<Annotation> getAnnotations() {
		return annotations;
	}

	@Override
	public void setAnnotations(List<Annotation> annotations) {
		this.annotations = annotations;
	}

	@Override
	public void addAnnotation(Annotation annotation) {
		annotations.add(annotation);
	}

	@Override
	public String getDocumentURI() {
		return uri;
	}

	@Override
	public void setDocumentURI(String uri) {
		this.uri = uri;
	}

}
