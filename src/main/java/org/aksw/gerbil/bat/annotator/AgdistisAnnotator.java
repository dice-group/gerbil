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

import it.unipi.di.acube.batframework.data.Annotation;
import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;

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

@Deprecated
public class AgdistisAnnotator extends it.unipi.di.acube.batframework.systemPlugins.AgdistisAnnotator {

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
