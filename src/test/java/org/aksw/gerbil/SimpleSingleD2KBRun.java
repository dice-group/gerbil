package org.aksw.gerbil;

import java.io.Closeable;
import java.util.concurrent.Semaphore;

import org.aksw.gerbil.annotator.AnnotatorConfiguration;
import org.aksw.gerbil.database.SimpleLoggingDAO4Debugging;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.evaluate.impl.InKBClassBasedFMeasureCalculator;
import org.aksw.gerbil.evaluate.impl.filter.SearcherBasedNotMatchingMarkingFilter;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.matching.MatchingsSearcher;
import org.aksw.gerbil.matching.MatchingsSearcherFactory;
import org.aksw.gerbil.matching.impl.CompoundMatchingsCounter;
import org.aksw.gerbil.matching.impl.MeaningMatchingsSearcher;
import org.aksw.gerbil.matching.impl.StrongSpanMatchingsSearcher;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.semantic.sameas.SameAsRetrieverDecorator;
import org.aksw.gerbil.semantic.sameas.impl.MultipleSameAsRetriever;
import org.aksw.gerbil.semantic.subclass.SubClassInferencer;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.web.config.AdapterManager;
import org.aksw.gerbil.web.config.AnnotatorsConfig;
import org.aksw.gerbil.web.config.DatasetsConfig;
import org.aksw.gerbil.web.config.RootConfig;
import org.aksw.simba.topicmodeling.concurrent.overseers.pool.DefeatableOverseer;
import org.aksw.simba.topicmodeling.concurrent.tasks.Task;
import org.aksw.simba.topicmodeling.concurrent.tasks.TaskObserver;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public class SimpleSingleD2KBRun extends EvaluatorFactory implements TaskObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSingleD2KBRun.class);

    private static final String ANNOTATOR_NAME = "WAT";
    private static final String DATASET_NAME = "MSNBC";
    private static final ExperimentType EXPERIMENT_TYPE = ExperimentType.D2KB;
    private static final Matching MATCHING = Matching.STRONG_ENTITY_MATCH;

    public static void main(String[] args) throws Exception {
        SingleRunTest test = new SingleRunTest();
        test.run();
    }

    private Semaphore mutex = new Semaphore(0);

    @Test
    public void runTest() throws Exception {
        run();
    }

    public void run() throws Exception {
        AdapterManager adapterManager = new AdapterManager();
        adapterManager.setAnnotators(AnnotatorsConfig.annotators());
        SameAsRetriever retriever = RootConfig.createSameAsRetriever();
        adapterManager.setDatasets(DatasetsConfig.datasets(RootConfig.getEntityCheckerManager(), retriever));

        AnnotatorConfiguration annotatorConfig = adapterManager.getAnnotatorConfig(ANNOTATOR_NAME, EXPERIMENT_TYPE);
        Assert.assertNotNull(annotatorConfig);
        DatasetConfiguration datasetConfig = adapterManager.getDatasetConfig(DATASET_NAME, EXPERIMENT_TYPE);
        Assert.assertNotNull(datasetConfig);

        DefeatableOverseer overseer = RootConfig.createOverseer();
        overseer.addObserver(this);

        ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] {
                new ExperimentTaskConfiguration(annotatorConfig, datasetConfig, EXPERIMENT_TYPE, MATCHING) };

        Experimenter experimenter = new Experimenter(overseer, new SimpleLoggingDAO4Debugging(), retriever, this,
                taskConfigs, "SingleRunTest");
        experimenter.run();

        mutex.acquire();

        closeHttpRetriever(retriever);
        overseer.shutdown();
    }

    private void closeHttpRetriever(SameAsRetriever retriever) {
        if (retriever != null) {
            if (retriever instanceof SameAsRetrieverDecorator) {
                closeHttpRetriever(((SameAsRetrieverDecorator) retriever).getDecorated());
            } else if (retriever instanceof MultipleSameAsRetriever) {
                for (SameAsRetriever decorated : ((MultipleSameAsRetriever) retriever).getRetriever()) {
                    closeHttpRetriever(decorated);
                }
            }
            if (retriever instanceof Closeable) {
                IOUtils.closeQuietly((Closeable) retriever);
            }
        }
    }

    @Override
    public void reportTaskFinished(Task task) {
        mutex.release();
    }

    @Override
    public void reportTaskThrowedException(Task task, Throwable t) {
        LOGGER.error("Task throwed exception.", t);
        Assert.assertNull(t);
        mutex.release();
    }

    @Override
    @SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
    protected Evaluator createEvaluator(ExperimentType type, ExperimentTaskConfiguration configuration, Dataset dataset,
            UriKBClassifier globalClassifier, SubClassInferencer inferencer) {
        switch (type) {
        case D2KB: {
            return new SearcherBasedNotMatchingMarkingFilter<NamedEntity>(
                    new StrongSpanMatchingsSearcher<NamedEntity>(),
                    new InKBClassBasedFMeasureCalculator<NamedEntity>(new CompoundMatchingsCounter<NamedEntity>(
                            (MatchingsSearcher<NamedEntity>) MatchingsSearcherFactory
                                    .createSpanMatchingsSearcher(configuration.matching),
                            new MeaningMatchingsSearcher<NamedEntity>(globalClassifier)), globalClassifier));
        }
        case Sa2KB:
        case A2KB:
        case C2KB:
        case ERec:
        case ETyping:
        case OKE_Task1:
        case OKE_Task2:
        default: {
            throw new IllegalArgumentException("Got an unknown Experiment Type.");
        }
        }
    }
}
