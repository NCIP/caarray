package gov.nih.nci.caarray.application.audit;

import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;

import org.junit.Test;

/**
 * 
 * @author gax
 */
public class AuditLogServiceBeanTest extends AbstractServiceTest {

    /**
     * Test of getLogs methods, of class the moke Dao.
     */
    @Test
    public void testGetLogsMocks() {
        final AuditLogServiceBean instance = new AuditLogServiceBean();
        instance.setAuditLogDao(new DaoFactoryStub().getAuditLogDao());

        assertTrue(instance.getRecords(null, null).isEmpty());
        assertTrue(instance.getRecordsCount(null) == 0);
    }

}
