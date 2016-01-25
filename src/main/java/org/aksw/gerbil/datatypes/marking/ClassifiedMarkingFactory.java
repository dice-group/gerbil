package org.aksw.gerbil.datatypes.marking;

import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.data.ScoredNamedEntity;

public class ClassifiedMarkingFactory {

    public static ClassifiedMarking createClassifiedMeaning(Marking marking) {
        if (marking instanceof ScoredNamedEntity) {
            ScoredNamedEntity sne = (ScoredNamedEntity) marking;
            return new ClassifiedScoredNamedEntity(sne.getStartPosition(), sne.getLength(), sne.getUris(),
                    sne.getConfidence());
        } else if (marking instanceof MeaningSpan) {
            MeaningSpan ne = (MeaningSpan) marking;
            return new ClassifiedNamedEntity(ne.getStartPosition(), ne.getLength(), ne.getUris());
        } else if (marking instanceof Meaning) {
            return new ClassifiedAnnotation(((Meaning) marking).getUris());
        }
        return null;
    }
}
