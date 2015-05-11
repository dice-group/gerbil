package org.aksw.gerbil.database;

import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.aksw.gerbil.evaluate.impl.FMeasureCalculator;

import com.carrotsearch.hppc.ObjectIntOpenHashMap;

public class ResultNameToIdMapping {

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

//            nameToIdMap.put(key, value);
//            public static final int EL_EE_MICRO_F1_MEASURE_INDEX = 6;
//            public static final int EL_EE_MICRO_PRECISION_INDEX = 7;
//            public static final int EL_EE_MICRO_RECALL_INDEX = 8;
//            public static final int EL_EE_MACRO_F1_MEASURE_INDEX = 9;
//            public static final int EL_EE_MACRO_PRECISION_INDEX = 10;
//            public static final int EL_EE_MACRO_RECALL_INDEX = 11;
//            public static final int ACCURACY_INDEX = 12;
        }
        return instance;
    }
}
