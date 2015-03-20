package org.aksw.gerbil.semantic.kb;

import java.util.Arrays;

import org.aksw.gerbil.semantic.kb.AbstractWhiteListBasedUriKBClassifier;

public class SimpleWhiteListBasedUriKBClassifier extends AbstractWhiteListBasedUriKBClassifier {

    public SimpleWhiteListBasedUriKBClassifier(String... kbNamespace) {
        super(Arrays.asList(kbNamespace));
    }

}
