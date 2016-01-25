package org.aksw.gerbil.datatypes.marking;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;

import com.carrotsearch.hppc.BitSet;

public class ClassifiedNamedEntity extends NamedEntity implements ClassifiedSpanMeaning {

    protected BitSet classBits = new BitSet(MarkingClasses.NUMBER_OF_CLASSES);

    public ClassifiedNamedEntity(int startPosition, int length, Set<String> uris) {
        super(startPosition, length, uris);
    }

    @Override
    public List<MarkingClasses> getClasses() {
        List<MarkingClasses> classes = new ArrayList<MarkingClasses>();
        for (int i = 0; i < MarkingClasses.NUMBER_OF_CLASSES; ++i) {
            if (classBits.get(i)) {
                classes.add(MarkingClasses.values()[i]);
            }
        }
        return classes;
    }

    @Override
    public boolean hasClass(MarkingClasses clazz) {
        return classBits.get(clazz.ordinal());
    }

    @Override
    public void setClass(MarkingClasses clazz) {
        classBits.set(clazz.ordinal());
    }

    @Override
    public void unsetClass(MarkingClasses clazz) {
        classBits.clear(clazz.ordinal());
    }

    @Override
    public Marking getWrappedMarking() {
        return this;
    }

}
