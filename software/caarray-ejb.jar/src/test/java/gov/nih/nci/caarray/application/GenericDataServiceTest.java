//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.ProjectDaoStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.ProposalStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Class to test the generic service.
 *
 * @author Scott Miller
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class GenericDataServiceTest extends AbstractCaarrayTest {

    GenericDataService service = null;

    private final LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();

    @Before
    public void setUpService() {
        GenericDataServiceBean serviceBean = new GenericDataServiceBean();
        serviceBean.setDaoFactory(this.daoFactoryStub);
        this.service = serviceBean;
        ((SearchDaoStub)this.daoFactoryStub.getSearchDao()).reset();
    }

    @Test
    public void testRetrieveProject() {
        Object obj = this.service.getPersistentObject(Project.class, 999l);
        assertEquals(null, obj);

        obj = this.service.getPersistentObject(Project.class, 1l);

        if(((Project)obj).getStatus().equals(ProposalStatus.DRAFT))
        {
            assertEquals(SecurityLevel.NO_VISIBILITY, ((Project) obj).getPublicProfile().getSecurityLevel());
        }
        else
        {
            assertEquals(SecurityLevel.VISIBLE, ((Project) obj).getPublicProfile().getSecurityLevel());
        }

    }
    
    @Test
    public void testRetrieveByIds() throws IllegalAccessException, InstantiationException {
        List<Project> lst = this.service.retrieveByIds(Project.class, Collections.singletonList(1L));
        assertEquals(1, lst.size());

        lst = this.service.retrieveByIds(Project.class, Collections.singletonList(9L));
        assertEquals(0, lst.size());
    }
    

    @Test
    public void testGetIncrementingCopyName() {
        String copyName = this.service.getIncrementingCopyName(Project.class, "name", "Name");
        assertEquals("Name5", copyName);
        copyName = this.service.getIncrementingCopyName(Project.class, "name", "Name3");
        assertEquals("Name5", copyName);
        copyName = this.service.getIncrementingCopyName(Project.class, "name", "Namonce");
        assertEquals("Namonce2", copyName);
    }

    @Test
    public void testDelete() {
        Project p = new Project();
        this.service.delete(p);
        assertEquals(p, ((LocalProjectDaoStub) this.daoFactoryStub.getProjectDao()).deletedObject);
    }

    @Test
    public void testFilterCollection() {
        this.service.filterCollection(null, null, null);
        assertEquals(1, ((SearchDaoStub)this.daoFactoryStub.getSearchDao()).getCallsToFiltercollection());
    }

    private static class LocalDaoFactoryStub extends DaoFactoryStub {

        LocalSearchDaoStub searchDao;
        LocalProjectDaoStub projectDao;

        /**
         * {@inheritDoc}
         */
        @Override
        public ProjectDao getProjectDao() {
            if (this.projectDao == null) {
                this.projectDao = new LocalProjectDaoStub();
            }

            return this.projectDao;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SearchDao getSearchDao() {
            if (this.searchDao == null) {
                this.searchDao = new LocalSearchDaoStub();
            }
            return this.searchDao;
        }
    }

    private static class LocalProjectDaoStub extends ProjectDaoStub {

        private PersistentObject deletedObject;

        /**
         * {@inheritDoc}
         */
        @Override
        public void remove(PersistentObject caArrayEntity) {
            this.deletedObject = caArrayEntity;
        }


        /**
         * @return last 'deleted' object, if any
         */
        public PersistentObject getDeletedObject() {
            return this.deletedObject;
        }

    }

    private static class LocalSearchDaoStub extends SearchDaoStub {

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId) {
            if (Project.class.equals(entityClass) && Long.valueOf(1L).equals(entityId)) {
                return (T) new Project();
            }
            return null;
        }
        
        @Override
        public <T extends PersistentObject> List<T> retrieveByIds(Class<T> entityClass, List<? extends Serializable> ids) {
            List<T> list = new ArrayList<T>();
            if (Project.class.equals(entityClass)) {
                if (ids.contains(1L)) {
                    try {
                        list.add(entityClass.newInstance());
                    } catch (InstantiationException e) { // NOPMD
                        // nothing to do - test will fail 
                    } catch (IllegalAccessException e) { // NOPMD
                        // nothing to do - test will fail
                    }
                }
                return list;
            }
            return new ArrayList<T>();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<String> findValuesWithSamePrefix(Class<?> entityClass, String fieldName, String prefix) {
            if (Project.class.equals(entityClass) && "name".equals(fieldName)) {
                if (StringUtils.isEmpty(prefix)) {
                    return Arrays.asList("Name1", "Name4", "Namonce21t");
                } else if ("Name".equals(prefix)) {
                    return Arrays.asList("Name1", "Name4");
                } else if ("Namonce".equals(prefix)) {
                    return Arrays.asList("Namonce21t");
                } else {
                    return new ArrayList<String>();
                }
            } else {
                return new ArrayList<String>();
            }
        }
    }
}
