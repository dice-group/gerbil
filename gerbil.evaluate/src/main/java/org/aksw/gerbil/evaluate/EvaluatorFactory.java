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
package org.aksw.gerbil.evaluate;

import java.util.List;

import org.aksw.agdistis.util.TripleIndex;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.converter.Literal2Resource;
import org.aksw.gerbil.dataset.converter.Literal2ResourceManager;
import org.aksw.gerbil.dataset.converter.impl.SPARQLBasedLiteral2Resource;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.impl.ModelComparator;
import org.aksw.gerbil.evaluate.impl.ROCEvaluator;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.semantic.kb.SimpleWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.semantic.subclass.SimpleSubClassInferencer;
import org.aksw.gerbil.semantic.subclass.SubClassInferencer;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("deprecation")
public class EvaluatorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluatorFactory.class);

    private static final String DEFAULT_WELL_KNOWN_KBS_PARAMETER_KEY = "org.aksw.gerbil.evaluate.DefaultWellKnownKB";
    private static final String DEFAULT_WELL_KNOWN_KBS[] = loadDefaultKBs();

    protected static final UrlValidator URL_VALIDATOR = new UrlValidator();

	private static final String GERBIL_HTTP_LITERAL_2_RESOURCE_CONVERTER_DOMAIN_KEY = "org.aksw.gerbil.dataset.converter.domain";

    public static final String SWC2017_TASK1_PROPERTIES_KEY = "org.aksw.gerbil.modelcomparator.swc2017.task1.properties";
    public static final String SWC2018_TASK1_PROPERTIES_KEY = "org.aksw.gerbil.modelcomparator.swc2018.task1.properties";
//	private static final String GERBIL_INDEX_LITERAL_2_RESOURCE_CONVERTER_FOLDER_KEY = null;
//
//	private static final String GERBIL_INDEX_LITERAL_2_RESOURCE_CONVERTER_DOMAIN_KEY = null;

    protected UriKBClassifier globalClassifier = null;
    protected SubClassInferencer inferencer = null;
    protected TripleIndex index = null;
    protected Literal2ResourceManager converterManager = new Literal2ResourceManager();

    public EvaluatorFactory() {
        this(null, null);
    }

    private static String[] loadDefaultKBs() {
        String kbs[] = GerbilConfiguration.getInstance().getStringArray(DEFAULT_WELL_KNOWN_KBS_PARAMETER_KEY);
        if (kbs == null) {
            LOGGER.error("Couldn't load the list of well known KBs. This GERBIL instance might not work as expected!");
        }
        return kbs;
    }

    public EvaluatorFactory(UriKBClassifier globalClassifier) {
        this(globalClassifier, null);
    }

    public EvaluatorFactory(SubClassInferencer inferencer) {
        this(null, inferencer);
    }

    public EvaluatorFactory(UriKBClassifier globalClassifier, SubClassInferencer inferencer) {
        if (globalClassifier != null) {
            this.globalClassifier = globalClassifier;
        } else {
            this.globalClassifier = new SimpleWhiteListBasedUriKBClassifier(DEFAULT_WELL_KNOWN_KBS);
        }
        if (inferencer != null) {
            this.inferencer = inferencer;
        } else {
            this.inferencer = new SimpleSubClassInferencer(ModelFactory.createDefaultModel());
        }
        //if(GerbilConfig has key Index)
        //try catch(use SPARQL based)
        //if(GerbilConfig has SPARQL key) 
        if(GerbilConfiguration.getInstance().containsKey(GERBIL_HTTP_LITERAL_2_RESOURCE_CONVERTER_DOMAIN_KEY)){
        	//add SPARQLBased...
        	for(String domain : GerbilConfiguration.getInstance().getStringArray(GERBIL_HTTP_LITERAL_2_RESOURCE_CONVERTER_DOMAIN_KEY)){
        		Literal2Resource converter = new SPARQLBasedLiteral2Resource(domain);
        		converterManager.registerLiteral2Resource(converter);
        	}
    	}
        
//        if(GerbilConfiguration.getInstance().containsKey(GERBIL_INDEX_LITERAL_2_RESOURCE_CONVERTER_FOLDER_KEY)){
//        	String folder = GerbilConfiguration.getInstance().getString(GERBIL_INDEX_LITERAL_2_RESOURCE_CONVERTER_FOLDER_KEY);
//        	for(String domain : GerbilConfiguration.getInstance().getStringArray(GERBIL_INDEX_LITERAL_2_RESOURCE_CONVERTER_DOMAIN_KEY)){
//        		Literal2Resource converter = null;
//        		try{
//        			converter = new IndexBasedLiteral2Resource(folder, domain);
//        		}
//        		catch(Exception e){
//        			converter = new SPARQLBasedLiteral2Resource(domain);        		
//        		}
//       			converterManager.registerLiteral2Resource(converter);
//        	}
//        }
    }

    @SuppressWarnings("rawtypes")
    protected Evaluator createEvaluator(ExperimentType type, ExperimentTaskConfiguration configuration, Dataset dataset) {
        return createEvaluator(type, configuration, dataset, globalClassifier, inferencer);
    }

    @SuppressWarnings({"rawtypes" })
    protected Evaluator createEvaluator(ExperimentType type, ExperimentTaskConfiguration configuration,
            Dataset dataset, UriKBClassifier classifier, SubClassInferencer inferencer) {
        switch (type) {
        //case Task1/Task2
        case SWC1:
        	return new ModelComparator(GerbilConfiguration.getInstance().getStringArray(SWC2017_TASK1_PROPERTIES_KEY), false);
        case SWC2:
        	return new ROCEvaluator();
//        case ETyping: {
//            return new SearcherBasedNotMatchingMarkingFilter<TypedSpan>(
//                    new StrongSpanMatchingsSearcher<TypedSpan>(),
//                    new ConfidenceScoreEvaluatorDecorator<TypedSpan>(new HierarchicalFMeasureCalculator<TypedSpan>(
//                            new HierarchicalMatchingsCounter<TypedSpan>(
//                                    (MatchingsSearcher<TypedSpan>) MatchingsSearcherFactory
//                                            .createSpanMatchingsSearcher(configuration.matching), classifier,
//                                    inferencer)), FMeasureCalculator.MICRO_F1_SCORE_NAME, new DoubleResultComparator()),
//                    true);
//        }

        case SWC2018T1:
            return new ModelComparator(GerbilConfiguration.getInstance().getStringArray(SWC2018_TASK1_PROPERTIES_KEY), true);
        default: {
            throw new IllegalArgumentException("Got an unknown Experiment Type.");
        }
        }
    }

    @SuppressWarnings({ "unchecked" })
    protected void addSubTaskEvaluators(List<Evaluator<?>> evaluators, ExperimentTaskConfiguration configuration,
            Dataset dataset) {
        ExperimentTaskConfiguration subTaskConfig;
        switch (configuration.type) {
        case AIT2KB: // falls through
        case AType:
        case C2KB:
        case D2KB:
        case ERec:
        case ETyping:
        case P2KB:
        case RE2KB:
            // Since the OKE challenge tasks are using the results of their
            // subtasks, the definition of subtasks is part of their evaluation
            // creation
        case SWC1:
        case SWC2:
        case SWC2018T1:
        case OKE_Task1:
        case OKE_Task2: {
            return;
        }
        case Sa2KB: // falls through
        case A2KB: {
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.ERec, configuration.matching);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig, createEvaluator(ExperimentType.ERec, subTaskConfig,
                    dataset)));
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
                    ExperimentType.D2KB, Matching.STRONG_ENTITY_MATCH);
            // evaluators.add(createEvaluator(ExperimentType.ELink,
            // configuration, dataset));
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig, createEvaluator(ExperimentType.D2KB, subTaskConfig,
                    dataset)));
            return;
        }
        case QA: {
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
            		 configuration.questionLanguage, ExperimentType.AIT2KB, configuration.matching);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig, createEvaluator(ExperimentType.AIT2KB, subTaskConfig,
                    dataset)));

            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
            		 configuration.questionLanguage, ExperimentType.AType, Matching.STRONG_ENTITY_MATCH);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig, createEvaluator(ExperimentType.AType, subTaskConfig,
                    dataset)));

    
            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
            		 configuration.questionLanguage, ExperimentType.P2KB, Matching.STRONG_ENTITY_MATCH);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig, createEvaluator(ExperimentType.P2KB, subTaskConfig,
                    dataset)));

            subTaskConfig = new ExperimentTaskConfiguration(configuration.annotatorConfig, configuration.datasetConfig,
            		 configuration.questionLanguage, ExperimentType.RE2KB, Matching.STRONG_ENTITY_MATCH);
            evaluators.add(new SubTaskEvaluator<>(subTaskConfig, createEvaluator(ExperimentType.RE2KB, subTaskConfig,
                    dataset)));
            return;
        }
        default: {
            throw new RuntimeException();
        }
        }
    }

    public void addEvaluators(List<Evaluator<?>> evaluators, ExperimentTaskConfiguration configuration, Dataset dataset) {
        converterManager.setQuestionLanguage(configuration.getQuestionLanguage());
    	evaluators.add(createEvaluator(configuration.type, configuration, dataset));
        addSubTaskEvaluators(evaluators, configuration, dataset);
    }
    
    public Literal2ResourceManager getConverterManager(){
    	return converterManager;
    }
}
