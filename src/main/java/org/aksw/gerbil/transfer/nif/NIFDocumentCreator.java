package org.aksw.gerbil.transfer.nif;


public interface NIFDocumentCreator {

	public String getDocumentAsNIFString(AnnotatedDocument document);
	
	public String getHttpContentType();
}
