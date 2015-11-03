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
package org.aksw.gerbil.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Very ugly way to manage the mapping of datasets to their metadata objects.
 * 
 * FIXME: This should be part of the dataset objects or their
 * metadata/configuration classes.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * 
 */
public class DatasetMetaDataMapping {

    private static DatasetMetaDataMapping instance;

    public static DatasetMetaDataMapping getInstance() {
        if (instance == null) {
            Map<String, DatasetMetaData> mapping = new HashMap<String, DatasetMetaData>();
            mapping.put("ACE2004", new DatasetMetaData(5.368421052631579, 0.014358108108108109, 373.89473684210526, 57,
                    306, 0.1413612565, 0.4712041885, 0.2513089005, 0.1361256545));
            mapping.put("AIDA/CoNLL-Complete", new DatasetMetaData(25.074659009332375, 0.14256269183047085,
                    175.8851399856425, 1393, 34929, 0.4479166667, 0.2000353107, 0.2224576271, 0.1295903955));
            mapping.put("AIDA/CoNLL-Test A", new DatasetMetaData(27.39351851851852, 0.14170079268146657,
                    193.31944444444446, 216, 5917, 0.4427207637, 0.2285202864, 0.2195704057, 0.1091885442));
            mapping.put("AIDA/CoNLL-Test B", new DatasetMetaData(24.31168831168831, 0.14901690238013107,
                    163.14718614718615, 231, 5616, 0.3445692884, 0.2322097378, 0.3164794007, 0.106741573));
            mapping.put("AIDA/CoNLL-Training", new DatasetMetaData(24.731501057082454, 0.1413109130004107,
                    175.01479915433404, 946, 23396, 0.4244917715, 0.215392062, 0.2303969022, 0.1297192643));
            mapping.put("AQUAINT", new DatasetMetaData(14.54, 0.06594702467343977, 220.48, 50, 727, 0.1379962193,
                    0.2325141777, 0.0586011342, 0.5708884688));
            mapping.put("DBpediaSpotlight", new DatasetMetaData(5.689655172413793, 0.1986754966887417,
                    28.637931034482758, 58, 330, 0.0755555556, 0.1555555556, 0.0088888889, 0.76));
            mapping.put("IITB", new DatasetMetaData(176.03846153846155, 0.2751799912822594, 639.7211538461538, 104,
                    18308, 0.1041726619, 0.1136690647, 0.0581294964, 0.724028777));
            mapping.put("KORE50",
                    new DatasetMetaData(2.88, 0.225, 12.8, 50, 144, 0.475, 0.1166666667, 0.2333333333, 0.175));
            // mapping.put("Meij", new DatasetMetaData(1.6175298804780875,
            // 0.11850554582603619, 13.649402390438247, 502, 812, 0.1508704062,
            // 0.1721470019, 0.1141199226, 0.5628626692));
            mapping.put("Microposts2014-Test", new DatasetMetaData(1.190521327014218, 0.060829135993800854,
                    19.571563981042654, 1055, 1256, 0.2801082544, 0.148849797, 0.1542625169, 0.4167794317));
            mapping.put("Microposts2014-Train", new DatasetMetaData(1.6333333333333333, 0.09394356503785271,
                    17.386324786324785, 2340, 3822, 0.2660714286, 0.1952380952, 0.1946428571, 0.344047619));
            mapping.put("MSNBC", new DatasetMetaData(37.75, 0.06941252183506481, 543.85, 20, 755, 0.1951219512,
                    0.2787456446, 0.181184669, 0.3449477352));
            mapping.put("N3-RSS-500", new DatasetMetaData(2.0, 0.06449948400412797, 31.008, 500, 1000, 0.1530612245,
                    0.3163265306, 0.3469387755, 0.1836734694));
            mapping.put("N3-Reuters-128", new DatasetMetaData(6.875, 0.055548541850776414, 123.765625, 128, 880,
                    0.3490813648, 0.1837270341, 0.3097112861, 0.157480315));
            // mapping.put("OKE 2015 Task 1 example set", new
            // DatasetMetaData(2.0,0.08955223880597014,22.333333333333332,3,6,
            // ?,?, ?, ?));
            // mapping.put("OKE 2015 Task 1 evaluation dataset", new
            // DatasetMetaData(6.574257425742574,0.21671018276762402,30.336633663366335,101,664,
            // ?,?, ?, ?));
            // mapping.put("OKE 2015 Task 1 gold standard sample", new
            // DatasetMetaData(3.5208333333333335,0.1736896197327852,20.270833333333332,96,338,
            // ?,?, ?, ?));

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
