package org.knowgraphs;

import java.io.File;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.aksw.gerbil.Experimenter;
import org.aksw.gerbil.annotator.AnnotatorConfiguration;
import org.aksw.gerbil.annotator.AnnotatorConfigurationImpl;
import org.aksw.gerbil.annotator.impl.instance.InstanceListBasedAnnotator;
import org.aksw.gerbil.database.SimpleLoggingDAO4Debugging;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.dataset.check.EntityCheckerManager;
import org.aksw.gerbil.dataset.impl.nif.FileBasedNIFDataset;
import org.aksw.gerbil.dataset.impl.nif.NIFFileDatasetConfig;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.semantic.subclass.SubClassInferencer;
import org.aksw.gerbil.web.config.RootConfig;
import org.aksw.simba.topicmodeling.concurrent.overseers.pool.DefeatableOverseer;
import org.aksw.simba.topicmodeling.concurrent.tasks.Task;
import org.aksw.simba.topicmodeling.concurrent.tasks.TaskObserver;
import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An evaluator test class for the KnowGraphs Winter School.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class EvaluatorTest extends EvaluatorFactory implements TaskObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluatorTest.class);

    /**
     * The name of the annotator. Possible names are "Babelfy", "DBpedia_Spotlight"
     * and "WAT"
     */
    private static final String ANNOTATOR_NAME = "Babelfy";
    /**
     * The name of the dataset. Possible names are "IITB", "KORE50", "MSNBC" and
     * "OKE_2015_Task_1_example_set". The latter should be preferred for testing
     * since it is smaller than the others ;)
     */
    private static final String DATASET_NAME = "OKE_2015_Task_1_example_set";

    ///////////////////////////// STANDARD ATTRIBUTES /////////////////////////////
    // It is very likely that you can simply ignore them ;)
    /**
     * We focus on D2KB so let's just define it here.
     */
    private static final ExperimentType EXPERIMENT_TYPE = ExperimentType.D2KB;
    /**
     * For D2KB, the matching does not really matter. Let's define it as strong.
     */
    private static final Matching MATCHING = Matching.STRONG_ANNOTATION_MATCH;
    /**
     * We won't use a sameAs retriever.
     */
    private static final SameAsRetriever SAME_AS_RETRIEVER = null;
    /**
     * We won't use entity checking.
     */
    private static final EntityCheckerManager ENTITY_CHECKER_MANAGER = null;
    /**
     * The directory with the prepared files.
     */
    private static final String DATA_CHALLENGE_DIRECTORY = "data-challenge/";

    /**
     * This is just for the internal synchronization with the thread that will
     * execute the experiment.
     */
    private Semaphore mutex = new Semaphore(0);
    /////////////////////////// END STANDARD ATTRIBUTES ///////////////////////////

    /**
     * This method implements the general workflow of a single experiment.
     * 
     * @throws Exception
     */
    public void run() throws Exception {
        // Get the (config) classes for our system and dataset
        AnnotatorConfiguration annotatorConfig = getAnnotatorConfig();
        DatasetConfiguration datasetConfig = getDatasetConfig();

        // Some general set up for our the experiment framework
        DefeatableOverseer overseer = RootConfig.createOverseer();
        overseer.addObserver(this);
        ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] {
                new ExperimentTaskConfiguration(annotatorConfig, datasetConfig, EXPERIMENT_TYPE, MATCHING) };
        Experimenter experimenter = new Experimenter(overseer, new SimpleLoggingDAO4Debugging(), SAME_AS_RETRIEVER,
                this, taskConfigs, "EvaluatorTest");

        // Start the experiment!
        experimenter.run();

        // Wait for the experiment to end
        mutex.acquire();
        overseer.shutdown();
    }

    /**
     * This is the method in which you should call your Evaluator class!
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Evaluator createEvaluator(ExperimentType type, ExperimentTaskConfiguration configuration, Dataset dataset,
            UriKBClassifier classifier, SubClassInferencer inferencer) {
        switch (type) {
        case D2KB: {
            /*
             * This is the place at which we should create an instance of our evaluation
             * class!
             */
            // 1. Variant, you can directly implement the Evaluator<T> interface. (T is a
            // MeaningSpan in D2KB)
            // return new MyFancyEvaluator();

            // 2. Variant, you can make use of an existing decorator that frees you from
            // system results that do not match a position of any annotation in the gold
            // standard.
//            return new SearcherBasedNotMatchingMarkingFilter<MeaningSpan>(
//                    new StrongSpanMatchingsSearcher<MeaningSpan>(),
//                    new MyFancyEvaluator(),
//                    true);

            // The following line will give you the original D2KB evaluator implementation
            // of GERBIL
            // (just for comparison)
            return super.createEvaluator(type, configuration, dataset, classifier, inferencer);
        }
        default: {
            throw new IllegalArgumentException("Got an unknown Experiment Type.");
        }
        }
    }

    @Override
    public void reportTaskFinished(Task task) {
        mutex.release();
    }

    @Override
    public void reportTaskThrowedException(Task task, Throwable t) {
        LOGGER.error("Task threw exception.", t);
        mutex.release();
    }

    protected AnnotatorConfiguration getAnnotatorConfig()
            throws NoSuchMethodException, SecurityException, GerbilException {
        StringBuilder fileName = new StringBuilder();
        fileName.append(DATA_CHALLENGE_DIRECTORY);
        fileName.append(DATASET_NAME);
        fileName.append(File.separator);
        fileName.append(ANNOTATOR_NAME);
        fileName.append(".ttl");

        FileBasedNIFDataset dataset = new FileBasedNIFDataset(fileName.toString(), ANNOTATOR_NAME, Lang.TTL);
        dataset.init();

        return new AnnotatorConfigurationImpl(ANNOTATOR_NAME, false,
                InstanceListBasedAnnotator.class.getConstructor(String.class, List.class),
                new Object[] { ANNOTATOR_NAME, dataset.getInstances() }, EXPERIMENT_TYPE);
    }

    protected DatasetConfiguration getDatasetConfig() {
        StringBuilder fileName = new StringBuilder();
        fileName.append(DATA_CHALLENGE_DIRECTORY);
        fileName.append("datasets");
        fileName.append(File.separator);
        fileName.append(DATASET_NAME);
        fileName.append(".ttl");
        return new NIFFileDatasetConfig(DATASET_NAME, fileName.toString(), false, EXPERIMENT_TYPE,
                ENTITY_CHECKER_MANAGER, SAME_AS_RETRIEVER);
    }

    public static void main(String[] args) throws Exception {
        EvaluatorTest test = new EvaluatorTest();
        test.run();
    }
}
