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
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.matching.Matching;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
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

    public <T extends Model> void storeAnnotatorOutput(ExperimentTaskConfiguration configuration,
            List<List<T>> results, List<Model> documents) {
        if (outputShouldBeStored(configuration)) {
            FileOutputStream fout = null;
            GZIPOutputStream gout = null;
            try {
                File file = generateOutputFile(configuration);
                List<Model> resultDocuments = documents;
                fout = new FileOutputStream(file);
                gout = new GZIPOutputStream(fout);
                Model unionizedModel = ModelFactory.createDefaultModel();
                for(Model m : resultDocuments){
                	unionizedModel.union(m);
                }
                unionizedModel.write(gout, "TTL");
            } catch (Exception e) {
                LOGGER.error("Couldn't write annotator result to file.", e);
            } finally {
                IOUtils.closeQuietly(gout);
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
        fileBuilder.append(".ttl.gz");
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

//    private <T extends Model> List<Model> generateResultDocuments(List<List<T>> results,
//            List<Model> documents) {
//        List<Model> resultDocuments = new ArrayList<Model>(documents.size());
//        Model datasetDocument, resultDocument;
//        for (int d = 0; d < documents.size(); ++d) {
//            datasetDocument = documents.get(d);
//            resultDocument = ModelFactory.createDefaultModel();
//            if ((d < results.size()) && (results.get(d) != null)) {
//                for (T m : results.get(d)) {
//                    resultDocument.addMarking(m);
//                }
//            }
//            resultDocuments.add(resultDocument);
//        }
//        return resultDocuments;
//    }

}
