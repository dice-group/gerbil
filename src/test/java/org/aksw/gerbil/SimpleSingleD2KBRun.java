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
package org.aksw.gerbil;

import java.io.Closeable;
import java.util.concurrent.Semaphore;

import org.aksw.gerbil.annotator.AnnotatorConfiguration;
import org.aksw.gerbil.annotator.decorator.ErrorCountingAnnotatorDecorator;
import org.aksw.gerbil.database.SimpleLoggingDAO4Debugging;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.datatypes.marking.ClassifiedSpanMeaning;
import org.aksw.gerbil.datatypes.marking.MarkingClasses;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.evaluate.impl.ClassConsideringFMeasureCalculator;
import org.aksw.gerbil.evaluate.impl.ClassifyingEvaluatorDecorator;
import org.aksw.gerbil.evaluate.impl.ConfidenceBasedFMeasureCalculator;
import org.aksw.gerbil.evaluate.impl.GSInKBClassifyingEvaluatorDecorator;
import org.aksw.gerbil.evaluate.impl.filter.SearcherBasedNotMatchingMarkingFilter;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.matching.MatchingsSearcher;
import org.aksw.gerbil.matching.MatchingsSearcherFactory;
import org.aksw.gerbil.matching.impl.ClassifiedMeaningMatchingsSearcher;
import org.aksw.gerbil.matching.impl.CompoundMatchingsSearcher;
import org.aksw.gerbil.matching.impl.MatchingsCounterImpl;
import org.aksw.gerbil.matching.impl.StrongSpanMatchingsSearcher;
import org.aksw.gerbil.matching.impl.clas.EmergingEntityMeaningClassifier;
import org.aksw.gerbil.matching.impl.clas.UriBasedMeaningClassifier;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.semantic.sameas.SameAsRetrieverDecorator;
import org.aksw.gerbil.semantic.sameas.impl.MultipleSameAsRetriever;
import org.aksw.gerbil.semantic.sameas.impl.cache.FileBasedCachingSameAsRetriever;
import org.aksw.gerbil.semantic.subclass.SubClassInferencer;
import org.aksw.gerbil.test.EntityCheckerManagerSingleton4Tests;
import org.aksw.gerbil.test.SameAsRetrieverSingleton4Tests;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.web.config.AdapterManager;
import org.aksw.gerbil.web.config.AnnotatorsConfig;
import org.aksw.gerbil.web.config.DatasetsConfig;
import org.aksw.gerbil.web.config.RootConfig;
import org.aksw.simba.topicmodeling.concurrent.overseers.pool.DefeatableOverseer;
import org.aksw.simba.topicmodeling.concurrent.tasks.Task;
import org.aksw.simba.topicmodeling.concurrent.tasks.TaskObserver;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public class SimpleSingleD2KBRun extends EvaluatorFactory implements TaskObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSingleD2KBRun.class);

    private static final String ANNOTATOR_NAME = "xLisa-NER";
    private static final String DATASET_NAME = "N3-RSS-500";
    private static final ExperimentType EXPERIMENT_TYPE = ExperimentType.D2KB;
    private static final Matching MATCHING = Matching.STRONG_ENTITY_MATCH;

    @BeforeClass
    public static void setMatchingsCounterDebugFlag() {
        MatchingsCounterImpl.setPrintDebugMsg(true);
        ConfidenceBasedFMeasureCalculator.setPrintDebugMsg(true);
        ErrorCountingAnnotatorDecorator.setPrintDebugMsg(true);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("waiting 10 secs...");
        Thread.sleep(10000);
        setMatchingsCounterDebugFlag();
        SimpleSingleD2KBRun test = new SimpleSingleD2KBRun();
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
        SameAsRetriever retriever = SameAsRetrieverSingleton4Tests.getInstance();
        adapterManager
                .setDatasets(DatasetsConfig.datasets(EntityCheckerManagerSingleton4Tests.getInstance(), retriever));

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
            } else if (retriever instanceof FileBasedCachingSameAsRetriever) {
                ((FileBasedCachingSameAsRetriever) retriever).storeCache();
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
    public Evaluator createEvaluator(ExperimentType type, ExperimentTaskConfiguration configuration, Dataset dataset,
            UriKBClassifier globalClassifier, SubClassInferencer inferencer) {
        switch (type) {
        case D2KB: {
            return new SearcherBasedNotMatchingMarkingFilter<MeaningSpan>(
                    new StrongSpanMatchingsSearcher<MeaningSpan>(),
                    new ClassifyingEvaluatorDecorator<MeaningSpan, ClassifiedSpanMeaning>(
                            new GSInKBClassifyingEvaluatorDecorator<ClassifiedSpanMeaning>(
                                    new ClassConsideringFMeasureCalculator<ClassifiedSpanMeaning>(
                                            new MatchingsCounterImpl<ClassifiedSpanMeaning>(
                                                    new CompoundMatchingsSearcher<ClassifiedSpanMeaning>(
                                                            (MatchingsSearcher<ClassifiedSpanMeaning>) MatchingsSearcherFactory
                                                                    .createSpanMatchingsSearcher(
                                                                            configuration.matching),
                                                            new ClassifiedMeaningMatchingsSearcher<ClassifiedSpanMeaning>())),
                                            MarkingClasses.IN_KB, MarkingClasses.EE, MarkingClasses.GS_IN_KB),
                                    new StrongSpanMatchingsSearcher<ClassifiedSpanMeaning>()),
                            new UriBasedMeaningClassifier<ClassifiedSpanMeaning>(globalClassifier, MarkingClasses.IN_KB),
                            new EmergingEntityMeaningClassifier<ClassifiedSpanMeaning>()),
                    true);
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
