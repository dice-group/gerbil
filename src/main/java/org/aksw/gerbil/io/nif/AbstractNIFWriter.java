/**
 * The MIT License (MIT)
 *
 * Copyright (C) ${year} Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
