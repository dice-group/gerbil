package org.aksw.gerbil.tools;

import java.io.PrintStream;
import java.util.List;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.AnnotatorConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.web.config.AnnotatorsConfig;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotatorAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotatorAnalyzer.class);

    private PrintStream out;
	
	public AnnotatorAnalyzer(PrintStream output) {
		out = output;
	}

	public static void main(String[] args) {
		PrintStream output = null;
		try {
            output = new PrintStream("annotatorAnalyzer.log");
            output.println("Name,ExperimentType");
            List<AnnotatorConfiguration> annotatorConfigs = AnnotatorsConfig.annotators().getConfigurations();
    		for(AnnotatorConfiguration annotatorConfig : annotatorConfigs){
    			AnnotatorAnalyzer ai = new AnnotatorAnalyzer(output);
    			try {
    				ai.analyzeAnnotator(annotatorConfig);
    			} catch(GerbilException e){
    				e.printStackTrace();
    			}
    		}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(output);
        }
	}
	
	public void analyzeAnnotator(AnnotatorConfiguration config) throws GerbilException {
        if (config.isApplicableForExperiment(ExperimentType.D2KB)) {
            analyze(config, ExperimentType.D2KB);
        } else if (config.isApplicableForExperiment(ExperimentType.OKE_Task2)) {
            analyze(config, ExperimentType.OKE_Task2);
        } else if (config.isApplicableForExperiment(ExperimentType.C2KB)) {
            analyze(config, ExperimentType.C2KB);
        } else if (config.isApplicableForExperiment(ExperimentType.ETyping)) {
            analyze(config, ExperimentType.ETyping);
        } else if (config.isApplicableForExperiment(ExperimentType.ERec)) {
            analyze(config, ExperimentType.ERec);
        } else {
            LOGGER.error("Can not analyze the Annotator with the following config: " + config.toString());
        }
    }

	  private void analyze(AnnotatorConfiguration config, ExperimentType type) throws GerbilException {
	        Annotator annotator = config.getAnnotator(type);
	        if (annotator == null) {
	            return;
	        }
	        out.print(config.getName());
	        out.print(',');
	        out.print(config.getExperimentType().getName());

	        out.println();
	    }
	
}
