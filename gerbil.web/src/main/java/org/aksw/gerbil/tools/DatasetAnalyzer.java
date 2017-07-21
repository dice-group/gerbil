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

import java.io.PrintStream;
import java.util.List;

import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.web.config.DatasetsConfig;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatasetAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetAnalyzer.class);

    public static void main(String[] args) {
        // List<DatasetConfiguration> datasetConfigs = DatasetsConfig
        // .datasets(RootConfig.getEntityCheckerManager(),
        // RootConfig.createSameAsRetriever()).getConfigurations();

        List<DatasetConfiguration> datasetConfigs = DatasetsConfig.datasets(null, null).getConfigurations();
        PrintStream output = null;
        try {
            output = new PrintStream("datasetAnalyzation.log");
            output.println("name,#subjects,#objects,#statements");
            DatasetAnalyzer analyzer = new DatasetAnalyzer(output);
            for (DatasetConfiguration config : datasetConfigs) {
                try {
                    analyzer.analyzeDataset(config);
                } catch (GerbilException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

    private PrintStream output;

    public DatasetAnalyzer(PrintStream output) {
        this.output = output;
    }

    public void analyzeDataset(DatasetConfiguration config) throws GerbilException {
        if (config.isApplicableForExperiment(ExperimentType.D2KB)) {
            analyze(config, ExperimentType.D2KB);
        } else if (config.isApplicableForExperiment(ExperimentType.OKE_Task2)) {
            analyze(config, ExperimentType.OKE_Task2);
        } else if (config.isApplicableForExperiment(ExperimentType.C2KB)) {
            analyze(config, ExperimentType.C2KB);
        } else {
            LOGGER.error("Can not analyze the dataset with the following config: " + config.toString());
        }
    }

    private void analyze(DatasetConfiguration config, ExperimentType type) throws GerbilException {
        Dataset dataset = config.getDataset(type);
        if (dataset == null) {
            return;
        }
        output.print(config.getName());
        output.print(',');
        Model model = dataset.getInstances().get(0);
     
        
        // average entities per document
        output.print(model.listSubjects().toList().size());
        output.print(',');
        // average entities per token
        output.print(model.listObjects().toList().size());
        output.print(',');

        // number of stmts
        output.print(model.size());
 
        output.println();
    }
}
