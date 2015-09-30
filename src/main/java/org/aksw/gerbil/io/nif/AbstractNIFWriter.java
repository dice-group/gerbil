/**
 * This file is part of NIF transfer library for the General Entity Annotator Benchmark.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NIF transfer library for the General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.io.nif;

import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.NIFTransferPrefixMapping;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public abstract class AbstractNIFWriter implements NIFWriter {

    private String httpContentType;
    private String language;
    private DocumentListWriter writer = new DocumentListWriter();

    public AbstractNIFWriter(String httpContentType, String language) {
        this.httpContentType = httpContentType;
        this.language = language;
    }

    protected Model createNIFModel(List<Document> document) {
        Model nifModel = ModelFactory.createDefaultModel();
        nifModel.setNsPrefixes(NIFTransferPrefixMapping.getInstance());
        writer.writeDocumentsToModel(nifModel, document);
        return nifModel;
    }

    @Override
    public String getHttpContentType() {
        return httpContentType;
    }

    @Override
    public String writeNIF(List<Document> document) {
        StringWriter writer = new StringWriter();
        writeNIF(document, writer);
        return writer.toString();
    }

    @Override
    public void writeNIF(List<Document> document, OutputStream os) {
        Model nifModel = createNIFModel(document);
        nifModel.write(os, language);
    }

    @Override
    public void writeNIF(List<Document> document, Writer writer) {
        Model nifModel = createNIFModel(document);
        nifModel.write(writer, language);
    }
}
