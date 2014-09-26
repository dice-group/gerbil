package org.aksw.gerbil.database;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author b.eickmann
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "file:src/main/resources/spring/database/database-context.xml"
})
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ExperimentDAOImplJUnitTest {

    @Autowired
    @Qualifier("experimentDAO")
    private ExperimentDAO dao;
    
    @Test
    public void test(){
        int taskId = this.dao.createTask("Der_GUTE_BERND", "test_dataset", "QUATSCH", "matsch", "id-23456");
        assertTrue(taskId == 2);
    }
    
}
