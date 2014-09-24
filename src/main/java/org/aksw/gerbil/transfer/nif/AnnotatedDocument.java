package org.aksw.gerbil.transfer.nif;

import java.util.List;

public interface AnnotatedDocument {
	
	// FIXME add language

	public String getDocumentURI();

	public void setDocumentURI(String uri);

	public String getText();

	public void setText(String text);

	public List<Annotation> getAnnotations();

	public void setAnnotations(List<Annotation> annotations);

	public void addAnnotation(Annotation annotation);
}
