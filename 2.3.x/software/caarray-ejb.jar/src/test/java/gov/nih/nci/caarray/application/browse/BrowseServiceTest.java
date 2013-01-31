//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.browse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.dao.BrowseDao;
import gov.nih.nci.caarray.dao.stub.BrowseDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.search.BrowseCategory;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * @author Winston Cheng
 */
public class BrowseServiceTest extends AbstractCaarrayTest {
    private BrowseService browseService;
    private final LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();
    private static final int NUM_PROJECTS = 5;

    @Before
    public void setUpService() {
        BrowseServiceBean browseServiceBean = new BrowseServiceBean();
        browseServiceBean.setDaoFactory(this.daoFactoryStub);
        browseService = browseServiceBean;
    }

    @Test
    public void testBrowseCount() {
        assertEquals(1, browseService.browseCount(null, null));
    }
    @Test
    public void testBrowseList() {
        assertNotNull(browseService.browseList(null, null, null));
    }
    @Test
    public void testTabList() {
        assertNotNull(browseService.tabList(null));
    }

    @Test
    public void testCountByBrowseCategory() {
        assertEquals(NUM_PROJECTS, browseService.countByBrowseCategory(null));
    }

    @Test
    public void testHybridizationCount() {
        assertEquals(1, browseService.hybridizationCount());
    }

    @Test
    public void testInstitutionCount() {
        assertEquals(2, browseService.institutionCount());
    }

    @Test
    public void testUserCount() {
        assertEquals(3, browseService.userCount());
    }

    private static class LocalDaoFactoryStub extends DaoFactoryStub {
        LocalBrowseDaoStub browseDao;

        @Override
        public BrowseDao getBrowseDao() {
            if (this.browseDao == null) {
                this.browseDao = new LocalBrowseDaoStub();
            }
            return this.browseDao;
        }
    }
    private static class LocalBrowseDaoStub extends BrowseDaoStub {
        @Override
        public int browseCount(BrowseCategory cat, Number fieldId) {
            return 1;
        }

        @Override
        public List<Project> browseList(PageSortParams<Project> params, BrowseCategory cat, Number fieldId) {
            return Collections.emptyList();
        }

        @Override
        public int countByBrowseCategory(BrowseCategory cat) {
            return NUM_PROJECTS;
        }

        @Override
        public int hybridizationCount() {
            return 1;
        }

        @Override
        public int institutionCount() {
            return 2;
        }

        @Override
        public int userCount() {
            return 3;
        }

        @Override
        public List<Object[]> tabList(BrowseCategory cat) {
            return Collections.emptyList();
        }
    }

}
