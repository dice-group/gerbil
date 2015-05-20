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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.NIFTransferPrefixMapping;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;

public abstract class AbstractNIFParser implements NIFParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNIFParser.class);

    private static final String TYPE_INFERENCE_RULES = "typeInferencerRules.txt";

    private String httpContentType;
    private DocumentListParser parser = new DocumentListParser();

    public AbstractNIFParser(String httpContentType) {
        this.httpContentType = httpContentType;
    }

    @Override
    public List<Document> parseNIF(String nifString) {
        return parseNIF(new StringReader(nifString));
    }

    @Override
    public List<Document> parseNIF(String nifString, Model model) {
        return parseNIF(new StringReader(nifString), model);
    }

    @Override
    public List<Document> parseNIF(Reader reader) {
        return parseNIF(reader, getDefaultModel());
    }

    @Override
    public List<Document> parseNIF(Reader reader, Model nifModel) {
        parseNIFModel(reader, nifModel);
        infereTypes(nifModel);
        return parser.parseDocuments(nifModel);
    }

    protected abstract Model parseNIFModel(Reader reader, Model nifModel);

    @Override
    public List<Document> parseNIF(InputStream is) {
        return parseNIF(is, getDefaultModel());
    }

    @Override
    public List<Document> parseNIF(InputStream is, Model nifModel) {
        parseNIFModel(is, nifModel);
        infereTypes(nifModel);
        return parser.parseDocuments(nifModel);
    }

    protected abstract Model parseNIFModel(InputStream is, Model nifModel);

    // protected Document createAnnotatedDocument(Model nifModel) {
    // List<Document> documents = parser.parseDocuments(nifModel);
    // if (documents.size() == 0) {
    // LOGGER.error("Couldn't find any documents inside the given NIF model. Returning null.");
    // return null;
    // }
    // if (documents.size() > 1) {
    // LOGGER.warn("Found more than one document inside the given NIF model. Returning only the first one.");
    // }
    // return documents.get(0);
    // }

    @Override
    public String getHttpContentType() {
        return httpContentType;
    }

    protected void infereTypes(Model nifModel) {
        InputStream is = AbstractNIFParser.class.getClassLoader().getResourceAsStream(TYPE_INFERENCE_RULES);
        List<String> lines;
        try {
            lines = IOUtils.readLines(is);
        } catch (IOException e) {
            LOGGER.error("Couldn't load type inferencer rules from resource \"" + TYPE_INFERENCE_RULES
                    + "\". Working on the standard model.", e);
            return;
        }
        IOUtils.closeQuietly(is);
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line);
        }

        Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(sb.toString()));
        InfModel infModel = ModelFactory.createInfModel(reasoner, nifModel);
        nifModel.add(infModel);
    }

    protected Model getDefaultModel() {
        Model nifModel = ModelFactory.createDefaultModel();
        nifModel.setNsPrefixes(NIFTransferPrefixMapping.getInstance());
        return nifModel;
    }

}
