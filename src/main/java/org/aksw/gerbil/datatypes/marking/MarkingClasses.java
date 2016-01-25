package org.aksw.gerbil.datatypes.marking;

public enum MarkingClasses {

    IN_KB("InKB"), EE("EE"), IN_KB_GS("GSInKB");
    
    public static final int NUMBER_OF_CLASSES = MarkingClasses.values().length;
    
    private final String label;

    private MarkingClasses(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
