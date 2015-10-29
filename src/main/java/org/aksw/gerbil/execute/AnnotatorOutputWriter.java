package org.aksw.gerbil.execute;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
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

    public AnnotatorOutputWriter(String outputDirectory) {
        this.outputDirectory = new File(outputDirectory);
        if (!this.outputDirectory.exists()) {
            this.outputDirectory.mkdirs();
        }
    }

    public <T extends Marking> void storeAnnotatorOutput(ExperimentTaskConfiguration configuration,
            List<List<T>> results, List<Document> documents) {
        if (outputShouldBeStored(configuration)) {
            FileOutputStream fout = null;
            try {
                File file = generateOutputFile(configuration);
                List<Document> resultDocuments = generateResultDocuments(results, documents);
                fout = new FileOutputStream(file);
                NIFWriter writer = new TurtleNIFWriter();
                writer.writeNIF(resultDocuments, fout);
            } catch (Exception e) {
                LOGGER.error("Couldn't write annotator result to file.", e);
            } finally {
                IOUtils.closeQuietly(fout);
            }
        }
    }

    private boolean outputShouldBeStored(ExperimentTaskConfiguration configuration) {
        return configuration.datasetConfig.couldBeCached() && (configuration.annotatorConfig.couldBeCached()
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
        fileBuilder.append(".ttl");
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
