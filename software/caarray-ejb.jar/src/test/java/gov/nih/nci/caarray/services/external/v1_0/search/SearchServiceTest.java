/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
