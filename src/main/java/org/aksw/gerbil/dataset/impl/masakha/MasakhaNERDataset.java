package org.aksw.gerbil.dataset.impl.masakha;

import org.aksw.gerbil.dataset.impl.conll.GenericCoNLLDataset;
import org.aksw.gerbil.dataset.impl.conll.CoNLLTypeRetriever;

public class MasakhaNERDataset extends GenericCoNLLDataset {
    private static final int ANNOTATION_COLUMN = 2;
    private static final int URI_COLUMN = 1;
    private static final CoNLLTypeRetriever TYPE_TAGS = new CoNLLTypeRetriever(
    	"GeographicalLocation", null, null, null, null, null, null,
        null, null, null, "language");
    private String languageCode;

    public MasakhaNERDataset(String file) {
        super(file, ANNOTATION_COLUMN, URI_COLUMN, TYPE_TAGS);
    }

    /**
     * Sets the language code for the dataset.
     * 
     * @param language the language code (ISO 639-2)
     */
    public void setLanguage(String language) {
        languageCode = language.toLowerCase();

        switch (languageCode) {
            case "amh":
                TYPE_TAGS.addTypeURI("amharic_type", "http://dbpedia.org/resource/Amharic_language");
                break;
            case "hau":
                TYPE_TAGS.addTypeURI("hausa_type", "http://dbpedia.org/resource/Igbo_language");
                break;
            case "ibo":
                TYPE_TAGS.addTypeURI("igbo_type", "http://dbpedia.org/resource/Kinyarwanda_language");
                break;
            case "kin":
                TYPE_TAGS.addTypeURI("kinyarwanda_type", "http://dbpedia.org/resource/Kinyarwanda_language");
                break;
            case "lug":
                TYPE_TAGS.addTypeURI("luganda_type", "http://dbpedia.org/resource/Luganda_language");
                break;
            case "lu":
                TYPE_TAGS.addTypeURI("luo_type", "http://dbpedia.org/resource/Luo_language");
                break;
            case "pcm":
                TYPE_TAGS.addTypeURI("pidgin_type", "http://dbpedia.org/resource/Nigerian_Pidgin");
                break;
            case "swa":
                TYPE_TAGS.addTypeURI("swahili_type", "http://dbpedia.org/resource/Swahili_language");
                break;
            case "wol":
                TYPE_TAGS.addTypeURI("wolof_type", "http://dbpedia.org/resource/Wolof_language");
                break;
            case "yor":
                TYPE_TAGS.addTypeURI("yoruba_type", "http://dbpedia.org/resource/Yoruba_language");
                break;
        }
    }
}

