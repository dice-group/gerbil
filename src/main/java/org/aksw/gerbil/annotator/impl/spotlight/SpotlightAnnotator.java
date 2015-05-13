package org.aksw.gerbil.annotator.impl.spotlight;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.annotator.EntityExtractor;
import org.aksw.gerbil.annotator.EntityLinker;
import org.aksw.gerbil.annotator.EntityRecognizer;
import org.aksw.gerbil.annotator.EntityTyper;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TypedSpan;

public class SpotlightAnnotator implements EntityRecognizer, EntityLinker, EntityExtractor, EntityTyper {

    private static final String SERVICE_URL_PARAM_KEY = "org.aksw.gerbil.annotator.impl.spotlight.SpotlightAnnotator.ServieURL";

    public static final String ANNOTATOR_NAME = "DBpedia Spotlight";

    private SpotlightClient client;

    public SpotlightAnnotator() {
        String url = GerbilConfiguration.getInstance().getString(SERVICE_URL_PARAM_KEY);
        if (url != null) {
            client = new SpotlightClient(url);
        } else {
            client = new SpotlightClient();
        }
    }

    @Override
    public String getName() {
        return ANNOTATOR_NAME;
    }

    @Override
    public List<TypedSpan> performTyping(Document document) throws GerbilException {
        try {
            return new ArrayList<TypedSpan>(client.disambiguate(document));
        } catch (IOException e) {
            throw new GerbilException("The DBpedia Spotlight client reported an error.", e,
                    ErrorTypes.UNEXPECTED_EXCEPTION);
        }
    }

    @Override
    public List<MeaningSpan> performExtraction(Document document) throws GerbilException {
        try {
            return new ArrayList<MeaningSpan>(client.annotate(document));
        } catch (IOException e) {
            throw new GerbilException("The DBpedia Spotlight client reported an error.", e,
                    ErrorTypes.UNEXPECTED_EXCEPTION);
        }
    }

    @Override
    public List<MeaningSpan> performLinking(Document document) throws GerbilException {
        try {
            return new ArrayList<MeaningSpan>(client.disambiguate(document));
        } catch (IOException e) {
            throw new GerbilException("The DBpedia Spotlight client reported an error.", e,
                    ErrorTypes.UNEXPECTED_EXCEPTION);
        }
    }

    @Override
    public List<Span> performRecognition(Document document) throws GerbilException {
        try {
            return new ArrayList<Span>(client.spot(document));
        } catch (IOException e) {
            throw new GerbilException("The DBpedia Spotlight client reported an error.", e,
                    ErrorTypes.UNEXPECTED_EXCEPTION);
        }
    }
}
