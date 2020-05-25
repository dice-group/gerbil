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

import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.utils.SingletonWikipediaApi;
import org.aksw.gerbil.web.config.DatasetsConfig;
import org.aksw.gerbil.web.config.RootConfig;
import org.apache.commons.io.IOUtils;

@Deprecated
public class DatasetWikiIdExporter {

    private static final String EXPORT_FOLDER_NAME = "export";

    public static void main(String[] args) {
        List<DatasetConfiguration> datasetConfigs = DatasetsConfig.datasets(RootConfig.getEntityCheckerManager(), RootConfig.createSameAsRetriever())
                .getConfigurations();
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

        return null;
    }

    private IntOpenHashSet analyzeAsD2W(DatasetConfiguration config) throws GerbilException {

        return null;
    }

    private void printIds(IntOpenHashSet ids, PrintStream output) {
        int idArray[] = ids.toArray(new int[ids.size()]);
        for (int i = 0; i < idArray.length; ++i) {
            output.println(idArray[i]);
        }
    }
}
