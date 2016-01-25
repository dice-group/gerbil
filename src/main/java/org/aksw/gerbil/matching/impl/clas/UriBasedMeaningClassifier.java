package org.aksw.gerbil.matching.impl.clas;

import org.aksw.gerbil.datatypes.marking.ClassifiedMeaning;
import org.aksw.gerbil.datatypes.marking.MarkingClasses;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;

public class UriBasedMeaningClassifier<T extends ClassifiedMeaning> implements MarkingClassifier<T> {

    protected UriKBClassifier classifier;
    protected MarkingClasses clazz;

    public UriBasedMeaningClassifier(UriKBClassifier classifier, MarkingClasses clazz) {
        this.classifier = classifier;
        this.clazz = clazz;
    }

    @Override
    public void classify(T marking) {
        if (classifier.containsKBUri(marking.getUris())) {
            marking.setClass(clazz);
        }
    }
}
