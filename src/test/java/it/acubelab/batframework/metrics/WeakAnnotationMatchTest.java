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
package it.acubelab.batframework.metrics;

import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.data.Mention;
import it.acubelab.batframework.data.Tag;
import it.acubelab.batframework.problems.A2WDataset;
import it.acubelab.batframework.problems.A2WSystem;
import it.acubelab.batframework.problems.TopicDataset;
import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.utils.AnnotationException;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.aksw.gerbil.Experimenter;
import org.aksw.gerbil.annotators.AbstractAnnotatorConfiguration;
import org.aksw.gerbil.bat.datasets.FileBasedNIFDataset;
import org.aksw.gerbil.database.SimpleLoggingDAO4Debugging;
import org.aksw.gerbil.datasets.AbstractDatasetConfiguration;
import org.aksw.gerbil.datasets.NIFFileDatasetConfig;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.utils.SingletonWikipediaApi;
import org.aksw.simba.topicmodeling.concurrent.overseers.simple.SimpleOverseer;
import org.apache.jena.riot.Lang;
import org.junit.Ignore;

@Ignore
public class WeakAnnotationMatchTest {

    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
        WikipediaApiInterface wikiAPI = SingletonWikipediaApi.getInstance();
        // System.out.println(wikiAPI.getIdByTitle("Berlin"));
        // System.out.println(wikiAPI.getIdByTitle("Germany"));

        // ExperimentTaskConfiguration taskConfigs[] = new
        // ExperimentTaskConfiguration[] { new ExperimentTaskConfiguration(
        // new LocalTestAnnoConfig(), new LocalTestDataConfig(),
        // ExperimentType.A2KB,
        // Matching.WEAK_ANNOTATION_MATCH) };
        ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] { new ExperimentTaskConfiguration(
                new LocalTestAnnoConfig(), new NIFFileDatasetConfig(wikiAPI, "GT",
                        "src/test/resources/doc1-4/gt/eval-de-ne-only-small4.ttl", false, ExperimentType.A2KB),
                ExperimentType.A2KB, Matching.WEAK_ANNOTATION_MATCH) };
        Experimenter experimenter = new Experimenter(wikiAPI, new SimpleOverseer(), new SimpleLoggingDAO4Debugging(),
                taskConfigs, "WEAK_ANNO_TEST");
        experimenter.run();
    }

    private static class LocalTestAnnoConfig extends AbstractAnnotatorConfiguration implements A2WSystem {

        private Map<String, HashSet<Annotation>> annotatorResults = new HashMap<String, HashSet<Annotation>>();

        public LocalTestAnnoConfig() {
            super("Local Test Annotator", false, ExperimentType.A2KB);
            // // Document 1 with one disambiguated entity in the groundtruth
            // and
            // // none found by our system.
            // annotatorResults.put(
            // "Document 1 with one disambiguated entity in the groundtruth and none found by our system.",
            // new HashSet<Annotation>());
            // // Document 2 with one disambiguated entity in the groundtruth
            // and
            // // the same entity detected and correctly
            // // disambiguated by our system.
            // HashSet<Annotation> result = new HashSet<Annotation>();
            // result.add(new Annotation(0, 8, 3354));
            // annotatorResults
            // .put("Document 2 with one disambiguated entity in the groundtruth and the same entity detected and correctly disambiguated by our system.",
            // result);
            // // Document 3 with no entity in groundtruth and none assigned by
            // our
            // // system.
            // annotatorResults.put(
            // "Document 3 with one disambiguated entity in the groundtruth and none found by our system.",
            // new HashSet<Annotation>());
            // // Document 4 with one disambiguated entity in the groundtruth,
            // and
            // // one found by our system. Our system's
            // // annotation has the same beginIndex and endIndex as the
            // // groundtruth, but different disambiguation
            // // (different value of taIdentRef).
            // result = new HashSet<Annotation>();
            // result.add(new Annotation(0, 8, 11867));
            // annotatorResults
            // .put("Document 4 with one disambiguated entity in the groundtruth, and one found by our system. Our system's annotation has the same beginIndex and endIndex as the groundtruth, but different disambiguation (different value of taIdentRef).",
            // result);

            FileBasedNIFDataset dataset = new FileBasedNIFDataset(SingletonWikipediaApi.getInstance(),
                    "src/test/resources/doc1-4/thd/exp4-out.nt", "GT", Lang.TTL);
            try {
                dataset.init();
            } catch (GerbilException e) {
                e.printStackTrace();
            }
            List<HashSet<Annotation>> annotations = dataset.getA2WGoldStandardList();
            List<String> instances = dataset.getTextInstanceList();
            for (int i = 0; i < instances.size(); ++i) {
                annotatorResults.put(instances.get(i), annotations.get(i));
            }

        }

        @Override
        protected TopicSystem loadAnnotator(ExperimentType type) throws Exception {
            return this;
        }

        @Override
        public HashSet<Tag> solveC2W(String text) throws AnnotationException {
            throw new IllegalStateException("Shouldn't be accessed in this test.");
        }

        @Override
        public long getLastAnnotationTime() {
            return -1;
        }

        @Override
        public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions) throws AnnotationException {
            throw new IllegalStateException("Shouldn't be accessed in this test.");
        }

        @Override
        public HashSet<Annotation> solveA2W(String text) throws AnnotationException {
            return annotatorResults.get(text);
        }
    }

    @SuppressWarnings("unused")
    private static class LocalTestDataConfig extends AbstractDatasetConfiguration implements A2WDataset {

        private List<String> instances = new ArrayList<String>();
        private List<HashSet<Annotation>> annotations = new ArrayList<HashSet<Annotation>>();

        public LocalTestDataConfig() {
            super("Local Test Dataset", false, ExperimentType.A2KB);
            // Document 1 with one disambiguated entity in the groundtruth and
            // none found by our system.
            instances.add("Document 1 with one disambiguated entity in the groundtruth and none found by our system.");
            HashSet<Annotation> localAnnotations = new HashSet<Annotation>();
            localAnnotations.add(new Annotation(0, 8, 3354));
            annotations.add(localAnnotations);
            // Document 2 with one disambiguated entity in the groundtruth and
            // the same entity detected and correctly
            // disambiguated by our system.
            instances
                    .add("Document 2 with one disambiguated entity in the groundtruth and the same entity detected and correctly disambiguated by our system.");
            localAnnotations = new HashSet<Annotation>();
            localAnnotations.add(new Annotation(0, 8, 3354));
            annotations.add(localAnnotations);
            // Document 3 with no entity in groundtruth and none assigned by our
            // system.
            instances.add("Document 3 with one disambiguated entity in the groundtruth and none found by our system.");
            annotations.add(new HashSet<Annotation>());
            // Document 4 with one disambiguated entity in the groundtruth, and
            // one found by our system. Our system's
            // annotation has the same beginIndex and endIndex as the
            // groundtruth, but different disambiguation
            // (different value of taIdentRef).
            instances
                    .add("Document 4 with one disambiguated entity in the groundtruth, and one found by our system. Our system's annotation has the same beginIndex and endIndex as the groundtruth, but different disambiguation (different value of taIdentRef).");
            localAnnotations = new HashSet<Annotation>();
            localAnnotations.add(new Annotation(0, 8, 3354));
            annotations.add(localAnnotations);
        }

        @Override
        protected TopicDataset loadDataset() throws Exception {
            return this;
        }

        @Override
        public int getTagsCount() {
            int sum = 0;
            for (HashSet<Annotation> localAnnotations : annotations) {
                sum += localAnnotations.size();
            }
            return sum;
        }

        @Override
        public List<HashSet<Tag>> getC2WGoldStandardList() {
            throw new IllegalStateException("Shouldn't be accessed in this test.");
        }

        @Override
        public int getSize() {
            return 0;
        }

        @Override
        public List<String> getTextInstanceList() {
            return instances;
        }

        @Override
        public List<HashSet<Mention>> getMentionsInstanceList() {
            throw new IllegalStateException("Shouldn't be accessed in this test.");
        }

        @Override
        public List<HashSet<Annotation>> getD2WGoldStandardList() {
            throw new IllegalStateException("Shouldn't be accessed in this test.");
        }

        @Override
        public List<HashSet<Annotation>> getA2WGoldStandardList() {
            return annotations;
        }

    }
}
