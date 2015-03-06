package org.aksw.gerbil.io.nif;

import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;

import com.hp.hpl.jena.rdf.model.Model;

public class DocumentListWriter {

    private DocumentWriter documentWriter = new DocumentWriter();

    public void writeDocumentsToModel(Model nifModel, List<Document> documents) {
        for (Document document : documents) {
            documentWriter.writeDocumentToModel(nifModel, document);
        }
    }
}
