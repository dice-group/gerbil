package org.aksw.gerbil.transfer.nif;

import java.io.Reader;

public interface NIFDocumentParser {

	public Document getDocumentFromNIFString(String nifString)
			throws Exception;

	public Document getDocumentFromNIFReader(Reader reader)
			throws Exception;

	public String getHttpContentType();
}
