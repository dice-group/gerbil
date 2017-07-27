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
package org.aksw.gerbil.database;

import java.util.Arrays;
import java.util.List;

import org.aksw.gerbil.annotator.decorator.TimeMeasuringAnnotatorDecorator;
import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.aksw.gerbil.evaluate.impl.ModelComparator;
import org.aksw.gerbil.evaluate.impl.ROCEvaluator;

import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.ObjectIntOpenHashMap;

/**
 * Workaround to handle additional results and their names.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class ResultNameToIdMapping {

    public static final int UKNOWN_RESULT_TYPE = -1;

    private static ResultNameToIdMapping instance;

    public static ResultNameToIdMapping getInstance() {
        if (instance == null) {
            ObjectIntOpenHashMap<String> nameToIdMap = new ObjectIntOpenHashMap<String>();

            nameToIdMap.put(ModelComparator.F1_SCORE_NAME, 0);
            nameToIdMap.put(ModelComparator.PRECISION_NAME, 1);
            nameToIdMap.put(ModelComparator.RECALL_NAME, 2);
            nameToIdMap.put(ROCEvaluator.AUC_NAME, 3);
            nameToIdMap.put(ROCEvaluator.ROC_NAME, 4);
            

            nameToIdMap.put(TimeMeasuringAnnotatorDecorator.AVG_TIME_RESULT_NAME, 20);

            instance = new ResultNameToIdMapping(nameToIdMap, IntObjectOpenHashMap.from(nameToIdMap.values().toArray(),
                    nameToIdMap.keys().toArray(String.class)));
        }
        return instance;
    }

    protected ObjectIntOpenHashMap<String> nameToIdMap;
    protected IntObjectOpenHashMap<String> idToNameMap;

    protected ResultNameToIdMapping(ObjectIntOpenHashMap<String> nameToIdMap,
            IntObjectOpenHashMap<String> idToNameMap) {
        this.nameToIdMap = nameToIdMap;
        this.idToNameMap = idToNameMap;
    }

    public int getResultId(String name) {
        return nameToIdMap.getOrDefault(name, UKNOWN_RESULT_TYPE);
    }

    public String getResultName(int id) {
        return idToNameMap.getOrDefault(id, null);
    }

    public int[] listAdditionalResultIds(List<ExperimentTaskResult> results) {
        IntOpenHashSet ids = new IntOpenHashSet();
        for (ExperimentTaskResult result : results) {
            if (result.hasAdditionalResults()) {
                ids.addAll(result.getAdditionalResults().keys());
            }
        }
        int idArray[] = ids.toArray();
        Arrays.sort(idArray);
        return idArray;
    }

    public String[] getNamesOfResultIds(int[] additionalResultIds) {
        String names[] = new String[additionalResultIds.length];
        for (int i = 0; i < additionalResultIds.length; ++i) {
            names[i] = getResultName(additionalResultIds[i]);
        }
        return names;
    }
}
