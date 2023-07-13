package org.aksw.gerbil.dataset.impl.indqner;

import org.aksw.gerbil.dataset.impl.conll.CoNLLTypeRetriever;
import org.aksw.gerbil.dataset.impl.conll.GenericCoNLLDataset;

/**
 * Implementation of the IndQNERDataset class, which represents an
 * InitializableDataset for the IndQNER dataset.
 * 
 * @author Neha
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class IndQNERDataset extends GenericCoNLLDataset {

    private static final int ANNOTATION_COLUMN = 1;
    private static final int URI_COLUMN = -1;
    private static final CoNLLTypeRetriever TYPE_TAGS = new CoNLLTypeRetriever("GeographicalLocation", null, null, null,
            null, "Person", null, null, null, null, null);

    public IndQNERDataset(String file) {
        super(file, ANNOTATION_COLUMN, URI_COLUMN, TYPE_TAGS);
        TYPE_TAGS.addTypeURI("AfterlifeLocation", "https://corpus.quran.com/concept.jsp?id=afterlife-location");
        TYPE_TAGS.addTypeURI("Allah", "https://corpus.quran.com/concept.jsp?id=allah");
        TYPE_TAGS.addTypeURI("Angel", "https://github.com/dice-group/IndQNER/Angel"); // TODO: replace
        TYPE_TAGS.addTypeURI("Artifact", "https://corpus.quran.com/concept.jsp?id=artifact");
        TYPE_TAGS.addTypeURI("AstronomicalBody", "https://corpus.quran.com/concept.jsp?id=astronomical-body");
        TYPE_TAGS.addTypeURI("Color", "https://corpus.quran.com/concept.jsp?id=color");
        TYPE_TAGS.addTypeURI("Event", "https://corpus.quran.com/concept.jsp?id=event");
        TYPE_TAGS.addTypeURI("Food", "https://github.com/dice-group/IndQNER/Food"); // TODO: replace
        TYPE_TAGS.addTypeURI("HolyBook", "https://corpus.quran.com/concept.jsp?id=holy-book");
        TYPE_TAGS.addTypeURI("Language", "https://corpus.quran.com/concept.jsp?id=language");
        TYPE_TAGS.addTypeURI("Messenger", "https://github.com/dice-group/IndQNER/Messenger"); // TODO: replace
        TYPE_TAGS.addTypeURI("Prophet", "https://github.com/dice-group/IndQNER/Prophet"); // TODO: replace
        TYPE_TAGS.addTypeURI("Religion", "https://corpus.quran.com/concept.jsp?id=religion");
        TYPE_TAGS.addTypeURI("Throne", "https://corpus.quran.com/concept.jsp?id=allah%27s-throne");
    }
}
