package org.aksw.gerbil.bat.utils;

import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.data.Mention;
import it.acubelab.batframework.data.MultipleAnnotation;
import it.acubelab.batframework.data.ScoredAnnotation;
import it.acubelab.batframework.data.ScoredTag;
import it.acubelab.batframework.data.Tag;
import it.acubelab.batframework.metrics.MatchRelation;
import it.acubelab.batframework.metrics.Metrics;
import it.acubelab.batframework.metrics.MetricsResultSet;
import it.acubelab.batframework.metrics.StrongAnnotationMatch;
import it.acubelab.batframework.problems.A2WDataset;
import it.acubelab.batframework.problems.A2WSystem;
import it.acubelab.batframework.problems.C2WDataset;
import it.acubelab.batframework.problems.C2WSystem;
import it.acubelab.batframework.problems.CandidatesSpotter;
import it.acubelab.batframework.problems.D2WDataset;
import it.acubelab.batframework.problems.D2WSystem;
import it.acubelab.batframework.problems.Sa2WSystem;
import it.acubelab.batframework.problems.Sc2WSystem;
import it.acubelab.batframework.problems.TopicDataset;
import it.acubelab.batframework.utils.Pair;
import it.acubelab.batframework.utils.ProblemReduction;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.aksw.gerbil.datatypes.ExperimentTaskState;

/**
 * Static methods to run the experiments. A set of annotators are run on a set
 * of datasets, and the metrics are computer according to a set of match
 * relations. The result is written in resulting hash tables.
 * 
 * Original class implemented by the A-cube lab - Università di Pisa -
 * Dipartimento di Informatica has been adapted for GERBIL.
 * 
 * @author Michael Röder <roeder@informatik.uni-leipzig.de>
 */
public class RunExperiments {

    private static double THRESHOLD_STEP = 1. / 128.;

    public static void computeMetricsA2WFakeReductionToSa2W(MatchRelation<Annotation> m, A2WSystem tagger,
            A2WDataset ds, ExperimentTaskState state, WikipediaApiInterface api,
            HashMap<String, HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>> results)
            throws Exception {
        Metrics<Annotation> metrics = new Metrics<Annotation>();
        float threshold = 0;
        System.out.print("Doing annotations... ");
        List<HashSet<Annotation>> computedAnnotations = doA2WAnnotations(tagger, ds, state);
        System.out.println("Done.");
        for (threshold = 0; threshold <= 1; threshold += THRESHOLD_STEP) {
            MetricsResultSet rs = metrics.getResult(computedAnnotations, ds.getA2WGoldStandardList(), m);
            updateThresholdRecords(results, m.getName(), tagger.getName(), ds.getName(), (float) threshold, rs);
        }
    }

    public static void computeMetricsA2WReducedFromSa2W(MatchRelation<Annotation> m, Sa2WSystem tagger, A2WDataset ds,
            ExperimentTaskState state, WikipediaApiInterface api,
            HashMap<String, HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>> results)
            throws Exception {
        Metrics<Annotation> metrics = new Metrics<Annotation>();
        System.out.println("Doing annotations... ");
        List<HashSet<ScoredAnnotation>> computedAnnotations = doSa2WAnnotations(tagger, ds, state);
        System.out.println("Done with all documents.");
        for (double threshold = 0; threshold <= 1; threshold += THRESHOLD_STEP) {
            System.out.println("Testing with tagger: " + tagger.getName() + " dataset: " + ds.getName()
                    + " score threshold: " + threshold);
            List<HashSet<Annotation>> reducedTags = ProblemReduction.Sa2WToA2WList(computedAnnotations,
                    (float) threshold);
            MetricsResultSet rs = metrics.getResult(reducedTags, ds.getA2WGoldStandardList(), m);
            updateThresholdRecords(results, m.getName(), tagger.getName(), ds.getName(), (float) threshold, rs);
        }
    }

    public static void computeMetricsC2WReducedFromSa2W(MatchRelation<Tag> m, Sa2WSystem tagger, C2WDataset ds,
            WikipediaApiInterface api, ExperimentTaskState state,
            HashMap<String, HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>> results)
            throws Exception {
        Metrics<Tag> metrics = new Metrics<Tag>();
        System.out.println("Doing annotations... ");
        List<HashSet<ScoredAnnotation>> computedAnnotations = doSa2WAnnotations(tagger, ds, state);
        System.out.println("Done with all documents.");
        for (double threshold = 0; threshold <= 1; threshold += THRESHOLD_STEP) {
            System.out.println("Testing with tagger: " + tagger.getName() + " dataset: " + ds.getName()
                    + " score threshold: " + threshold);
            List<HashSet<Annotation>> reducedAnnotations = ProblemReduction.Sa2WToA2WList(computedAnnotations,
                    (float) threshold);
            List<HashSet<Tag>> reducedTags = ProblemReduction.A2WToC2WList(reducedAnnotations);
            List<HashSet<Tag>> reducedGs = ds.getC2WGoldStandardList();
            MetricsResultSet rs = metrics.getResult(reducedTags, reducedGs, m);
            updateThresholdRecords(results, m.getName(), tagger.getName(), ds.getName(), (float) threshold, rs);
        }
    }

    public static void computeMetricsC2WReducedFromSc2W(MatchRelation<Tag> m, Sc2WSystem tagger, C2WDataset ds,
            WikipediaApiInterface api, ExperimentTaskState state,
            HashMap<String, HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>> results)
            throws Exception {
        Metrics<Tag> metrics = new Metrics<Tag>();
        double threshold = 0;
        System.out.print("Doing annotations... ");
        List<HashSet<ScoredTag>> computedAnnotations = doSc2WTags(tagger, ds, state);
        System.out.println("Done.");
        for (threshold = 0; threshold <= 1; threshold += THRESHOLD_STEP) {
            System.out.println("Testing with tagger: " + tagger.getName() + " dataset: " + ds.getName()
                    + " score threshold: " + threshold);
            List<HashSet<Tag>> reducedAnnotations = ProblemReduction.Sc2WToC2WList(computedAnnotations,
                    (float) threshold);
            List<HashSet<Tag>> reducedGs = ds.getC2WGoldStandardList();
            MetricsResultSet rs = metrics.getResult(reducedAnnotations, reducedGs, m);
            updateThresholdRecords(results, m.getName(), tagger.getName(), ds.getName(), (float) threshold, rs);
        }
    }

    public static void computeMetricsC2W(MatchRelation<Tag> m, C2WSystem tagger, C2WDataset ds,
            WikipediaApiInterface api, ExperimentTaskState state,
            HashMap<String, HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>> results)
            throws Exception {
        Metrics<Tag> metrics = new Metrics<Tag>();
        double threshold = 0;
        System.out.print("Doing annotations... ");
        List<HashSet<Tag>> computedAnnotations = doC2WTags(tagger, ds, state);
        System.out.println("Done.");
        System.out.println("Testing with tagger: " + tagger.getName() + " dataset: " + ds.getName()
                + " (no score thr.)");
        MetricsResultSet rs = metrics.getResult(computedAnnotations, ds.getC2WGoldStandardList(), m);
        for (threshold = 0; threshold <= 1; threshold += THRESHOLD_STEP) {
            updateThresholdRecords(results, m.getName(), tagger.getName(), ds.getName(), (float) threshold, rs);
        }
    }

    public static void computeMetricsD2WFakeReductionToSa2W(D2WSystem tagger, D2WDataset ds, ExperimentTaskState state,
            WikipediaApiInterface api,
            HashMap<String, HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>> results)
            throws Exception {
        Metrics<Annotation> metrics = new Metrics<Annotation>();
        StrongAnnotationMatch m = new StrongAnnotationMatch(api);
        float threshold = 0;
        System.out.print("Doing native D2W annotations... ");
        List<HashSet<Annotation>> computedAnnotations = doD2WAnnotations(tagger, ds, state);
        System.out.println("Done with all documents.");
        for (threshold = 0; threshold <= 1; threshold += THRESHOLD_STEP) {
            MetricsResultSet rs = metrics.getResult(computedAnnotations, ds.getD2WGoldStandardList(), m);
            updateThresholdRecords(results, m.getName(), tagger.getName(), ds.getName(), (float) threshold, rs);
        }
    }

    public static void computeMetricsD2WReducedFromSa2W(Sa2WSystem tagger, D2WDataset ds, ExperimentTaskState state,
            WikipediaApiInterface api,
            HashMap<String, HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>> results)
            throws Exception {
        Metrics<Annotation> metrics = new Metrics<Annotation>();
        StrongAnnotationMatch m = new StrongAnnotationMatch(api);
        System.out.println("Doing annotations... ");
        List<HashSet<ScoredAnnotation>> computedAnnotations = doSa2WAnnotations(tagger, ds, state);
        System.out.println("Done with all documents.");
        System.out.printf("Testing with tagger: %s, dataset: %s, for values of the score threshold in [0,1].%n",
                tagger.getName(), ds.getName());
        for (double threshold = 0; threshold <= 1; threshold += THRESHOLD_STEP) {

            List<HashSet<Annotation>> reducedAnns = ProblemReduction.Sa2WToD2WList(computedAnnotations,
                    ds.getMentionsInstanceList(), (float) threshold);
            MetricsResultSet rs = metrics.getResult(reducedAnns, ds.getD2WGoldStandardList(), m);
            updateThresholdRecords(results, m.getName(), tagger.getName(), ds.getName(), (float) threshold, rs);
        }
    }

    public static HashMap<String, HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>> performC2WExpVarThreshold(
            Vector<MatchRelation<Tag>> matchRels, Vector<A2WSystem> a2wAnnotators, Vector<Sa2WSystem> sa2wAnnotators,
            Vector<Sc2WSystem> sc2wTaggers, Vector<C2WSystem> c2wTaggers, Vector<C2WDataset> dss,
            ExperimentTaskState state, WikipediaApiInterface api) throws Exception {
        HashMap<String, HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>> result = new HashMap<String, HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>>();
        for (MatchRelation<Tag> m : matchRels)
            for (C2WDataset ds : dss) {
                System.out.println("Testing " + ds.getName() + " with score threshold parameter...");

                if (sa2wAnnotators != null)
                    for (Sa2WSystem t : sa2wAnnotators) {
                        computeMetricsC2WReducedFromSa2W(m, t, ds, api, state, result);
                    }

                if (sc2wTaggers != null)
                    for (Sc2WSystem t : sc2wTaggers) {
                        computeMetricsC2WReducedFromSc2W(m, t, ds, api, state, result);
                    }
                if (c2wTaggers != null)
                    for (C2WSystem t : c2wTaggers) {
                        computeMetricsC2W(m, t, ds, api, state, result);
                    }

                System.out.println("Flushing Wikipedia API cache...");
                api.flush();
            }
        return result;
    }

    public static HashMap<String, HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>> performA2WExpVarThreshold(
            Vector<MatchRelation<Annotation>> metrics, Vector<A2WSystem> a2wTaggers, Vector<Sa2WSystem> sa2wTaggers,
            Vector<A2WDataset> dss, ExperimentTaskState state, WikipediaApiInterface api) throws Exception {
        HashMap<String, HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>> result = new HashMap<String, HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>>();
        for (MatchRelation<Annotation> metric : metrics) {
            for (A2WDataset ds : dss) {
                if (sa2wTaggers != null)
                    for (Sa2WSystem t : sa2wTaggers) {
                        System.out.println("Testing " + ds.getName() + " on " + t.getName()
                                + " with score threshold parameter...");
                        computeMetricsA2WReducedFromSa2W(metric, t, ds, state, api, result);
                    }

                if (a2wTaggers != null)
                    for (A2WSystem t : a2wTaggers) {
                        System.out.println("Testing " + ds.getName() + " on " + t.getName()
                                + " with score threshold parameter...");
                        computeMetricsA2WFakeReductionToSa2W(metric, t, ds, state, api, result);
                    }

                System.out.println("Flushing Wikipedia API cache...");
                api.flush();
            }
        }
        return result;
    }

    public static HashMap<String, HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>> performD2WExpVarThreshold(
            Vector<D2WSystem> d2wAnnotators, Vector<Sa2WSystem> sa2wAnnotators, Vector<D2WDataset> dss,
            ExperimentTaskState state, WikipediaApiInterface api) throws Exception {
        HashMap<String, HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>> result = new HashMap<String, HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>>();
        for (D2WDataset ds : dss) {
            if (sa2wAnnotators != null)
                for (Sa2WSystem t : sa2wAnnotators) {
                    System.out.println("Testing " + ds.getName() + " on " + t.getName()
                            + " with score threshold parameter...");
                    computeMetricsD2WReducedFromSa2W(t, ds, state, api, result);
                }
            if (d2wAnnotators != null)
                for (D2WSystem t : d2wAnnotators) {
                    System.out.println("Testing " + ds.getName() + " on " + t.getName()
                            + " with score threshold parameter...");
                    computeMetricsD2WFakeReductionToSa2W(t, ds, state, api, result);
                }

            System.out.println("Flushing Wikipedia API cache...");
            api.flush();
        }
        return result;
    }

    private static void updateThresholdRecords(
            HashMap<String, HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>> threshRecords,
            String metricsName, String taggerName, String datasetName, float threshold, MetricsResultSet rs) {
        HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>> bestThreshold;
        if (!threshRecords.containsKey(metricsName))
            threshRecords.put(metricsName, new HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>());
        bestThreshold = threshRecords.get(metricsName);

        HashMap<String, HashMap<Float, MetricsResultSet>> firstLevel;
        if (!bestThreshold.containsKey(taggerName))
            bestThreshold.put(taggerName, new HashMap<String, HashMap<Float, MetricsResultSet>>());
        firstLevel = bestThreshold.get(taggerName);

        HashMap<Float, MetricsResultSet> secondLevel;
        if (!firstLevel.containsKey(datasetName))
            firstLevel.put(datasetName, new HashMap<Float, MetricsResultSet>());
        secondLevel = firstLevel.get(datasetName);

        // populate the hash table with the new record.
        secondLevel.put(threshold, rs);
    }

    public static Pair<Float, MetricsResultSet> getBestRecord(
            HashMap<String, HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>> threshResults,
            String metricsName, String taggerName, String datasetName) {
        HashMap<Float, MetricsResultSet> records = threshResults.get(metricsName).get(taggerName).get(datasetName);
        List<Float> thresholds = new Vector<Float>(records.keySet());
        Collections.sort(thresholds);
        Pair<Float, MetricsResultSet> bestRecord = null;
        for (Float t : thresholds)
            if (bestRecord == null || records.get(t).getMacroF1() > bestRecord.second.getMacroF1())
                bestRecord = new Pair<Float, MetricsResultSet>(t, records.get(t));
        return bestRecord;
    }

    public static HashMap<Float, MetricsResultSet> getRecords(
            HashMap<String, HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>> threshResults,
            String metricsName, String taggerName, String datasetName) {
        HashMap<Float, MetricsResultSet> records = threshResults.get(metricsName).get(taggerName).get(datasetName);
        return records;

    }

//    public static MetricsResultSet performMentionSpottingExp(MentionSpotter spotter, D2WDataset ds) throws Exception {
//        List<HashSet<Mention>> output = doSpotMentions(spotter, ds);
//        Metrics<Mention> metrics = new Metrics<Mention>();
//        return metrics.getResult(output, ds.getMentionsInstanceList(), new MentionMatch());
//    }
//
//    public static void performMentionSpottingExp(List<MentionSpotter> mentionSpotters, List<D2WDataset> dss)
//            throws Exception {
//        for (MentionSpotter spotter : mentionSpotters) {
//            for (D2WDataset ds : dss) {
//                System.out.println("Testing Spotter " + spotter.getName() + " on dataset " + ds.getName());
//                System.out.println("Doing spotting... ");
//                MetricsResultSet rs = performMentionSpottingExp(spotter, ds);
//                System.out.println("Done with all documents.");
//
//                System.out.printf("%s / %s%n%s%n%n", spotter.getName(), ds.getName(), rs);
//            }
//        }
//
//    }
//
//    public static MetricsResultSet performCandidateSpottingExp(CandidatesSpotter spotter, D2WDataset dss,
//            WikipediaApiInterface api) throws Exception {
//        Metrics<MultipleAnnotation> metrics = new Metrics<MultipleAnnotation>();
//
//        List<HashSet<MultipleAnnotation>> gold = annotationToMulti(dss.getD2WGoldStandardList());
//
//        List<HashSet<MultipleAnnotation>> output = new Vector<HashSet<MultipleAnnotation>>();
//        for (String text : dss.getTextInstanceList())
//            output.add(spotter.getSpottedCandidates(text));
//
//        // Filter system annotations so that only those contained in the dataset
//        // AND in the output are taken into account.
//        output = mentionSubstraction(output, gold);
//        gold = mentionSubstraction(gold, output);
//
//        return metrics.getResult(output, gold, new MultiEntityMatch(api));
//
//    }

    public static Integer[] candidateCoverageDistributionExp(CandidatesSpotter spotter, D2WDataset dss,
            WikipediaApiInterface api) throws Exception {
        List<HashSet<MultipleAnnotation>> gold = annotationToMulti(dss.getD2WGoldStandardList());

        List<HashSet<MultipleAnnotation>> output = new Vector<HashSet<MultipleAnnotation>>();
        for (String text : dss.getTextInstanceList())
            output.add(spotter.getSpottedCandidates(text));

        output = mentionSubstraction(output, gold);
        gold = mentionSubstraction(gold, output);

        Vector<Integer> positions = new Vector<>();
        for (int i = 0; i < output.size(); i++) {
            HashSet<MultipleAnnotation> outI = output.get(i);
            HashSet<MultipleAnnotation> goldI = gold.get(i);
            for (MultipleAnnotation outAnn : outI)
                for (MultipleAnnotation goldAnn : goldI)
                    if (outAnn.overlaps(goldAnn)) {
                        int goldCand = goldAnn.getCandidates()[0];
                        int candIdx = 0;
                        for (; candIdx < outAnn.getCandidates().length; candIdx++)
                            if (outAnn.getCandidates()[candIdx] == goldCand) {
                                positions.add(candIdx);
                                break;
                            }
                        if (candIdx == outAnn.getCandidates().length)
                            positions.add(-1);
                    }
        }

        return positions.toArray(new Integer[positions.size()]);

    }

    private static <T extends Mention> List<HashSet<T>> mentionSubstraction(List<HashSet<T>> list1,
            List<HashSet<T>> list2) {
        List<HashSet<T>> list1filtered = new Vector<HashSet<T>>();
        for (int i = 0; i < list1.size(); i++) {
            HashSet<T> filtered1 = new HashSet<T>();
            list1filtered.add(filtered1);
            for (T a : list1.get(i)) {
                boolean found = false;
                for (T goldA : list2.get(i))
                    if (a.getPosition() == goldA.getPosition() && a.getLength() == goldA.getLength()) {
                        found = true;
                        break;
                    }
                if (found)
                    filtered1.add(a);
            }
        }
        return list1filtered;
    }

    private static List<HashSet<MultipleAnnotation>> annotationToMulti(List<HashSet<Annotation>> d2wGoldStandardList) {
        List<HashSet<MultipleAnnotation>> res = new Vector<HashSet<MultipleAnnotation>>();
        for (HashSet<Annotation> annSet : d2wGoldStandardList) {
            HashSet<MultipleAnnotation> multiAnn = new HashSet<MultipleAnnotation>();
            res.add(multiAnn);
            for (Annotation a : annSet)
                multiAnn.add(new MultipleAnnotation(a.getPosition(), a.getLength(), new int[] { a.getConcept() }));
        }
        return res;
    }

    public static List<HashSet<ScoredAnnotation>> doSa2WAnnotations(Sa2WSystem annotator, TopicDataset ds,
            ExperimentTaskState state) throws Exception {
        List<HashSet<ScoredAnnotation>> computedAnns = new Vector<HashSet<ScoredAnnotation>>();
        HashSet<ScoredAnnotation> res;
        for (String doc : ds.getTextInstanceList()) {
            res = annotator.solveSa2W(doc);
            computedAnns.add(res);
            state.increaseExperimentStepCount();
        }
        return computedAnns;
    }

    /**
     * Use the given tagger to annotate the whole dataset.
     * 
     * @param annotator
     *            the system used to tag the dataset.
     * @param datasetTexts
     *            the documents of the dataset.
     * @return a list containing the annotations found by the tagger. The
     *         annotations are in the same order of the documents given by
     *         argument.
     * @throws Exception
     */
    public static List<HashSet<Annotation>> doA2WAnnotations(A2WSystem annotator, C2WDataset ds,
            ExperimentTaskState state) throws Exception {
        List<HashSet<Annotation>> computedAnns = new Vector<HashSet<Annotation>>();
        HashSet<Annotation> res;
        for (String doc : ds.getTextInstanceList()) {
            res = annotator.solveA2W(doc);
            computedAnns.add(res);
            state.increaseExperimentStepCount();
        }
        return computedAnns;
    }

    public static List<HashSet<Annotation>> doD2WAnnotations(D2WSystem annotator, D2WDataset ds,
            ExperimentTaskState state) throws Exception {
        List<HashSet<Annotation>> computedAnns = new ArrayList<HashSet<Annotation>>();
        String doc;
        HashSet<Mention> mentions;
        HashSet<Annotation> res;
        for (int i = 0; i < ds.getTextInstanceList().size(); i++) {
            doc = ds.getTextInstanceList().get(i);
            mentions = ds.getMentionsInstanceList().get(i);
            res = annotator.solveD2W(doc, mentions);
            computedAnns.add(res);
            state.increaseExperimentStepCount();
        }
        return computedAnns;
    }

    public static List<HashSet<Tag>> doC2WTags(C2WSystem tagger, C2WDataset ds, ExperimentTaskState state)
            throws Exception {
        List<HashSet<Tag>> computedTags = new Vector<HashSet<Tag>>();
        HashSet<Tag> res;
        for (String doc : ds.getTextInstanceList()) {
            res = tagger.solveC2W(doc);
            computedTags.add(res);
            state.increaseExperimentStepCount();
        }
        return computedTags;
    }

    public static List<HashSet<ScoredTag>> doSc2WTags(Sc2WSystem tagger, C2WDataset ds, ExperimentTaskState state)
            throws Exception {
        List<HashSet<ScoredTag>> computedTags = new Vector<HashSet<ScoredTag>>();
        HashSet<ScoredTag> res;
        for (String doc : ds.getTextInstanceList()) {
            res = tagger.solveSc2W(doc);
            computedTags.add(res);
            state.increaseExperimentStepCount();
        }
        return computedTags;
    }
}
