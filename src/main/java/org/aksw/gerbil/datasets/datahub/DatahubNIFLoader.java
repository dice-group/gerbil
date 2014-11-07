package org.aksw.gerbil.datasets.datahub;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.aksw.gerbil.config.GerbilConfiguration;
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
import com.google.common.collect.Sets;

public class DatahubNIFLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatahubNIFLoader.class);

    private static final String DATAHUB_NIF_CORPUS_META_INF_URL_PROPERTY_NAME = "org.aksw.gerbil.datasets.DatahubNIFLoader.metaInfURL";
    private static final String DATAHUB_TAG_INF_URL_PROPERTY_NAME = "org.aksw.gerbil.datasets.DatahubNIFLoader.tagInfURL";
    private static final String DATAHUB_NEEDED_TAGS_ARRAY_PROPERTY_NAME = "org.aksw.gerbil.datasets.DatahubNIFLoader.corpusTags";

    private RestTemplate rt;
    private String neededTags[];
    private Map<String, String> datasets;

    public DatahubNIFLoader() {
        rt = new RestTemplate();
        init();
    }

    private void init() {
        neededTags = GerbilConfiguration.getInstance().getStringArray(
                DATAHUB_NEEDED_TAGS_ARRAY_PROPERTY_NAME);
        if (neededTags == null) {
            LOGGER.error("Couldn't load the needed property \"{}\".",
                    DATAHUB_NEEDED_TAGS_ARRAY_PROPERTY_NAME);
            neededTags = new String[0];
        }
        List<String> nifDataSets = getNIFDataSets();
        getNIFDataSetsMetaInformation(nifDataSets);
    }

    private void getNIFDataSetsMetaInformation(List<String> nifDataSets) {
        datasets = Maps.newHashMap();
        String nifCorpusMetaInfURL = GerbilConfiguration.getInstance().getString(
                DATAHUB_NIF_CORPUS_META_INF_URL_PROPERTY_NAME);
        if (nifCorpusMetaInfURL == null) {
            LOGGER.error("Couldn't load the needed property \"{}\". Aborting.",
                    DATAHUB_NIF_CORPUS_META_INF_URL_PROPERTY_NAME);
            return;
        }
        // go through all datasets tagged with nif
        for (String d : nifDataSets) {
            ResponseEntity<Dataset.Response> entity = rt.getForEntity(nifCorpusMetaInfURL + d,
                    Dataset.Response.class);
            if (entity.getStatusCode().equals(HttpStatus.OK)) {
                Dataset.Response body = entity.getBody();
                List<Resource> resources = body.getResult().getResources();
                // go through the downloadable Resources
                for (Resource r : resources) {
                    String url = r.getUrl();
                    LOGGER.debug("checking {}", url);
                    HttpHeaders headers = rt.headForHeaders(url);
                    long contentLength = headers.getContentLength();
                    LOGGER.debug("{} bytes", contentLength);
                    // FIXME - put the magic number in application.properties
                    // add if less than 20mb ends with ttl (turtle) but not with dataid.ttl (we aint gonna need it yet)
                    if (contentLength < 20_000_000 && url.endsWith(".ttl") && !url.endsWith("dataid.ttl")) {
                        LOGGER.debug("{}: {} has less than 20mb and is turtle > add to Dataset", d, url);
                        datasets.put(d, url);
                    }
                }
            }
        }

    }

    private List<String> getNIFDataSets() {
        List<String> result = Lists.newArrayList();

        String taggedCorpusURL = GerbilConfiguration.getInstance().getString(
                DATAHUB_TAG_INF_URL_PROPERTY_NAME);
        if (taggedCorpusURL == null) {
            LOGGER.error("Couldn't load the needed property \"{}\". Aborting.",
                    DATAHUB_TAG_INF_URL_PROPERTY_NAME);
        } else {
            Set<String> requestResult, taggedCorpora = null;
            String[] body;
            for (int i = 0; i < neededTags.length; ++i) {
                try {
                    ResponseEntity<String[]> forEntity = rt.getForEntity(taggedCorpusURL + neededTags[i],
                            String[].class);
                    if (forEntity.getStatusCode().equals(HttpStatus.OK)) {
                        body = forEntity.getBody();
                        if (taggedCorpora == null) {
                            taggedCorpora = Sets.newHashSet(body);
                            LOGGER.debug("corpora with \"{}\" tag {}", neededTags[i], taggedCorpora);
                        } else {
                            requestResult = Sets.newHashSet(body);
                            LOGGER.debug("corpora with \"{}\" tag {}", neededTags[i], requestResult);
                            taggedCorpora = Sets.intersection(taggedCorpora, requestResult);
                        }
                    } else {
                        LOGGER.warn("Couldn't get any datasets with the {} tag from DataHubIO. Status: ",
                                neededTags[i], forEntity.getStatusCode());
                    }
                } catch (Exception e) {
                    LOGGER.warn("Couldn't get any datasets with the {} tag from DataHubIO. Exception: {}",
                            neededTags[i], e);
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        DatahubNIFLoader d = new DatahubNIFLoader();
        // d.init();

        for (Entry<String, String> e : d.datasets.entrySet()) {
            LOGGER.debug("{}: {}", e.getKey(), e.getValue());
        }
    }

    public Map<String, String> getDataSets() {
        return datasets;
    }
}
