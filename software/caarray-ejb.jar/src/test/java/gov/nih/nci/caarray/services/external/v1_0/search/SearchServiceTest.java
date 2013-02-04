//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.services.external.v1_0.search;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.dao.ContactDao;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.stub.ContactDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.ProjectDaoStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.services.external.v1_0.search.impl.SearchServiceBean;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * Unit test for the search service.
 * 
 * @author dkokotov
 */
public class SearchServiceTest extends AbstractServiceTest {
    private final LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();
    private SearchService searchService;

    private static Person PI1;
    private static Person PI2;

    @Before
    public void setUpService() {
        CaArrayUsernameHolder.setUser(STANDARD_USER);

        final SearchServiceBean searchServiceBean = new SearchServiceBean();
        searchServiceBean.setDaoFactory(this.daoFactoryStub);

        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        this.searchService = searchServiceBean;
        locatorStub.addLookup(SearchService.JNDI_NAME, this.searchService);
    }

    @BeforeClass
    public static void setUpData() {
        PI1 = new Person();
        PI1.setFirstName("John");
        PI1.setLastName("Doe");
        PI1.setEmail("john@baz.com");
        PI1.setMiddleInitials("J");

        PI2 = new Person();
        PI2.setFirstName("Jane");
        PI2.setLastName("Doe");
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetAllPrincipalInvestigators() {
        final List<gov.nih.nci.caarray.external.v1_0.experiment.Person> pis =
                this.searchService.getAllPrincipalInvestigators();
        assertEquals(2, pis.size());
        assertEquivalent(PI1, pis.get(0));
        assertEquivalent(PI2, pis.get(1));
    }

    private void assertEquivalent(Person intPerson, gov.nih.nci.caarray.external.v1_0.experiment.Person extPerson) {
        assertEquals(intPerson.getFirstName(), extPerson.getFirstName());
        assertEquals(intPerson.getLastName(), extPerson.getLastName());
        assertEquals(intPerson.getEmail(), extPerson.getEmailAddress());
        assertEquals(intPerson.getMiddleInitials(), extPerson.getMiddleInitials());
    }

    private static class LocalDaoFactoryStub extends DaoFactoryStub {

        LocalProjectDaoStub projectDao = new LocalProjectDaoStub();

        @Override
        public ProjectDao getProjectDao() {
            return this.projectDao;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SearchDao getSearchDao() {
            return new LocalSearchDaoStub(this.projectDao);
        }

        @Override
        public ContactDao getContactDao() {
            return new LocalContactDaoStub();
        }
    }

    private static class LocalProjectDaoStub extends ProjectDaoStub {

        final HashMap<Long, PersistentObject> savedObjects = new HashMap<Long, PersistentObject>();
        PersistentObject lastSaved;
        PersistentObject lastDeleted;

        @Override
        public Long save(PersistentObject caArrayObject) {
            this.lastSaved = caArrayObject;
            this.savedObjects.put(caArrayObject.getId(), caArrayObject);
            return caArrayObject.getId();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<Project> getProjectsForCurrentUser(PageSortParams<Project> pageSortParams) {
            return new ArrayList<Project>();
        }

        @Override
        public void remove(PersistentObject caArrayEntity) {
            this.lastDeleted = caArrayEntity;
            this.savedObjects.remove(caArrayEntity.getId());
        }

        public PersistentObject getLastDeleted() {
            return this.lastDeleted;
        }

    }

    private static class LocalContactDaoStub extends ContactDaoStub {
        @Override
        public List<Person> getAllPrincipalInvestigators() {
            return Arrays.asList(PI1, PI2);
        }
    }

    private static class LocalSearchDaoStub extends SearchDaoStub {
        private final LocalProjectDaoStub projectDao;

        /**
         * @param projectDao
         */
        public LocalSearchDaoStub(LocalProjectDaoStub projectDao) {
            this.projectDao = projectDao;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId) {
            final PersistentObject po = this.projectDao.savedObjects.get(entityId);
            if (po != null) {
                return (T) po;
            }
            if (Sample.class.equals(entityClass)) {
                final Sample s = getSample(entityId);
                return (T) s;
            } else if (Source.class.equals(entityClass)) {
                final Source s = getSource(entityId);
                return (T) s;
            } else if (Factor.class.equals(entityClass)) {
                final Factor f = getFactor(entityId);
                return (T) f;
            } else if (Extract.class.equals(entityClass)) {
                final Extract e = getExtract(entityId);
                return (T) e;
            } else if (Project.class.equals(entityClass)) {
                return (T) getProject(entityId);
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("deprecation")
        private Project getProject(Long id) {
            if (this.projectDao.savedObjects.containsKey(id)) {
                return (Project) this.projectDao.savedObjects.get(id);
            }
            final Project project = new Project();
            project.setId(id);
            this.projectDao.save(project);
            return project;
        }

        private Extract getExtract(Long entityId) {
            final Extract e = new Extract();
            setABM(e, entityId);
            final Sample s = getSample(entityId++);
            e.getSamples().add(s);
            s.getExtracts().add(e);
            return e;
        }

        private Sample getSample(Long entityId) {
            final Sample s = new Sample();
            setABM(s, entityId);
            final Source source = getSource(entityId++);
            s.getSources().add(source);
            source.getSamples().add(s);
            return s;
        }

        private Source getSource(Long entityId) {
            final Source s = new Source();
            setABM(s, entityId);
            return s;
        }

        @SuppressWarnings("deprecation")
        private Factor getFactor(Long entityId) {
            final Factor f = new Factor();
            f.setName("Test");
            f.setId(entityId);
            return f;
        }

        @SuppressWarnings("deprecation")
        private void setABM(AbstractBioMaterial abm, Long entityId) {
            abm.setName("Test");
            abm.setDescription("Test");
            abm.setId(entityId);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends gov.nih.nci.caarray.domain.AbstractCaArrayObject> java.util.List<T> query(T entityToMatch) {
            final List<T> results = new ArrayList<T>();
            if (entityToMatch instanceof Sample) {
                final Sample sampleToMatch = (Sample) entityToMatch;
                for (final PersistentObject po : this.projectDao.savedObjects.values()) {
                    final Project p = (Project) po;
                    if (sampleToMatch.getExperiment().getProject().getId().equals(p.getId())) {
                        for (final Sample s : p.getExperiment().getSamples()) {
                            if (sampleToMatch.getExternalId().equals(s.getExternalId())) {
                                results.add((T) s);
                            }
                        }
                    }
                }
            }
            return results;
        }
    }

}
