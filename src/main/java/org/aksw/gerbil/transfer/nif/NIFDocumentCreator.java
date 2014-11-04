package org.aksw.gerbil.transfer.nif;


public interface NIFDocumentCreator {

	public String getDocumentAsNIFString(Document document);
	
	public String getHttpContentType();
}
