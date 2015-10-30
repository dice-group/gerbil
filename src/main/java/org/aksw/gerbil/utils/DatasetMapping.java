/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
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
package org.aksw.gerbil.utils;

import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;

/**
 * This class is a very ugly workaround performing the mapping from dataset
 * names to {@link DatasetConfiguration} objects and from an
 * {@link ExperimentType} to a list of {@link DatasetConfiguration}s that are
 * usable for this {@link ExperimentType}.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * @author Giuseppe Rizzo <giuse.rizzo@gmail.com>
 */
@Deprecated
public class DatasetMapping {

    // private static final Logger LOGGER =
    // LoggerFactory.getLogger(DatasetMapping.class);
    //
//     private static final String UPLOADED_FILES_PATH_PROPERTY_KEY =
//     "org.aksw.gerbil.UploadPath";
//     private static final String UPLOADED_DATASET_SUFFIX = " (uploaded)";
    //
    // private static DatasetMapping instance = null;
    //
    // private synchronized static DatasetMapping getInstance() {
    // if (instance == null) {
    // Map<String, DatasetConfiguration> nameDatasetMapping = new
    // HashMap<String, DatasetConfiguration>();
    // @SuppressWarnings("deprecation")
    // WikipediaApiInterface wikiApi = SingletonWikipediaApi.getInstance();
    //
    // nameDatasetMapping.put(ACE2004DatasetConfig.DATASET_NAME, new
    // ACE2004DatasetConfig(wikiApi));
    // nameDatasetMapping.put(AQUAINTDatasetConfiguration.DATASET_NAME, new
    // AQUAINTDatasetConfiguration(wikiApi));
    // nameDatasetMapping.put(IITBDatasetConfig.DATASET_NAME, new
    // IITBDatasetConfig(wikiApi));
    // nameDatasetMapping.put(MeijDatasetConfig.DATASET_NAME, new
    // MeijDatasetConfig(wikiApi));
    // nameDatasetMapping.put(MSNBCDatasetConfig.DATASET_NAME, new
    // MSNBCDatasetConfig(wikiApi));
    //
    // nameDatasetMapping.put(AIDACoNLLDatasetConfig.DATASET_NAME_START +
    // "-Training"
    // + BatFrameworkDatasetWrapper.DATASET_NAME_SUFFIX, new
    // AIDACoNLLDatasetConfig(
    // AIDACoNLLDatasetConfig.AIDACoNLLChunk.TRAINING, wikiApi));
    // nameDatasetMapping.put(AIDACoNLLDatasetConfig.DATASET_NAME_START +
    // "-Test A"
    // + BatFrameworkDatasetWrapper.DATASET_NAME_SUFFIX, new
    // AIDACoNLLDatasetConfig(
    // AIDACoNLLDatasetConfig.AIDACoNLLChunk.TEST_A, wikiApi));
    // nameDatasetMapping.put(AIDACoNLLDatasetConfig.DATASET_NAME_START +
    // "-Test B"
    // + BatFrameworkDatasetWrapper.DATASET_NAME_SUFFIX, new
    // AIDACoNLLDatasetConfig(
    // AIDACoNLLDatasetConfig.AIDACoNLLChunk.TEST_B, wikiApi));
    // nameDatasetMapping.put(AIDACoNLLDatasetConfig.DATASET_NAME_START +
    // "-Complete"
    // + BatFrameworkDatasetWrapper.DATASET_NAME_SUFFIX, new
    // AIDACoNLLDatasetConfig(
    // AIDACoNLLDatasetConfig.AIDACoNLLChunk.COMPLETE, wikiApi));
    //
    // nameDatasetMapping.put(Microposts2014Config.DATASET_NAME_START + "-Train"
    // + BatFrameworkDatasetWrapper.DATASET_NAME_SUFFIX, new
    // Microposts2014Config(
    // Microposts2014Config.Microposts2014Chunk.TRAIN, wikiApi));
    // nameDatasetMapping.put(Microposts2014Config.DATASET_NAME_START + "-Test"
    // + BatFrameworkDatasetWrapper.DATASET_NAME_SUFFIX, new
    // Microposts2014Config(
    // Microposts2014Config.Microposts2014Chunk.TEST, wikiApi));
    //
    // // Got through the known NIF datasets
    // KnownNIFFileDatasetConfig.NIFDatasets knownNifDatasets[] =
    // KnownNIFFileDatasetConfig.NIFDatasets.values();
    // for (int i = 0; i < knownNifDatasets.length; ++i) {
    // nameDatasetMapping.put(knownNifDatasets[i].getDatasetName(), new
    // KnownNIFFileDatasetConfig(
    // knownNifDatasets[i]));
    // }
    // OKETask1DatasetConfig.NIFDatasets okeTask1NifDatasets[] =
    // OKETask1DatasetConfig.NIFDatasets.values();
    // for (int i = 0; i < okeTask1NifDatasets.length; ++i) {
    // nameDatasetMapping.put(okeTask1NifDatasets[i].getDatasetName(), new
    // OKETask1DatasetConfig(
    // okeTask1NifDatasets[i]));
    // }
    //
    // // load Datahub data
    // DatahubNIFLoader datahub = new DatahubNIFLoader();
    // Map<String, String> datasets = datahub.getDataSets();
    // for (String datasetName : datasets.keySet()) {
    // nameDatasetMapping.put(datasetName, new DatahubNIFConfig(datasetName,
    // datasets.get(datasetName), true));
    // }
    //
    // instance = new DatasetMapping(nameDatasetMapping);
    // }
    // return instance;
    // }
    //
    // public static Set<String> getDatasetsForExperimentType(ExperimentType
    // type) {
    // DatasetMapping datasets = getInstance();
    // Set<String> names = new HashSet<String>();
    // for (String datasetName : datasets.mapping.keySet()) {
    // if (datasets.mapping.get(datasetName).isApplicableForExperiment(type)) {
    // names.add(datasetName);
    // }
    // }
    // return names;
    // }
    //
//     public static DatasetConfiguration getDatasetConfig(String name) {
//     DatasetMapping datasets = getInstance();
//     if (datasets.mapping.containsKey(name)) {
//     return datasets.mapping.get(name);
//     } else {
//     if (name.startsWith("NIFDS_")) {
//     String uploadedFilesPath = GerbilConfiguration.getInstance()
//     .getString(UPLOADED_FILES_PATH_PROPERTY_KEY);
//     if (uploadedFilesPath == null) {
//     LOGGER.error("Couldn't process uploaded file request, because the upload path is not set (\"{}\"",
//     UPLOADED_FILES_PATH_PROPERTY_KEY);
//     }
//     // This describes a NIF based web service
//     // The name should have the form "NIFDS_name(uri)"
//     int pos = name.indexOf('(');
//     if (pos < 0) {
//     LOGGER.error("Couldn't parse the definition of this NIF based web service \""
//     + name
//     + "\". Returning null.");
//     return null;
//     }
//     String uri = uploadedFilesPath + name.substring(pos + 1, name.length() -
//     1);
//     // remove "NIFDS_" from the name
//     name = name.substring(6, pos) + UPLOADED_DATASET_SUFFIX;
//     LOGGER.error("name={}, uri={}", name, uri);
//     return new NIFFileDatasetConfig(name, uri, false, ExperimentType.EExt,
//     ExperimentType.OKE_Task1,
//     ExperimentType.OKE_Task2);
//     }
//     LOGGER.error("Got an unknown annotator name\"" + name +
//     "\". Returning null.");
//     return null;
//     }
//     }
    //
    // public static List<DatasetConfiguration> getDatasetConfigurations() {
    // return getInstance().getDatasetConfigs();
    // }
    //
    // private final Map<String, DatasetConfiguration> mapping;
    //
    // private DatasetMapping(Map<String, DatasetConfiguration> mapping) {
    // this.mapping = mapping;
    // }
    //
    // protected List<DatasetConfiguration> getDatasetConfigs() {
    // return new ArrayList<DatasetConfiguration>(this.mapping.values());
    // }
}
