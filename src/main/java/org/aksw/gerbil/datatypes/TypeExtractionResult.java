package org.aksw.gerbil.datatypes;

import java.util.List;

import org.aksw.gerbil.transfer.nif.TypingInfo;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;

public class TypeExtractionResult {

    public List<NamedEntity> types;
    public TypingInfo typeInfo;

    public TypeExtractionResult(List<NamedEntity> types, TypingInfo typeInfo) {
        super();
        this.types = types;
        this.typeInfo = typeInfo;
    }

}
