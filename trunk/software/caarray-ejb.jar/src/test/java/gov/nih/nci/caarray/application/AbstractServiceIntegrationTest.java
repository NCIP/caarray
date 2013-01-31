//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application;

import gov.nih.nci.caarray.AbstractHibernateTest;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;

import org.junit.Before;

/**
 * Base class for EJB service integration tests (e.g. tests that test EJB beans with real DAO implementations
 * backed by the database.
 * 
 * @author dkokotov
 */
public abstract class AbstractServiceIntegrationTest extends AbstractHibernateTest {
    public AbstractServiceIntegrationTest() {
        super(false);
    }
    
    @Before
    public void resetTemporaryFileCache() {
        TemporaryFileCacheLocator.resetTemporaryFileCache();
    }
}
