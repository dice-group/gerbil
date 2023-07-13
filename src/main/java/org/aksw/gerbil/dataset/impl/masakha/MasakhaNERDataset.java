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
                TYPE_TAGS.addTypeURI("hausa_type", "http://dbpedia.org/resource/Hausa_language");
                break;
            case "ibo":
                TYPE_TAGS.addTypeURI("igbo_type", "http://dbpedia.org/resource/Igbo_language");
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
            case "bam":
                TYPE_TAGS.addTypeURI("bambara_type", "http://dbpedia.org/resource/Bambara_language");
                break;
            case "ewe":
                TYPE_TAGS.addTypeURI("ewe_type", "http://dbpedia.org/resource/Ewe_language");
                break;
            case "fon":
                TYPE_TAGS.addTypeURI("fon_type", "http://dbpedia.org/resource/Fon_language");
                break;
            case "mos":
                TYPE_TAGS.addTypeURI("mossi_type", "http://dbpedia.org/resource/Mossi_language");
                break;
            case "bbj":
                TYPE_TAGS.addTypeURI("ghomala_type", "http://dbpedia.org/resource/Ghomala_language");
                break;
            case "nya":
                TYPE_TAGS.addTypeURI("chichewa_type", "http://dbpedia.org/resource/Chichewa_language");
                break;
            case "tsn":
                TYPE_TAGS.addTypeURI("setswana_type", "http://dbpedia.org/resource/Setswana_language");
                break;
            case "twi":
                TYPE_TAGS.addTypeURI("twi_type", "http://dbpedia.org/resource/Twi_language");
                break;
            case "sna":
                TYPE_TAGS.addTypeURI("chishona_type", "http://dbpedia.org/resource/Chishona_language");
                break;
            case "xho":
                TYPE_TAGS.addTypeURI("isixhosa_type", "http://dbpedia.org/resource/IsiXhosa_language");
                break;
            case "zul":
                TYPE_TAGS.addTypeURI("isizulu_type", "http://dbpedia.org/resource/IsiZulu_language");
                break;  
        }
    }
}

