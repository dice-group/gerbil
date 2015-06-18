package org.aksw.gerbil.dataset.impl.bat;

import it.unipi.di.acube.batframework.data.Tag;
import it.unipi.di.acube.batframework.problems.A2WDataset;
import it.unipi.di.acube.batframework.problems.C2WDataset;
import it.unipi.di.acube.batframework.problems.TopicDataset;
import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.utils.bat.BAT2NIF_TranslationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatFrameworkDatasetWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatFrameworkDatasetWrapper.class);

    public static final String DATASET_NAME_SUFFIX = " (BAT)";
    public static final String DATASET_URI_PREFIX = "http://gerbil.aksw.org/BAT-Framework/";

    public static Dataset create(TopicDataset dataset, WikipediaApiInterface wikiApi) {
        LOGGER.warn("Using wrappers for BAT framework datasets is not recommended!");
        if (dataset instanceof A2WDataset) {
            return new A2KBDatasetWrapper<A2WDataset>((A2WDataset) dataset, wikiApi);
        }
        if (dataset instanceof C2WDataset) {
            return new C2KBDatasetWrapper<C2WDataset>((C2WDataset) dataset, wikiApi);
        }
        LOGGER.error("Couldn't find a matching wrapper for \"" + dataset.getClass().getName() + "\". Returning null.");
        return null;
    }

    protected abstract static class AbstractTopicSystemWrapper<T extends TopicDataset> extends AbstractDataset {
        protected BAT2NIF_TranslationHelper translater;
        protected List<Document> documents;

        public AbstractTopicSystemWrapper(T dataset, WikipediaApiInterface wikiApi) {
            super(dataset.getName() + DATASET_NAME_SUFFIX);
            this.translater = new BAT2NIF_TranslationHelper(wikiApi);
            // Create the document list
            String uri = DATASET_URI_PREFIX + dataset.getName().replace(' ', '+');
            if (!uri.endsWith("/")) {
                uri += '/';
            }
            documents = new ArrayList<Document>(dataset.getSize());
            int documentId = 0;
            for (String text : dataset.getTextInstanceList()) {
                documents.add(new DocumentImpl(text, uri + Integer.toString(documentId)));
                ++documentId;
            }
        }

        @Override
        public int size() {
            return documents.size();
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<Document> getInstances() {
            return documents;
        }
    }

    protected static class C2KBDatasetWrapper<T extends C2WDataset> extends AbstractTopicSystemWrapper<T> {

        public C2KBDatasetWrapper(T dataset, WikipediaApiInterface wikiApi) {
            super(dataset, wikiApi);
            // Add the tags to the documents
            List<HashSet<Tag>> tagLists = dataset.getC2WGoldStandardList();
            int documentId = 0;
            for (HashSet<Tag> tags : tagLists) {
                this.documents.get(documentId).getMarkings().addAll(translater.translateTags(tags));
                ++documentId;
            }
        }
    }

    protected static class A2KBDatasetWrapper<T extends A2WDataset> extends C2KBDatasetWrapper<T> {

        public A2KBDatasetWrapper(T dataset, WikipediaApiInterface wikiApi) {
            super(dataset, wikiApi);
            // Add the annotations to the documents
            List<HashSet<it.unipi.di.acube.batframework.data.Annotation>> annotationLists = dataset
                    .getA2WGoldStandardList();
            int documentId = 0;
            for (HashSet<it.unipi.di.acube.batframework.data.Annotation> annotations : annotationLists) {
                this.documents.get(documentId).getMarkings().addAll(translater.translateAnnotations(annotations));
                ++documentId;
            }
        }
    }
}
