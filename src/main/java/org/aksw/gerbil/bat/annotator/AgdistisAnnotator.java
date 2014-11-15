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
package org.aksw.gerbil.bat.annotator;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashSet;

import org.aksw.gerbil.bat.converter.DBpediaToWikiId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.utils.WikipediaApiInterface;

@Deprecated
public class AgdistisAnnotator extends it.acubelab.batframework.systemPlugins.AgdistisAnnotator {

    protected String host;
    protected int port;
    protected WikipediaApiInterface wikiApi;
    protected JSONParser jsonParser = new JSONParser();

    public AgdistisAnnotator(String host, int port, WikipediaApiInterface wikiApi) {
        super(host, port, wikiApi);
        this.wikiApi = wikiApi;
        this.host = host;
        this.port = port;
    }
    
    @Override
    public long getLastAnnotationTime() {
        return -1;
    }

    public HashSet<Annotation> getAnnotations(String textWithMentions) throws IOException, ParseException {
        URL agdistisUrl = new URL("http://" + host + ":" + port + "/AGDISTIS");
        String parameters = "type=agdistis&text=" + URLEncoder.encode(textWithMentions, "UTF-8");
        HttpURLConnection slConnection = (HttpURLConnection) agdistisUrl.openConnection();
        slConnection.setDoOutput(true);
        slConnection.setDoInput(true);
        slConnection.setRequestMethod("POST");
        slConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        slConnection.setRequestProperty("charset", "utf-8");
        slConnection.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
        slConnection.setUseCaches(false);

        DataOutputStream wr = new DataOutputStream(slConnection.getOutputStream());
        wr.writeBytes(parameters);
        wr.flush();
        wr.close();

        InputStream in = slConnection.getInputStream();
        HashSet<Annotation> annotations = parseJsonStream(in);
        return annotations;
    }

    private HashSet<Annotation> parseJsonStream(InputStream in) throws IOException, ParseException {
        HashSet<Annotation> annotations = new HashSet<>();

        JSONArray namedEntities = (JSONArray) this.jsonParser.parse(new InputStreamReader(in, "UTF-8"));
        for (Object obj : namedEntities) {
            JSONObject namedEntity = (JSONObject) obj;

            long start = (long) namedEntity.get("start");
            long offset = (long) namedEntity.get("offset");
            int position = (int) start;
            int length = (int) offset;

            String url = (String) namedEntity.get("disambiguatedURL");
            if (url == null) {
                // String mention = (String) namedEntity.get("namedEntity");
                // System.err.printf("No entity for \"%s\" at position %d%n", mention, position);
                continue;
            }

            String urlDecoded = URLDecoder.decode(url, "UTF-8");
            int wikiArticle = DBpediaToWikiId.getId(wikiApi, urlDecoded);
            if (wikiArticle == -1)
                System.err.printf("Wiki title of url %s (decoded %s) could not be found.%n", url, urlDecoded);
            annotations.add(new Annotation(position, length, wikiArticle));
        }

        return annotations;
    }
}
