package org.aksw.gerbil.matching.impl;

import org.aksw.gerbil.datatypes.marking.ClassifiedMeaning;
import org.aksw.gerbil.datatypes.marking.MarkingClasses;

public class ClassifiedMeaningMatchingsSearcher<T extends ClassifiedMeaning>
        extends AbstractMeaningMatchingsSearcher<T> {

    protected boolean hasKbUri(ClassifiedMeaning meaning) {
        return meaning.hasClass(MarkingClasses.IN_KB);
    }
}
