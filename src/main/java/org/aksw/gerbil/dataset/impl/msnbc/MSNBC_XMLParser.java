package org.aksw.gerbil.dataset.impl.msnbc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.apache.commons.io.IOUtils;
import org.apache.xerces.jaxp.SAXParserFactoryImpl;
import org.xml.sax.SAXException;

public class MSNBC_XMLParser {

    private SAXParser parser;

    public MSNBC_XMLParser() throws GerbilException {
        SAXParserFactory factory = SAXParserFactoryImpl.newInstance();
        try {
            parser = factory.newSAXParser();
        } catch (Exception e) {
            throw new GerbilException("Couldn't create SAX parser.", e, ErrorTypes.DATASET_LOADING_ERROR);
        }
    }

    public MSNBC_Result parseAnnotationsFile(File file) throws IOException, SAXException {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            return parseAnnotationsStream(fin);
        } finally {
            IOUtils.closeQuietly(fin);
        }
    }

    public MSNBC_Result parseAnnotationsStream(InputStream is) throws IOException, SAXException {
        MSNBC_XMLHandler handler = new MSNBC_XMLHandler();
        parser.parse(is, handler);
        return handler;
    }
}
