/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.dataset.impl.iitb;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.apache.commons.io.IOUtils;
import org.apache.xerces.jaxp.SAXParserFactoryImpl;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class IITB_XMLParser {

    private SAXParser parser;

    public IITB_XMLParser() throws GerbilException {
        SAXParserFactory factory = SAXParserFactoryImpl.newInstance();
        try {
            parser = factory.newSAXParser();
        } catch (Exception e) {
            throw new GerbilException("Couldn't create SAX parser.", e, ErrorTypes.DATASET_LOADING_ERROR);
        }
    }

    public Map<String, Set<IITB_Annotation>> parseAnnotationsFile(File file) throws IOException, SAXException {
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            return parseAnnotationsStream(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public Map<String, Set<IITB_Annotation>> parseAnnotationsStream(InputStream is) throws IOException, SAXException {
        IITB_XMLHandler handler = new IITB_XMLHandler();
        InputSource is2 = new InputSource(is);
        is2.setEncoding("UTF-8");
        parser.parse(is2, handler);
        return handler.getDocumentAnnotationsMap();
    }
}
