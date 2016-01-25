package org.aksw.gerbil.matching.impl.clas;

import org.aksw.gerbil.datatypes.marking.ClassifiedMarking;
import org.aksw.gerbil.datatypes.marking.MarkingClasses;
import org.aksw.gerbil.transfer.nif.Marking;

/**
 * This classifier is used to set the {@link MarkingClasses#EE} flag of
 * {@link ClassifiedMarking}s. Since it simply inverts the
 * {@link MarkingClasses#IN_KB} flag, it is required to use a classifier
 * that sets classifies the {@link Marking} regarding this class before this
 * classifier is used.
 */
public class EmergingEntityMeaningClassifier<T extends ClassifiedMarking> implements MarkingClassifier<T> {

    @Override
    public void classify(T marking) {
        if (!marking.hasClass(MarkingClasses.IN_KB)) {
            marking.setClass(MarkingClasses.EE);
        }
    }

}