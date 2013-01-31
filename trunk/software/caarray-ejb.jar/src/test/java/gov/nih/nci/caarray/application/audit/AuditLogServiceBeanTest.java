//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.audit;

import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import org.junit.Test;
import static org.junit.Assert.*;

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
        AuditLogServiceBean instance = new AuditLogServiceBean();
        instance.setDaoLocator(new DaoFactoryStub());
        
        assertTrue(instance.getRecords(null, null).isEmpty());
        assertTrue(instance.getRecordsCount(null) == 0);
    }


}
