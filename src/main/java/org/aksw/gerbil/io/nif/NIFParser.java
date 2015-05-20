package org.aksw.gerbil.io.nif;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;

public interface NIFParser {

    public List<Document> parseNIF(String nifString);

    public List<Document> parseNIF(Reader reader);
    
    public List<Document> parseNIF(InputStream is);

    public String getHttpContentType();
}
