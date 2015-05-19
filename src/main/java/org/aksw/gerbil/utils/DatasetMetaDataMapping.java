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

import java.util.HashMap;
import java.util.Map;

/**
 * Very ugly way to manage the mapping of datasets to their metadata objects.
 * 
 * FIXME: This should be part of the dataset objects or their metadata/configuration classes.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * 
 */
public class DatasetMetaDataMapping {

    private static DatasetMetaDataMapping instance;

    public static DatasetMetaDataMapping getInstance() {
        if (instance == null) {
            Map<String, DatasetMetaData> mapping = new HashMap<String, DatasetMetaData>();
            mapping.put("ACE2004", new DatasetMetaData(4.43859649122807, 0.011871246246246246, 373.89473684210526, 57, 253, 0.1413612565, 0.4712041885, 0.2513089005, 0.1361256545));
            mapping.put("AIDA/CoNLL-Complete", new DatasetMetaData(19.966977745872217, 0.10701149602179165, 186.58722182340273, 1393, 27814, 0.4479166667, 0.2000353107, 0.2224576271, 0.1295903955));
            mapping.put("AIDA/CoNLL-Test A", new DatasetMetaData(22.180555555555557, 0.10842064767249768, 204.5787037037037, 216, 4791, 0.4427207637, 0.2285202864, 0.2195704057, 0.1091885442));
            mapping.put("AIDA/CoNLL-Test B", new DatasetMetaData(19.415584415584416, 0.11180635189709329, 173.65367965367966 , 231, 4485, 0.3445692884, 0.2322097378, 0.3164794007, 0.106741573));
            mapping.put("AIDA/CoNLL-Training", new DatasetMetaData(19.596194503171247, 0.10556166115264816, 185.63742071881606, 946, 18538, 0.4244917715, 0.215392062, 0.2303969022, 0.1297192643));
            mapping.put("AQUAINT", new DatasetMetaData(14.54, 0.06594702467343977, 220.48, 50, 727, 0.1379962193, 0.2325141777, 0.0586011342, 0.5708884688));
            mapping.put("DBpediaSpotlight", new DatasetMetaData(5.689655172413793, 0.1986754966887417, 28.637931034482758, 58, 330, 0.0755555556, 0.1555555556, 0.0088888889, 0.76));
            mapping.put("IITB", new DatasetMetaData(109.22330097087378, 0.17017607551279723, 641.8252427184466, 103, 11250, 0.1041726619, 0.1136690647, 0.0581294964, 0.724028777));
            mapping.put("KORE50", new DatasetMetaData(2.86, 0.2234375, 12.8, 50, 143, 0.475, 0.1166666667, 0.2333333333, 0.175));
            mapping.put("Meij", new DatasetMetaData(1.6175298804780875, 0.11850554582603619, 13.649402390438247, 502, 812,  0.1508704062, 0.1721470019, 0.1141199226, 0.5628626692));
            mapping.put("Microposts2014-Test", new DatasetMetaData(1.2360515021459229, 0.07175602949970102, 17.225751072961373, 1165, 1440, 0.2801082544, 0.148849797, 0.1542625169, 0.4167794317));
            mapping.put("Microposts2014-Train", new DatasetMetaData(1.614957264957265, 0.09288663848195851, 17.386324786324785, 2340, 3779, 0.2660714286, 0.1952380952, 0.1946428571, 0.344047619));
            mapping.put("MSNBC", new DatasetMetaData(32.5, 0.05975912475866507, 543.85, 20, 650,  0.1951219512, 0.2787456446, 0.181184669, 0.3449477352));
            mapping.put("N3-RSS-500", new DatasetMetaData(0.992, 0.03199174406604747, 31.008, 500, 496, 0.1530612245, 0.3163265306, 0.3469387755, 0.1836734694));
            mapping.put("N3-Reuters-128", new DatasetMetaData(4.8515625, 0.03919959601060472, 123.765625, 128, 621, 0.3490813648, 0.1837270341, 0.3097112861, 0.157480315));
            instance = new DatasetMetaDataMapping(mapping);
        }
        return instance;
    }

    private Map<String, DatasetMetaData> mapping;

    public DatasetMetaDataMapping(Map<String, DatasetMetaData> mapping) {
        super();
        this.mapping = mapping;
    }

    public DatasetMetaData getMetaData(String datasetName) {
        if (mapping.containsKey(datasetName)) {
            return mapping.get(datasetName);
        } else {
            return null;
        }
    }
}
