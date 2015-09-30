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
package org.aksw.gerbil.transfer.nif;

import java.io.InputStream;
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
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * 
 */
public abstract class AbstractNIFDocumentParser implements NIFDocumentParser {

    private static final Logger LOGGER = LoggerFactory
	    .getLogger(AbstractNIFDocumentParser.class);

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
    public Document getDocumentFromNIFStream(InputStream stream)
	    throws Exception {
	List<Document> documents = parser.parseNIF(stream);
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
