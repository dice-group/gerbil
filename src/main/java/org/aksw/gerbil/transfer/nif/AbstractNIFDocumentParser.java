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
package org.aksw.gerbil.transfer.nif;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.aksw.gerbil.io.nif.NIFParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a simple Wrapper of a {@link NIFParser} instance offering the
 * methods of the {@link NIFDocumentParser} interface and might be removed in
 * future releases.
 * 
 * @author Michael R&ouml;der <roeder@informatik.uni-leipzig.de>
 * 
 */
public abstract class AbstractNIFDocumentParser implements NIFDocumentParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNIFDocumentParser.class);

    private NIFParser parser;

    public AbstractNIFDocumentParser(NIFParser parser) {
        this.parser = parser;
    }

    @Override
    public Document getDocumentFromNIFString(String nifString) throws Exception {
        return getDocumentFromNIFReader(new StringReader(nifString));
    }

    @Override
    public Document getDocumentFromNIFReader(Reader reader) throws Exception {
        List<Document> documents = parser.parseNIF(reader);
        if (documents.size() == 0) {
            LOGGER.error("Couldn't find any documents inside the given NIF model. Returning null.");
            return null;
        }
        if (documents.size() > 1) {
            LOGGER.warn("Found more than one document inside the given NIF model. Returning only the first one.");
        }
        return documents.get(0);
    }

    @Override
    public String getHttpContentType() {
        return parser.getHttpContentType();
    }
}
