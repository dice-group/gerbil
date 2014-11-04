package org.aksw.gerbil.datasets.datahub;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aksw.gerbil.datasets.datahub.model.Dataset;
import org.aksw.gerbil.datasets.datahub.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DatahubNIFLoader {

    private static final Logger logger = LoggerFactory.getLogger(DatahubNIFLoader.class);

    // TODO - add to application.properties
    private static final String NIF_TAGGED_CORPURA_URL = "http://datahub.io/api/1/rest/tag/nif";
    private static final String NIF_CORPUS_META_INF_URL = "http://datahub.io/api/3/action/package_show?id=";
    private RestTemplate rt;
    private Map<String, String> datasets;

    public DatahubNIFLoader() {
        rt = new RestTemplate();
        init();
    }

    private void init() {
        List<String> nifDataSets = getNIFDataSets();
        getNIFDataSetsMetaInformation(nifDataSets);
    }

    private void getNIFDataSetsMetaInformation(List<String> nifDataSets) {
        datasets = Maps.newHashMap();
        // go through all datasets tagged with nif
        for (String d : nifDataSets) {
            ResponseEntity<Dataset.Response> entity = rt.getForEntity(NIF_CORPUS_META_INF_URL + d,
                    Dataset.Response.class);
            if (entity.getStatusCode().equals(HttpStatus.OK)) {
                Dataset.Response body = entity.getBody();
                List<Resource> resources = body.getResult().getResources();
                // go through the downloadable Resources
                for (Resource r : resources) {
                    String url = r.getUrl();
                    logger.debug("checking {}", url);
                    HttpHeaders headers = rt.headForHeaders(url);
                    long contentLength = headers.getContentLength();
                    logger.debug("{} bytes", contentLength);
                    // FIXME - put the magic number in application.properties
                    // add if less than 20mb ends with ttl (turtle) but not with dataid.ttl (we aint gonna need it yet)
                    if (contentLength < 20_000_000 && url.endsWith(".ttl") && !url.endsWith("dataid.ttl")) {
                        logger.debug("{}: {} has less than 20mb and is turtle > add to Dataset", d, url);
                        datasets.put(d, url);
                    }
                }
            }
        }

    }

    private List<String> getNIFDataSets() {
        List<String> result = Lists.newArrayList();
        ResponseEntity<String[]> forEntity = rt.getForEntity(NIF_TAGGED_CORPURA_URL, String[].class);
        if (forEntity.getStatusCode().equals(HttpStatus.OK)) {
            logger.debug("everything is ok");
            String[] body = forEntity.getBody();
            result = Lists.newArrayList(body);
            logger.debug("corpura {}", datasets);
        }
        return result;
    }

    public static void main(String[] args) {
        DatahubNIFLoader d = new DatahubNIFLoader();
        d.init();

        for (Entry<String, String> e : d.datasets.entrySet()) {
            logger.debug("{}: {}", e.getKey(), e.getValue());
        }
    }

    public Map<String, String> getDataSets() {
        return datasets;
    }
}
