package org.aksw.gerbil.qa.datatypes;

import java.util.Collection;
import java.util.Set;

import org.aksw.gerbil.datatypes.marking.MeaningsContainingMarking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.data.Annotation;

public class ResourceAnswerSet extends AnswerSet<Annotation> implements MeaningsContainingMarking {

    public ResourceAnswerSet(Set<Annotation> answers) {
        super(answers);
    }

    @Override
    public Collection<? extends Meaning> getMeanings() {
        return getAnswers();
    }

}
