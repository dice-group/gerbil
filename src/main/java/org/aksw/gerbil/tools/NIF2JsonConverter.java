package org.aksw.gerbil.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.aksw.gerbil.io.json.SimpleJsonDatasetWriter;
import org.aksw.gerbil.io.nif.impl.TurtleNIFParser;
import org.aksw.gerbil.transfer.nif.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NIF2JsonConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NIF2JsonConverter.class);

    private TurtleNIFParser parser = new TurtleNIFParser();
    private SimpleJsonDatasetWriter writer = new SimpleJsonDatasetWriter();
    private BiFunction<Document, Integer, Integer> idGenerator = new IDExtractor();

    public void run(File input, File outputDir) {
        if (input.isDirectory()) {
            File[] files = input.listFiles();
            for (int i = 0; i < files.length; ++i) {
                run(files[i], outputDir);
            }
        } else {
            convertFile(input, outputDir);
        }
    }

    public void convertFile(File inputFile, File outputDir) {
        List<Document> documents = readDocuments(inputFile);
        if (documents == null) {
            return;
        }
        File outputFile = generateOutputFileName(inputFile, outputDir);
        writeOuptutFile(documents, outputFile);
    }

    private List<Document> readDocuments(File inputFile) {
        try (InputStream is = openStream(inputFile)) {
            return parser.parseNIF(is);
        } catch (Exception e) {
            LOGGER.error("Exception while reading the file.", e);
        }
        return null;
    }

    private InputStream openStream(File inputFile) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(inputFile));
        if (inputFile.getName().endsWith(".gz")) {
            is = new GZIPInputStream(is);
        }
        return is;
    }

    private File generateOutputFileName(File inputFile, File outputDir) {
        String fileName = inputFile.getName();
        if (fileName.endsWith(".gz")) {
            fileName = fileName.substring(0, fileName.length() - 3);
        }
        if (fileName.endsWith(".bz2")) {
            fileName = fileName.substring(0, fileName.length() - 4);
        }
        if (fileName.endsWith(".ttl")) {
            fileName = fileName.substring(0, fileName.length() - 4);
        }
        return new File(outputDir.getAbsolutePath() + File.separator + fileName + ".json");
    }

    private void writeOuptutFile(List<Document> documents, File outputFile) {
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            writer.writeDocuments(documents, idGenerator, out);
        } catch (IOException e) {
            LOGGER.error("Error writing file.", e);
        }
    }

    public static void main(String[] args) {
        String input = "/home/micha/data/pipelines/ER-D2KB-system-responses";
        String output = "./";
        NIF2JsonConverter converter = new NIF2JsonConverter();
        converter.run(new File(input), new File(output));
    }

    /**
     * This class tries to extract the document ID from the document IRI by
     * searching for the last number in the IRI. This number is returned as ID. As a
     * fallback strategy, the given ID of the document in the document list is
     * returned.
     * 
     * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
     *
     */
    public static class IDExtractor implements BiFunction<Document, Integer, Integer> {

        private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");

        @Override
        public Integer apply(Document document, Integer idInList) {
            String iri = document.getDocumentURI();
            Matcher matcher = NUMBER_PATTERN.matcher(iri);
            int lastStart = -1;
            int lastEnd = -1;
            while (matcher.find()) {
                lastStart = matcher.start();
                lastEnd = matcher.end();
            }
            if (lastStart >= 0) {
                return Integer.parseInt(iri.substring(lastStart, lastEnd));
            }
            return idInList;
        }

    }
}
