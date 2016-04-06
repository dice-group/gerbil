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
package org.aksw.gerbil;

import java.util.Map;

import org.aksw.gerbil.dataset.datahub.DatahubNIFLoader;
import org.junit.Ignore;

@Ignore
public class DataHubIOTest {

    public static void main(String[] args) {
        DatahubNIFLoader loader = new DatahubNIFLoader();
        Map<String, String> datasets = loader.getDataSets();
        System.out.println(datasets.toString());
    }
}
