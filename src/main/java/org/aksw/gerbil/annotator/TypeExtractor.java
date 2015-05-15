package org.aksw.gerbil.annotator;

import org.aksw.gerbil.datatypes.TypeExtractionResult;
import org.aksw.gerbil.transfer.nif.Document;

@Deprecated
public interface TypeExtractor {

    public TypeExtractionResult performTypeExtraction(Document document);

}
