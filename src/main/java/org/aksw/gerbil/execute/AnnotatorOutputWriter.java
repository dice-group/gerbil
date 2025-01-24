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
package org.aksw.gerbil.execute;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.io.json.SimpleJsonDatasetWriter;
import org.aksw.gerbil.io.nif.NIFWriter;
import org.aksw.gerbil.io.nif.impl.TurtleNIFWriter;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotatorOutputWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotatorOutputWriter.class);

    private static final String DEFAULT_STORABLE_ANNOTATOR_NAME_PART = "(store)";

    private File outputDirectory;
    private String storableAnnotatorNamePart = DEFAULT_STORABLE_ANNOTATOR_NAME_PART;
    private String format;

    public AnnotatorOutputWriter(String outputDirectory) {
        this(outputDirectory, "ttl");
    }

    public AnnotatorOutputWriter(String outputDirectory, String format) {
        this.outputDirectory = new File(outputDirectory);
        if (!this.outputDirectory.exists()) {
            this.outputDirectory.mkdirs();
        }
        this.format = format;
    }

    public <T extends Marking> void storeAnnotatorOutput(ExperimentTaskConfiguration configuration,
            List<List<T>> results, List<Document> documents) {
        if (outputShouldBeStored(configuration)) {
            FileOutputStream fout = null;
            GZIPOutputStream gout = null;
            try {
                File file = generateOutputFile(configuration);
                List<Document> resultDocuments = generateResultDocuments(results, documents);
                fout = new FileOutputStream(file);
                gout = new GZIPOutputStream(fout);
                write(resultDocuments, gout);
            } catch (Exception e) {
                LOGGER.error("Couldn't write annotator result to file.", e);
            } finally {
                IOUtils.closeQuietly(gout);
                IOUtils.closeQuietly(fout);
            }
        }
    }

    protected void write(List<Document> resultDocuments, GZIPOutputStream gout) {
        switch (format) {
        case "ttl": {
            NIFWriter writer = new TurtleNIFWriter();
            writer.writeNIF(resultDocuments, gout);
            return;
        }
        case "json": {
            SimpleJsonDatasetWriter writer = new SimpleJsonDatasetWriter();
            try {
                writer.writeDocuments(resultDocuments, gout);
            } catch (IOException e) {
                LOGGER.error("Error while writing annotations.", e);
            }
            return;
        }
        default: {
            LOGGER.error("Unknown format {}. Won't write any annotation results.", format);
        }
        }
    }

    private boolean outputShouldBeStored(ExperimentTaskConfiguration configuration) {
        return (configuration.datasetConfig.couldBeCached()
                || configuration.datasetConfig.getName().contains(storableAnnotatorNamePart))
                && (configuration.annotatorConfig.couldBeCached()
                        || configuration.annotatorConfig.getName().contains(storableAnnotatorNamePart));
    }

    private File generateOutputFile(ExperimentTaskConfiguration configuration) {
        StringBuilder fileBuilder = new StringBuilder();
        fileBuilder.append(outputDirectory.getAbsolutePath());
        fileBuilder.append(File.separator);
        appendCleanedString(fileBuilder, configuration.annotatorConfig.getName());
        fileBuilder.append('-');
        appendCleanedString(fileBuilder, configuration.datasetConfig.getName());
        if (configuration.matching == Matching.WEAK_ANNOTATION_MATCH) {
            fileBuilder.append("-w-");
        } else {
            fileBuilder.append("-s-");
        }
        appendCleanedString(fileBuilder, configuration.type.name());
        fileBuilder.append('.');
        fileBuilder.append(format);
        fileBuilder.append(".gz");
        return new File(fileBuilder.toString());
    }

    private void appendCleanedString(StringBuilder builder, String s) {
        char chars[] = s.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            if (Character.isLetterOrDigit(chars[i])) {
                builder.append(chars[i]);
            } else {
                builder.append('_');
            }
        }
    }

    private <T extends Marking> List<Document> generateResultDocuments(List<List<T>> results,
            List<Document> documents) {
        List<Document> resultDocuments = new ArrayList<Document>(documents.size());
        Document datasetDocument, resultDocument;
        for (int d = 0; d < documents.size(); ++d) {
            datasetDocument = documents.get(d);
            resultDocument = new DocumentImpl(datasetDocument.getText(), datasetDocument.getDocumentURI());
            if ((d < results.size()) && (results.get(d) != null)) {
                for (T m : results.get(d)) {
                    resultDocument.addMarking(m);
                }
            }
            resultDocuments.add(resultDocument);
        }
        return resultDocuments;
    }

}
