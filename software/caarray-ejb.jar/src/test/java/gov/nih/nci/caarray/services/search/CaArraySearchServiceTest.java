//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class CaArraySearchServiceTest extends AbstractCaarrayTest {

    private CaArraySearchService searchService;
    private final LocalDaoFactoryStub caArrayDaoFactoryStub = new LocalDaoFactoryStub();

    @Before
    public void setUp() throws Exception {
        CaArraySearchServiceBean searchServiceBean = new CaArraySearchServiceBean();
        searchServiceBean.setDaoFactory(caArrayDaoFactoryStub);
        searchService = searchServiceBean;
    }

    @Test
    public void testSearch() {
        Project exampleProject = null;
        List<Project> projects = searchService.search(exampleProject);
        assertTrue(projects.isEmpty());
        exampleProject = new Project();
        projects = searchService.search(exampleProject);
        assertEquals(1, projects.size());
        assertEquals(exampleProject, projects.get(0));
    }

    @Test
    public void testSearchCQLQuery() {
        CQLQuery query = null;
        List results = searchService.search(query);
        assertTrue(results.isEmpty());
        query = new CQLQuery();
        results = searchService.search(query);
        assertFalse(results.isEmpty());
    }

    private static class LocalDaoFactoryStub extends DaoFactoryStub {

        @Override
        public SearchDao getSearchDao() {
            return new SearchDaoStub() {

                @Override
                public <T extends AbstractCaArrayObject> List<T> query(T entityToMatch) {
                    ArrayList<T> list = new ArrayList<T>();
                    list.add(entityToMatch);
                    return list;
                }

                @Override
                public List<AbstractCaArrayObject> query(CQLQuery cqlQuery) {
                    ArrayList<AbstractCaArrayObject> list = new ArrayList<AbstractCaArrayObject>();
                    list.add(new Project());
                    return list;
                }

            };
        }
    }
}
