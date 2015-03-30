package org.aksw.gerbil.annotator;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.TypingInfo;

public interface EntityTyper {

    public TypingInfo performTyping(Document document);
}
