package gov.nih.nci.caarray.application.audit;

import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gax
 */
public class AuditLogServiceBeanTest {

    /**
     * Test of getLogs methods, of class the moke Dao.
     */
    @Test
    public void testGetLogsMocks() {
        AuditLogServiceBean instance = new AuditLogServiceBean();
        instance.setDaoLocator(new DaoFactoryStub());
        
        assertTrue(instance.getRecords(null, null).isEmpty());
        assertTrue(instance.getRecordsCount(null) == 0);
    }


}