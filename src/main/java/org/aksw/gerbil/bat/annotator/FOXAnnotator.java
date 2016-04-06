/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.bat.annotator;

@Deprecated
public class FOXAnnotator /*implements Sa2WSystem*/ {

//    private static final Logger LOGGER = LoggerFactory.getLogger(FOXAnnotator.class);
//
//    /*
//     * static {
//     * PropertyConfigurator.configure(FOXAnnotator.class.getResourceAsStream("log4jFOXAnnotator.properties"));
//     * }
//     */
//
//    public static final String NAME = "FOX";
//    protected IFoxApi fox = new FoxApi();
//    protected WikipediaApiInterface wikiApi;
//
//    public static void main(String[] a) {
//        String test = "The philosopher and mathematician Gottfried Wilhelm Leibniz was born in Leipzig.";
//        HashSet<Annotation> set = new FOXAnnotator(SingletonWikipediaApi.getInstance()).solveA2W(test);
//        LOGGER.info("Got {} annotations.", set.size());
//    }
//
//    public FOXAnnotator(WikipediaApiInterface wikiApi) {
//        this.wikiApi = wikiApi;
//    }
//
//    @Override
//    public HashSet<ScoredTag> solveSc2W(String text) throws AnnotationException {
//        return ProblemReduction.Sa2WToSc2W(solveSa2W(text));
//    }
//
//    @Override
//    public HashSet<ScoredAnnotation> solveSa2W(String text) throws AnnotationException {
//        return (HashSet<ScoredAnnotation>) fox(text);
//    }
//
//    @Override
//    public HashSet<Annotation> solveA2W(String text) throws AnnotationException {
//        return ProblemReduction.Sa2WToA2W(solveSa2W(text), Float.MIN_VALUE);
//    }
//
//    @Override
//    public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions) throws AnnotationException {
//        return ProblemReduction.Sa2WToD2W(solveSa2W(text), mentions, Float.MIN_VALUE);
//
//    }
//
//    @Override
//    public HashSet<Tag> solveC2W(String text) throws AnnotationException {
//        return ProblemReduction.A2WToC2W(solveA2W(text));
//    }
//
//    protected Set<ScoredAnnotation> fox(String text) throws AnnotationException {
//        if (LOGGER.isTraceEnabled())
//            LOGGER.trace("Got text \"{}\".", text);
//
//        Set<ScoredAnnotation> set = new HashSet<>();
//        try {
//            // request FOX
//            FoxResponse response = fox
//                    .setInput(text)
//                    .setLightVersion(FoxParameter.FOXLIGHT.OFF)
//                    .setOutputFormat(FoxParameter.OUTPUT.JSONLD)
//                    .setTask(FoxParameter.TASK.NER)
//                    .send();
//
//            // parse results
//            if (response != null && response.getOutput() != null) {
//                JSONObject outObj = new JSONObject(response.getOutput());
//                if (outObj.has("@graph")) {
//
//                    JSONArray graph = outObj.getJSONArray("@graph");
//                    for (int i = 0; i < graph.length(); i++)
//                        set.addAll(add(graph.getJSONObject(i)));
//
//                } else
//                    set.addAll(add(outObj));
//            }
//        } catch (Exception e) {
//            LOGGER.error("Got an exception while communicating with the FOX web service.", e);
//            throw new AnnotationException("Got an exception while communicating with the FOX web service: "
//                    + e.getLocalizedMessage());
//        }
//        if (LOGGER.isTraceEnabled())
//            LOGGER.trace("Found {} annotations.", set.size());
//        return set;
//    }
//
//    protected Set<ScoredAnnotation> add(JSONObject entity) throws Exception {
//        Set<ScoredAnnotation> set = new HashSet<>();
//        try {
//
//            if (entity != null && entity.has("means") && entity.has("beginIndex") && entity.has("ann:body")) {
//
//                String uri = entity.getString("means");
//                String body = entity.getString("ann:body");
//                Object begin = entity.get("beginIndex");
//
//                int wikiID = DBpediaToWikiId.getId(wikiApi, URLDecoder.decode(uri, "UTF-8"));
//                if (wikiID > -1) {
//                    if (begin instanceof JSONArray) {
//                        // for all indices
//                        for (int ii = 0; ii < ((JSONArray) begin).length(); ii++) {
//                            int b = Integer.valueOf(((JSONArray) begin).getString(ii));
//                            set.add(new ScoredAnnotation(b, b + body.length(), wikiID, 1f));
//                            if (LOGGER.isDebugEnabled())
//                                LOGGER.debug("[begin={}, body={}, id={}]", b, body, wikiID);
//                        }
//                    } else if (begin instanceof String) {
//                        // just one index
//                        int b = Integer.valueOf((String) begin);
//                        set.add(new ScoredAnnotation(b, b + body.length(), wikiID, 1f));
//                        if (LOGGER.isDebugEnabled())
//                            LOGGER.debug("[begin={}, body={}, id={}]", b, body, wikiID);
//
//                    } else if (LOGGER.isDebugEnabled())
//                        LOGGER.debug("Couldn't find index");
//                } else if (LOGGER.isDebugEnabled())
//                    LOGGER.debug("Couldn't find ".concat(uri));
//            }
//        } catch (Exception e) {
//            LOGGER.error("Got an Exception while parsing the response of FOX.", e);
//            throw new Exception("Got an Exception while parsing the response of FOX.", e);
//        }
//        return set;
//    }
//
//    @Override
//    public String getName() {
//        return NAME;
//    }
//
//    @Override
//    public long getLastAnnotationTime() {
//        return -1;
//    }
}
