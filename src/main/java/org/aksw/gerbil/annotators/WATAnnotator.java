/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aksw.gerbil.annotators;


public class WATAnnotator /*implements Sa2WSystem */{

//    public static final String ANNOTATOR_NAME = "WAT";
//
//    private static final String WAT_CONFIG_FILE_PROPERTY_ENDPOINT = "org.aksw.gerbil.annotators.wat.endpoint";
//    private static final String WAT_CONFIG_FILE_PROPERTY_PARAMETERS = "org.aksw.gerbil.annotators.wat.parameters";
//
//    private final String endpoint;
//    private final String urlParameters;
//    private final String urlTag;
//    private final String urlD2W;
//
//    private Gson gson;
//    private GsonBuilder gsonBuilder = new GsonBuilder();
//
//    private HttpClient client = HttpClients.createDefault();
//    private static final Logger LOGGER = LoggerFactory.getLogger(WATAnnotator.class);
//
//    public WATAnnotator() throws GerbilException {
//        this.endpoint = GerbilConfiguration.getInstance().getString(WAT_CONFIG_FILE_PROPERTY_ENDPOINT);
//        if (endpoint == null) {
//            throw new GerbilException("Couldn't load endpoint (\"" + WAT_CONFIG_FILE_PROPERTY_ENDPOINT + "\".",
//                    ErrorTypes.ANNOTATOR_LOADING_ERROR);
//        }
//        this.urlParameters = GerbilConfiguration.getInstance().getString(WAT_CONFIG_FILE_PROPERTY_PARAMETERS);
//        if (endpoint == null) {
//            throw new GerbilException("Couldn't load parameters (\"" + WAT_CONFIG_FILE_PROPERTY_PARAMETERS + "\".",
//                    ErrorTypes.ANNOTATOR_LOADING_ERROR);
//        }
//        this.urlTag = String.format("%s/tag/tag", endpoint);
//        this.urlD2W = String.format("%s/tag/disambiguate", endpoint);
//        this.gson = gsonBuilder.create();
//    }
//
//    @Override
//    public HashSet<Annotation> solveA2W(String text) throws AnnotationException {
//        return ProblemReduction.Sa2WToA2W(solveSa2W(text));
//    }
//
//    @Override
//    public HashSet<Tag> solveC2W(String text) throws AnnotationException {
//        return ProblemReduction.A2WToC2W(solveA2W(text));
//    }
//
//    @Override
//    public String getName() {
//        return String.format("WAT (endpoint=%s, ulrParameters=%s)", endpoint, urlParameters);
//    }
//
//    @Override
//    public long getLastAnnotationTime() {
//        return -1;
//    }
//
//    public HashSet<Annotation> solveD2WParams(String text, HashSet<Mention> mentions) throws AnnotationException {
//        HashSet<Annotation> res = new HashSet<>();
//        JsonObject obj;
//
//        try {
//            obj = queryJson(text, mentions, urlD2W);
//        } catch (Exception e) {
//            throw new AnnotationException("An error occurred while querying WAT API. Message: " + e.getMessage());
//        }
//
//        JsonArray jsAnnotations = obj.getAsJsonArray("annotations");
//        for (int i = 0; i < jsAnnotations.size(); i++) {
//            JsonObject js_ann = jsAnnotations.get(i).getAsJsonObject();
//
//            int start = js_ann.get("start").getAsInt();
//            int end = js_ann.get("end").getAsInt();
//            int id = js_ann.get("id").getAsInt();
//
//            Mention m = new Mention(start, end - start);
//            if (mentions.contains(m))
//                res.add(new Annotation(m.getPosition(), m.getLength(), id));
//        }
//        return res;
//    }
//
//    @Override
//    public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions) throws AnnotationException {
//        return solveD2WParams(text, mentions);
//    }
//
//    @Override
//    public HashSet<ScoredTag> solveSc2W(String text) throws AnnotationException {
//        HashSet<ScoredTag> res = new HashSet<>();
//        JsonObject obj;
//
//        try {
//            obj = queryJson(text, null, urlTag);
//        } catch (Exception e) {
//            throw new AnnotationException("An error occurred while querying WAT API. Message: " + e.getMessage());
//        }
//
//        try {
//            JsonArray jsAnnotations = obj.getAsJsonArray("annotations");
//            for (int i = 0; i < jsAnnotations.size(); i++) {
//                JsonObject js_ann = jsAnnotations.get(i).getAsJsonObject();
//                JsonArray jsRanking = js_ann.getAsJsonArray("ranking");
//                for (int j = 0; j < jsRanking.size(); j++) {
//                    JsonObject jsCand = jsRanking.get(j).getAsJsonObject();
//                    int id = jsCand.get("id").getAsInt();
//                    double rho = jsCand.get("score").getAsDouble();
//                    res.add(new ScoredTag(id, (float) rho));
//                }
//            }
//        } catch (Exception e) {
//            throw new AnnotationException(e.getMessage());
//        }
//
//        return res;
//    }
//
//    @Override
//    public HashSet<ScoredAnnotation> solveSa2W(String text) throws AnnotationException {
//        HashSet<ScoredAnnotation> res = new HashSet<>();
//        JsonObject obj;
//
//        try {
//            obj = queryJson(text, null, urlTag);
//        } catch (Exception e) {
//            throw new AnnotationException("An error occurred while querying WAT API. Message: " + e.getMessage());
//        }
//
//        try {
//            JsonArray jsAnnotations = obj.getAsJsonArray("annotations");
//            for (int i = 0; i < jsAnnotations.size(); i++) {
//                JsonObject js_ann = jsAnnotations.get(i).getAsJsonObject();
//
//                int start = js_ann.get("start").getAsInt();
//                int end = js_ann.get("end").getAsInt();
//                int id = js_ann.get("id").getAsInt();
//                double rho = js_ann.get("rho").getAsDouble();
//
//                // Mention m = new Mention(start, end - start);
//                res.add(new ScoredAnnotation(start, end - start, id, (float) rho));
//            }
//        } catch (Exception e) {
//            throw new AnnotationException(e.getMessage());
//        }
//        return res;
//    }
//
//    private JsonObject queryJson(String text, Set<Mention> mentions, String url) throws IOException {
//
//        JsonObject parameters = new JsonObject();
//
//        if (mentions != null) {
//            JsonArray mentionsJson = new JsonArray();
//            for (Mention m : mentions) {
//                JsonObject mentionJson = new JsonObject();
//                mentionJson.addProperty("start", m.getPosition());
//                mentionJson.addProperty("end", m.getPosition() + m.getLength());
//                mentionsJson.add(mentionJson);
//            }
//            parameters.add("spans", mentionsJson);
//        }
//        parameters.addProperty("text", text);
//
//        HttpPost request = new HttpPost(String.format("%s?%s", url, urlParameters));
//        request.addHeader("Content-Type", "application/json");
//        request.setEntity(new StringEntity(gson.toJson(parameters), "UTF8"));
//        request.addHeader("Accept", "application/json");
//
//        CloseableHttpResponse response = (CloseableHttpResponse) client.execute(request);
//        InputStream is = null;
//        HttpEntity entity = null;
//        try {
//            StatusLine status = response.getStatusLine();
//            if ((status.getStatusCode() < 200) || (status.getStatusCode() >= 300)) {
//                entity = response.getEntity();
//                LOGGER.error("The response had a wrong status: \"" + status.toString() + "\". Content of response: \""
//                        + IOUtils.toString(entity.getContent()) + "\". Returning null.");
//                return null;
//            }
//            entity = response.getEntity();
//            is = entity.getContent();
//            return new JsonParser().parse(IOUtils.toString(is)).getAsJsonObject();
//        } catch (Exception e) {
//            LOGGER.error("Couldn't request annotation for given text. Returning null.", e);
//        } finally {
//            IOUtils.closeQuietly(is);
//            if (entity != null) {
//                EntityUtils.consume(entity);
//            }
//            if (response != null) {
//                response.close();
//            }
//        }
//        return null;
//    }
}
