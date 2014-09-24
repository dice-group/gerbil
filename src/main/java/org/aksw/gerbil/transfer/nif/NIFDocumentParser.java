package org.aksw.gerbil.transfer.nif;

import java.io.Reader;

public interface NIFDocumentParser {

	public AnnotatedDocument getDocumentFromNIFString(String nifString)
			throws Exception;

	public AnnotatedDocument getDocumentFromNIFReader(Reader reader)
			throws Exception;

	public String getHttpContentType();
}
