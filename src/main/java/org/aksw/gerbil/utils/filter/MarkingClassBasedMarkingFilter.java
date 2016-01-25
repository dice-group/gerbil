package org.aksw.gerbil.utils.filter;

import org.aksw.gerbil.datatypes.marking.ClassifiedMarking;
import org.aksw.gerbil.datatypes.marking.MarkingClasses;

public class MarkingClassBasedMarkingFilter<T extends ClassifiedMarking> extends AbstractMarkingFilter<T> {

    protected MarkingClasses markingClass;

    public MarkingClassBasedMarkingFilter(MarkingClasses markingClass) {
        this.markingClass = markingClass;
    }

    @Override
    public boolean isMarkingGood(T marking) {
        return marking.hasClass(markingClass);
    }
}
