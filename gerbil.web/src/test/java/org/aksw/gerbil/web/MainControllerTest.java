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
package org.aksw.gerbil.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.database.SimpleLoggingDAO4Debugging;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Test;

public class MainControllerTest {

    @Test
    public void countReturnsBothStatisticsAsJsonNumbers() throws Exception {
        MainController controller = new MainController();
        ExperimentDAO dao = new SimpleLoggingDAO4Debugging() {
            @Override
            public Integer countExperiments() {
                return 13;
            }

            @Override
            public Integer countExperimentTasks() {
                return 42;
            }
        };
        Field daoField = MainController.class.getDeclaredField("dao");
        daoField.setAccessible(true);
        daoField.set(controller, dao);

        Object parsedResponse = JSONValue.parse(controller.count());

        // Parsing the body verifies both the exact schema and JSON number types, rather than matching text.
        assertTrue(parsedResponse instanceof JSONObject);
        JSONObject counts = (JSONObject) parsedResponse;
        assertEquals(2, counts.size());
        assertEquals(Long.valueOf(13), counts.get("experiments"));
        assertEquals(Long.valueOf(42), counts.get("experiment_tasks"));
    }
}
