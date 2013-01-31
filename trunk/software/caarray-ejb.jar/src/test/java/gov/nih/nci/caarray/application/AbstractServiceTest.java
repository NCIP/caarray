//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application;

import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;

import org.junit.Before;

/**
 * Base class for EJB service unit tests, which test EJB beans with DAO dependencies stubbed out.
 * 
 * @author dkokotov
 */
public abstract class AbstractServiceTest extends AbstractCaarrayTest {
    @Before
    public void resetTemporaryFileCache() {
        TemporaryFileCacheLocator.resetTemporaryFileCache();
    }
}
