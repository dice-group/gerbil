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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.datatypes.ExperimentType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ExperimentTypeComparatorTest {

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        for (ExperimentType type : ExperimentType.values()) {
            testConfigs.add(new Object[] { type, type, 0 });
        }
        testConfigs.add(new Object[] { ExperimentType.ERec, ExperimentType.A2KB, -1 });
        testConfigs.add(new Object[] { ExperimentType.A2KB, ExperimentType.ERec, 1 });
        testConfigs.add(new Object[] { ExperimentType.D2KB, ExperimentType.A2KB, -1 });
        testConfigs.add(new Object[] { ExperimentType.A2KB, ExperimentType.D2KB, 1 });
        testConfigs.add(new Object[] { ExperimentType.D2KB, ExperimentType.ERec, -1 });
        testConfigs.add(new Object[] { ExperimentType.ERec, ExperimentType.D2KB, 1 });
        testConfigs.add(new Object[] { ExperimentType.ERec, ExperimentType.OKE_Task1, -1 });
        testConfigs.add(new Object[] { ExperimentType.OKE_Task1, ExperimentType.ERec, 1 });
        testConfigs.add(new Object[] { ExperimentType.OKE_Task1, ExperimentType.OKE_Task2, -1 });
        testConfigs.add(new Object[] { ExperimentType.OKE_Task2, ExperimentType.OKE_Task1, 1 });
        return testConfigs;
    }

    private ExperimentType expType1;
    private ExperimentType expType2;
    private int expectedResult;

    public ExperimentTypeComparatorTest(ExperimentType expType1, ExperimentType expType2, int expectedResult) {
        this.expType1 = expType1;
        this.expType2 = expType2;
        this.expectedResult = expectedResult;
    }

    @Test
    public void test() {
        ExperimentTypeComparator comparator = new ExperimentTypeComparator();
        int result = comparator.compare(expType1, expType2);
        Assert.assertEquals(expectedResult, result);
    }
}
