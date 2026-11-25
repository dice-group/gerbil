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

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.aksw.gerbil.datatypes.ErrorTypes;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ExperimentDAOImplTest {

    private JDBCDataSource dataSource;
    private ExperimentDAOImpl dao;

    @Before
    public void setUp() throws SQLException {
        dataSource = new JDBCDataSource();
        dataSource.setUrl("jdbc:hsqldb:mem:experiment_count_" + System.nanoTime());
        dataSource.setUser("SA");
        dataSource.setPassword("");

        execute("CREATE TABLE ExperimentTasks (id INTEGER PRIMARY KEY, state INTEGER)");
        execute("CREATE TABLE Experiments (id VARCHAR(300) NOT NULL, taskId INTEGER NOT NULL, "
                + "PRIMARY KEY (id, taskId), FOREIGN KEY (taskId) REFERENCES ExperimentTasks(id))");
        execute("CREATE TABLE ExperimentTasks_SubTasks (taskId INTEGER NOT NULL, subTaskId INTEGER NOT NULL, "
                + "PRIMARY KEY (taskId, subTaskId), FOREIGN KEY (taskId) REFERENCES ExperimentTasks(id), "
                + "FOREIGN KEY (subTaskId) REFERENCES ExperimentTasks(id))");
        dao = new ExperimentDAOImpl(dataSource);
    }

    @After
    public void tearDown() throws SQLException {
        execute("DROP SCHEMA PUBLIC CASCADE");
    }

    @Test
    public void countsRawExperimentAndTaskRowsIndependently() throws SQLException {
        // Mixed states and a stored subtask prove that the task count has no execution-state or hierarchy filter.
        execute("INSERT INTO ExperimentTasks (id, state) VALUES (1, " + ExperimentDAO.TASK_FINISHED + ")");
        execute("INSERT INTO ExperimentTasks (id, state) VALUES (2, "
                + ExperimentDAO.TASK_STARTED_BUT_NOT_FINISHED_YET + ")");
        execute("INSERT INTO ExperimentTasks (id, state) VALUES (3, "
                + ErrorTypes.UNEXPECTED_EXCEPTION.getErrorCode() + ")");
        execute("INSERT INTO ExperimentTasks (id, state) VALUES (4, " + ExperimentDAO.TASK_FINISHED + ")");
        execute("INSERT INTO ExperimentTasks_SubTasks (taskId, subTaskId) VALUES (1, 4)");

        // Reusing an experiment ID proves that physical association rows are not deduplicated by ID.
        execute("INSERT INTO Experiments (id, taskId) VALUES ('shared-id', 1)");
        execute("INSERT INTO Experiments (id, taskId) VALUES ('shared-id', 2)");
        execute("INSERT INTO Experiments (id, taskId) VALUES ('other-id', 3)");

        assertEquals(Integer.valueOf(3), dao.countExperiments());
        assertEquals(Integer.valueOf(4), dao.countExperimentTasks());

        execute("INSERT INTO Experiments (id, taskId) VALUES ('shared-id', 3)");
        assertEquals(Integer.valueOf(4), dao.countExperiments());
        assertEquals(Integer.valueOf(4), dao.countExperimentTasks());

        execute("INSERT INTO ExperimentTasks (id, state) VALUES (5, " + ExperimentDAO.TASK_FINISHED + ")");
        assertEquals(Integer.valueOf(4), dao.countExperiments());
        assertEquals(Integer.valueOf(5), dao.countExperimentTasks());
    }

    private void execute(String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
