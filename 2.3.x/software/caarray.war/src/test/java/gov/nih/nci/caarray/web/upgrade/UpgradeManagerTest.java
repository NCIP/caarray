//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.upgrade;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.application.UserTransactionStub;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceStub;
import gov.nih.nci.caarray.domain.ConfigParamEnum;
import gov.nih.nci.caarray.util.ConfigurationHelper;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;

import org.apache.commons.configuration.DataConfiguration;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Winston Cheng
 *
 */
public class UpgradeManagerTest extends AbstractCaarrayTest {

    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(ArrayDesignService.JNDI_NAME, new ArrayDesignServiceStub());
        locatorStub.addLookup(GenericDataService.JNDI_NAME, new GenericDataServiceStub());
        locatorStub.addLookup("java:comp/UserTransaction", new UserTransactionStub());
    }

    @Test
    public void testPerformUpgrades() {
        DataConfiguration config = ConfigurationHelper.getConfiguration();
        String currentVersion = config.getString(ConfigParamEnum.SCHEMA_VERSION.name());
        config.setProperty(ConfigParamEnum.SCHEMA_VERSION.name(), "test1");

        // perform upgrades (test1->test2->test3)
        try {
            UpgradeManager.getInstance().performUpgrades();
            assertEquals("test3", config.getString(ConfigParamEnum.SCHEMA_VERSION.name()));            
        } finally {
            // revert version back
            config.setProperty(ConfigParamEnum.SCHEMA_VERSION.name(), currentVersion);            
        }
    }
}
