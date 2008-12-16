package gov.nih.nci.caarray.web;

import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;

import org.junit.Before;

public abstract class AbstractDownloadTest extends AbstractCaarrayTest {

     @Before
     public void preTest() throws Exception {
         TemporaryFileCacheLocator.resetTemporaryFileCache();
     }
}
