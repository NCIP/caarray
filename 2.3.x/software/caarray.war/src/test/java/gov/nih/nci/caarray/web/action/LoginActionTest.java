//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.browse.BrowseService;
import gov.nih.nci.caarray.application.browse.BrowseServiceStub;
import gov.nih.nci.caarray.domain.search.BrowseCategory;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.web.action.HomeAction.BrowseItems;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

/**
 * @author Winston Cheng
 *
 */
@SuppressWarnings("PMD")
public class LoginActionTest extends AbstractCaarrayTest {
    private final HomeAction loginAction = new HomeAction();
    private final LocalBrowseServiceStub projectServiceStub = new LocalBrowseServiceStub();

    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(BrowseService.JNDI_NAME, this.projectServiceStub);
    }

    @Test
    public void testExecute() throws Exception {
        String result = this.loginAction.execute();
        List<BrowseItems> browseItems = loginAction.getBrowseItems();
        assertEquals(BrowseCategory.values().length+2, browseItems.size());
        assertEquals(Action.INPUT, result);
    }
    private static class LocalBrowseServiceStub extends BrowseServiceStub { }
}
