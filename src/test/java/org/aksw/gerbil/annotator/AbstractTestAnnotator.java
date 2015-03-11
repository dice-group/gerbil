package org.aksw.gerbil.annotator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.gerbil.annotators.AbstractAnnotatorConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.transfer.nif.Document;
import org.junit.Ignore;

@Ignore
public class AbstractTestAnnotator extends AbstractAnnotatorConfiguration implements Annotator {

    protected Map<String, Document> uriInstanceMapping;

    public AbstractTestAnnotator(String annotatorName, boolean couldBeCached, List<Document> instances,
            ExperimentType... applicableForExperiment) {
        super(annotatorName, couldBeCached, applicableForExperiment);
        this.uriInstanceMapping = new HashMap<String, Document>(instances.size());
        for (Document document : instances) {
            uriInstanceMapping.put(document.getDocumentURI(), document);
        }
    }

    @Override
    protected Annotator loadAnnotator(ExperimentType type) throws Exception {
        return this;
    }

    protected Document getDocument(String uri) {
        if (uriInstanceMapping.containsKey(uri)) {
            return uriInstanceMapping.get(uri);
        } else {
            return null;
        }
    }
}
