package org.aksw.gerbil.dataset.impl.iitb;

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
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            return parseAnnotationsStream(fin);
        } finally {
            IOUtils.closeQuietly(fin);
        }
    }

    public Map<String, Set<IITB_Annotation>> parseAnnotationsStream(InputStream is) throws IOException, SAXException {
        IITB_XMLHandler handler = new IITB_XMLHandler();
        parser.parse(is, handler);
        return handler.getDocumentAnnotationsMap();
    }
}
