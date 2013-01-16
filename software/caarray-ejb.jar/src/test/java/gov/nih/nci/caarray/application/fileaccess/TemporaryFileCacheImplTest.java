//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.fileaccess;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.AbstractServiceIntegrationTest;

import java.io.File;

import org.junit.Test;

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
        final TemporaryFileCacheImpl instance = new TemporaryFileCacheImpl();
        final File result = instance.createFile("foo");
        assertTrue(result.exists());
        instance.delete("foo");
        assertFalse(result.exists());
    }

}
