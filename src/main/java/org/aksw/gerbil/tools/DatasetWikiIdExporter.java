/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
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
package org.aksw.gerbil.tools;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unipi.di.acube.batframework.data.Annotation;
import it.unipi.di.acube.batframework.data.Tag;
import it.unipi.di.acube.batframework.problems.C2WDataset;
import it.unipi.di.acube.batframework.problems.D2WDataset;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.datasets.DatasetConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.utils.DatasetMapping;
import org.aksw.gerbil.utils.SingletonWikipediaApi;
import org.apache.commons.io.IOUtils;

@SuppressWarnings("deprecation")
public class DatasetWikiIdExporter {

    private static final String EXPORT_FOLDER_NAME = "export";

    public static void main(String[] args) {
        List<DatasetConfiguration> datasetConfigs = DatasetMapping.getDatasetConfigurations();
        File exportFolder = new File(EXPORT_FOLDER_NAME);
        if (!exportFolder.exists()) {
            exportFolder.mkdirs();
        }
        PrintStream output = null;
        DatasetWikiIdExporter analyzer = new DatasetWikiIdExporter();
        for (DatasetConfiguration config : datasetConfigs) {
            try {
                output = new PrintStream(exportFolder.getAbsolutePath() + File.separator
                        + config.getName().replaceAll("[/:]", "_") + "_wikiIds.txt");
                analyzer.analyzeDataset(config, output);
                SingletonWikipediaApi.getInstance().flush();
            } catch (GerbilException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(output);
            }
        }
    }

    public DatasetWikiIdExporter() {
    }

    public void analyzeDataset(DatasetConfiguration config, PrintStream output) throws GerbilException {
        IntOpenHashSet ids = analyzeAsD2W(config);
        if (ids == null) {
            ids = analyzeAsC2W(config);
        }
        printIds(ids, output);
    }

    private IntOpenHashSet analyzeAsC2W(DatasetConfiguration config) throws GerbilException {
        D2WDataset dataset = (D2WDataset) config.getDataset(ExperimentType.D2KB);
        if (dataset == null) {
            return null;
        }
        List<HashSet<Annotation>> goldStandard = dataset.getD2WGoldStandardList();
        IntOpenHashSet ids = new IntOpenHashSet();
        for (HashSet<Annotation> annotations : goldStandard) {
            for (Annotation annotation : annotations) {
                ids.add(annotation.getConcept());
            }
        }
        return ids;
    }

    private IntOpenHashSet analyzeAsD2W(DatasetConfiguration config) throws GerbilException {
        C2WDataset dataset = (C2WDataset) config.getDataset(ExperimentType.C2KB);
        if (dataset == null) {
            return null;
        }
        List<HashSet<Tag>> goldStandard = dataset.getC2WGoldStandardList();
        IntOpenHashSet ids = new IntOpenHashSet();
        for (HashSet<Tag> tags : goldStandard) {
            for (Tag tag : tags) {
                ids.add(tag.getConcept());
            }
        }
        return ids;
    }

    private void printIds(IntOpenHashSet ids, PrintStream output) {
        int idArray[] = ids.toArray(new int[ids.size()]);
        for (int i = 0; i < idArray.length; ++i) {
            output.println(idArray[i]);
        }
    }
}
