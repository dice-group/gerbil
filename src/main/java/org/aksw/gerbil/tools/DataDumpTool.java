package org.aksw.gerbil.tools;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.database.ExperimentDAOImpl;
import org.aksw.gerbil.dataid.DataIDGenerator;
import org.aksw.gerbil.datatypes.ExperimentTaskStatus;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DataDumpTool implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataDumpTool.class);

    private static final String OUTPUT_FILE_NAME = "datadump.nt";
    private static final Lang OUTPUT_LANG = Lang.NT;

    private static final String GERBIL_BASE_URL = "http://gerbil.aksw.org/gerbil/";

    private static final String EXPERIMENT_IDS_QUERY = "SELECT id, taskId FROM Experiments";

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = null;
        DataDumpTool tool = null;
        try {
            context = new ClassPathXmlApplicationContext("/spring/database/database-context.xml");
            DriverManagerDataSource database = context.getBean("databaseDataSource", DriverManagerDataSource.class);
            tool = new DataDumpTool(GERBIL_BASE_URL, database);
            tool.run(OUTPUT_FILE_NAME, OUTPUT_LANG);
        } finally {
            IOUtils.closeQuietly(tool);
            IOUtils.closeQuietly(context);
        }
    }

    private String gerbilBaseUrl;
    private DataSource dataSource;
    private ExperimentDAO dao;

    public DataDumpTool(String gerbilBaseUrl, DataSource dataSource) {
        this.gerbilBaseUrl = gerbilBaseUrl;
        this.dataSource = dataSource;
        this.dao = new ExperimentDAOImpl(dataSource);
    }

    public void run(String outputFileName, Lang language) {
        List<ExperimentToTaskLink> experiments = loadExperiments();
        LOGGER.info("Loaded {} experiment to experiment task links from the database.", experiments.size());

        DataIDGenerator generator = new DataIDGenerator(gerbilBaseUrl);

        Model model = generator.generateDataIDModel();

        Resource experimentResource;
        String taskUri;
        List<Resource> listOfTasks;
        Map<String, Resource> experimentInstances = new HashMap<String, Resource>();
        Map<String, List<Resource>> taskResources = new HashMap<String, List<Resource>>();
        int count = 0;
        for (ExperimentToTaskLink link : experiments) {
            // Get the experiment resource
            if (experimentInstances.containsKey(link.experimentId)) {
                experimentResource = experimentInstances.get(link.experimentId);
            } else {
                experimentResource = generator.createExperimentResource(model, link.experimentId);
                experimentInstances.put(link.experimentId, experimentResource);
            }
            // Get the experiment task resource
            taskUri = generator.generateExperimentTaskUri(link.experimentTaskId);
            if (taskResources.containsKey(taskUri)) {
                listOfTasks = taskResources.get(taskUri);
            } else {
                listOfTasks = new ArrayList<Resource>();
                ExperimentTaskStatus result = dao.getResultOfExperimentTask(link.experimentTaskId);
                if (result == null) {
                    LOGGER.error("Couldn't find an experiment task with the id {}. It will be ignored.",
                            link.experimentTaskId);
                    listOfTasks = null;
                } else {
                    generator.createExperimentTask(model, result, null, listOfTasks);
                    taskResources.put(taskUri, listOfTasks);
                }
            }
            // Link both resources
            if (listOfTasks != null) {
                generator.linkTasksToExperiment(model, experimentResource, listOfTasks);
            }
            ++count;
            if ((count % 1000) == 0) {
                LOGGER.info("Processed {} experiment task links.", count);
            }
        }

        File outputFile = new File(outputFileName);
        if ((outputFile.getParentFile() != null) && (!outputFile.getParentFile().exists())) {
            outputFile.getParentFile().mkdirs();
        }
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(outputFile);
            RDFDataMgr.write(fout, model, language);
        } catch (FileNotFoundException e) {
            LOGGER.error("Exception while writing model.", e);
        } finally {
            IOUtils.closeQuietly(fout);
        }
    }

    private List<ExperimentToTaskLink> loadExperiments() {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        return template.query(EXPERIMENT_IDS_QUERY, new ExperimentToTaskLinkRowMapper());
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(dao);
    }

    protected static class ExperimentToTaskLink {
        public String experimentId;
        public int experimentTaskId;

        public ExperimentToTaskLink(String experimentId, int experimentTaskId) {
            this.experimentId = experimentId;
            this.experimentTaskId = experimentTaskId;
        }
    }

    protected static class ExperimentToTaskLinkRowMapper implements RowMapper<ExperimentToTaskLink> {

        @Override
        public ExperimentToTaskLink mapRow(ResultSet arg0, int arg1) throws SQLException {
            return new ExperimentToTaskLink(arg0.getString(1), arg0.getInt(2));
        }

    }
}
