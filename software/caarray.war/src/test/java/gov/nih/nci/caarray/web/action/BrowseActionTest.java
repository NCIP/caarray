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
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.search.BrowseCategory;
import gov.nih.nci.caarray.domain.search.ProjectSortCriterion;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.web.ui.BrowseTab;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.fiveamsolutions.nci.commons.web.displaytag.SortablePaginatedList;
import com.opensymphony.xwork2.Action;

/**
 * @author Winston Cheng
 *
 */
public class BrowseActionTest extends AbstractCaarrayTest {
    private final BrowseAction browseAction = new BrowseAction();
    private final LocalBrowseServiceStub projectServiceStub = new LocalBrowseServiceStub();
    private static final int NUM_TABS = 3;
    private static final int NUM_PROJECTS = 5;

    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(BrowseService.JNDI_NAME, this.projectServiceStub);
    }

    @Test
    public void testExecute() throws Exception {
        String result = this.browseAction.execute();
        SortedSet<BrowseTab> tabs = browseAction.getTabs();
        BrowseTab lastTab = tabs.last();
        assertEquals(NUM_TABS, tabs.size());
        assertEquals("Tab"+NUM_TABS,lastTab.getName());
        assertEquals(NUM_TABS,lastTab.getId().intValue());
        assertEquals(NUM_TABS,lastTab.getCount());
        assertEquals(Action.SUCCESS, result);
    }

    @Test
    public void testList() throws Exception {
        this.browseAction.setCategory(BrowseCategory.ORGANISMS);
        this.browseAction.setId(1L);
        String result = this.browseAction.list();
        SortablePaginatedList<Project, ProjectSortCriterion> asdf = browseAction.getResults();
        assertEquals(NUM_PROJECTS, asdf.getList().size());
        assertEquals("tab", result);
    }
    private static class LocalBrowseServiceStub extends BrowseServiceStub {
        @Override
        public List<Object[]> tabList(BrowseCategory cat) {
            List<Object[]> tabs = new ArrayList<Object[]>();
            for (int i=1; i<=NUM_TABS; i++) {
                Object[] tab = new Object[3];
                tab[0] = "Tab"+i;
                tab[1] = i;
                tab[2] = i;
                tabs.add(tab);
            }
            return tabs;
        }
        @Override
        public int browseCount(BrowseCategory cat, Number fieldId) {
            return NUM_PROJECTS;
        }
        @Override
        public List<Project> browseList(PageSortParams<Project> params, BrowseCategory cat, Number fieldId) {
            List<Project> projects = new ArrayList<Project>();
            if (cat == BrowseCategory.ORGANISMS && fieldId.equals(1L)) {
                for (int i=0; i<NUM_PROJECTS; i++) {
                    projects.add(new Project());
                }
            }
            return projects;
        }
    }
}
