//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.application.fileaccess;

import gov.nih.nci.caarray.application.AbstractServiceIntegrationTest;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.util.HibernateUtil;
import java.io.File;
import org.hibernate.Transaction;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gax
 */
public class TemporaryFileCacheImplTest extends AbstractServiceIntegrationTest {

    /**
     * Test GF23736 regression.
     */
    @Test
    public void testCloseFiles() throws Exception {
        CaArrayFile caArrayFile = new CaArrayFile();
        caArrayFile.setName("foo");
        caArrayFile.setFileStatus(FileStatus.UPLOADED);
        caArrayFile.writeContents(TemporaryFileCacheImplTest.class.getProtectionDomain().getCodeSource().getLocation().openStream());
        Transaction tx = HibernateUtil.beginTransaction();
        HibernateUtil.getCurrentSession().save(caArrayFile);
        tx.commit();
        tx = HibernateUtil.beginTransaction();
        TemporaryFileCacheImpl instance = new TemporaryFileCacheImpl();
        File result = instance.getFile(caArrayFile);

        instance.closeFiles();
        assertFalse(result.exists());
        tx.commit();
    }
    
}
