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
package org.aksw.gerbil.database;

import java.util.Arrays;
import java.util.List;

import org.aksw.gerbil.annotator.decorator.TimeMeasuringAnnotatorDecorator;
import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.aksw.gerbil.evaluate.impl.InKBClassBasedFMeasureCalculator;
import org.aksw.gerbil.evaluate.impl.FMeasureCalculator;

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

    public static final String ENTITY_IN_KB_CLASS_NAME = "InKB";
    public static final String EMERGING_ENTITY_CLASS_NAME = "EE";

    public static final int UKNOWN_RESULT_TYPE = -1;

    private static ResultNameToIdMapping instance;

    public static ResultNameToIdMapping getInstance() {
        if (instance == null) {
            ObjectIntOpenHashMap<String> nameToIdMap = new ObjectIntOpenHashMap<String>();

            nameToIdMap.put(FMeasureCalculator.MACRO_F1_SCORE_NAME, ExperimentTaskResult.MACRO_F1_MEASURE_INDEX);
            nameToIdMap.put(FMeasureCalculator.MACRO_PRECISION_NAME, ExperimentTaskResult.MACRO_PRECISION_INDEX);
            nameToIdMap.put(FMeasureCalculator.MACRO_RECALL_NAME, ExperimentTaskResult.MACRO_RECALL_INDEX);
            nameToIdMap.put(FMeasureCalculator.MICRO_F1_SCORE_NAME, ExperimentTaskResult.MICRO_F1_MEASURE_INDEX);
            nameToIdMap.put(FMeasureCalculator.MICRO_PRECISION_NAME, ExperimentTaskResult.MICRO_PRECISION_INDEX);
            nameToIdMap.put(FMeasureCalculator.MICRO_RECALL_NAME, ExperimentTaskResult.MICRO_RECALL_INDEX);

            nameToIdMap.put(InKBClassBasedFMeasureCalculator.MACRO_ACCURACY_NAME, 6);
            nameToIdMap.put(InKBClassBasedFMeasureCalculator.MICRO_ACCURACY_NAME, 7);

            nameToIdMap.put(InKBClassBasedFMeasureCalculator.IN_KB_MACRO_F1_SCORE_NAME, 8);
            nameToIdMap.put(InKBClassBasedFMeasureCalculator.IN_KB_MACRO_PRECISION_NAME, 9);
            nameToIdMap.put(InKBClassBasedFMeasureCalculator.IN_KB_MACRO_RECALL_NAME, 10);
            nameToIdMap.put(InKBClassBasedFMeasureCalculator.IN_KB_MICRO_F1_SCORE_NAME, 11);
            nameToIdMap.put(InKBClassBasedFMeasureCalculator.IN_KB_MICRO_PRECISION_NAME, 12);
            nameToIdMap.put(InKBClassBasedFMeasureCalculator.IN_KB_MICRO_RECALL_NAME, 13);

            nameToIdMap.put(InKBClassBasedFMeasureCalculator.EE_MACRO_F1_SCORE_NAME, 14);
            nameToIdMap.put(InKBClassBasedFMeasureCalculator.EE_MACRO_PRECISION_NAME, 15);
            nameToIdMap.put(InKBClassBasedFMeasureCalculator.EE_MACRO_RECALL_NAME, 16);
            nameToIdMap.put(InKBClassBasedFMeasureCalculator.EE_MICRO_F1_SCORE_NAME, 17);
            nameToIdMap.put(InKBClassBasedFMeasureCalculator.EE_MICRO_PRECISION_NAME, 18);
            nameToIdMap.put(InKBClassBasedFMeasureCalculator.EE_MICRO_RECALL_NAME, 19);

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
