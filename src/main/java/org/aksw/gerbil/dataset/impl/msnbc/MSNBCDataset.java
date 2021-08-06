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
package org.aksw.gerbil.dataset.impl.msnbc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.dataset.impl.xml.CommonXMLParser;
import org.aksw.gerbil.dataset.impl.xml.CommonXMLTagDef;
import org.aksw.gerbil.dataset.impl.xml.GenericResult;
import org.aksw.gerbil.dataset.impl.xml.GenericXMLDataset;
import org.aksw.gerbil.dataset.impl.xml.XMLDataUtil;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.apache.commons.io.FileUtils;

public class MSNBCDataset extends GenericXMLDataset {

    private static final String dsName = "msnbc";
	private static final CommonXMLTagDef TAG_DEF = new CommonXMLTagDef("ReferenceProblem", null, null, null,
			"ReferenceInstance", "Offset", "Length", "SurfaceForm", "ChosenAnnotation");
    protected String annotationsDirectory;

    public MSNBCDataset(String textsDirectory, String annotationsDirectory) throws GerbilException {
        super(textsDirectory, dsName, TAG_DEF);
        this.annotationsDirectory = annotationsDirectory;
    }

    @Override
    public void init() throws GerbilException {
        this.documents = loadDocuments(new File(textsDirectory), new File(annotationsDirectory));
    }

    @Override
    protected List<Document> loadDocuments(File textDir) throws GerbilException {
        return loadDocuments(textDir, new File(annotationsDirectory));
    }

    protected List<Document> loadDocuments(File textDir, File annoDir) throws GerbilException {
        if ((!textDir.exists()) || (!textDir.isDirectory())) {
            throw new GerbilException(
                    "The given text directory (" + textDir.getAbsolutePath() + ") is not existing or not a directory.",
                    ErrorTypes.DATASET_LOADING_ERROR);
        }
        String textDirPath = textDir.getAbsolutePath();
        if (!textDirPath.endsWith(File.separator)) {
            textDirPath = textDirPath + File.separator;
        }
        if ((!annoDir.exists()) || (!annoDir.isDirectory())) {
            throw new GerbilException("The given annotation directory (" + annoDir.getAbsolutePath()
                    + ") is not existing or not a directory.", ErrorTypes.DATASET_LOADING_ERROR);
        }
        CommonXMLParser parser = new CommonXMLParser();
        GenericResult parsedResult;
        String text;
        List<Document> documents = new ArrayList<Document>();
        for (File annoFile : annoDir.listFiles()) {
            String fileName = annoFile.getName();
            // parse the annotation file
            try {
                parsedResult = parser.parseDSFile(annoFile, dsName, TAG_DEF);
            } catch (Exception e) {
                throw new GerbilException(
                        "Couldn't parse given annotation file (\"" + annoFile.getAbsolutePath() + "\".", e,
                        ErrorTypes.DATASET_LOADING_ERROR);
            }
            // read the text file
            try {
                text = FileUtils.readFileToString(new File(textDirPath + fileName));
            } catch (IOException e) {
                throw new GerbilException(
                        "Couldn't read text file \"" + textDirPath + fileName
                                + "\" mentioned in the annotations file \"" + annoFile.getAbsolutePath() + "\".",
                        e, ErrorTypes.DATASET_LOADING_ERROR);
            }
            // add text and uri to document
            Document result = parsedResult.getDocuments().get(0);
            result.setText(text);
            result.setDocumentURI(XMLDataUtil.generateDocumentUri(name, fileName));
            documents.add(result);
        }
        return documents;
    }
}
