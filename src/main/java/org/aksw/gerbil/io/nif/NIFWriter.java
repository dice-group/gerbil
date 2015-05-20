package org.aksw.gerbil.io.nif;

import java.io.OutputStream;
import java.io.Writer;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;

public interface NIFWriter {

    public String writeNIF(List<Document> document);

    public void writeNIF(List<Document> document, Writer writer);

    public void writeNIF(List<Document> document, OutputStream os);

    public String getHttpContentType();

}
