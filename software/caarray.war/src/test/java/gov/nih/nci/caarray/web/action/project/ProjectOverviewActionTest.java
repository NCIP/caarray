//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceStub;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceStub;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.web.AbstractBaseStrutsTest;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.opensymphony.xwork2.Action;

/**
 * @author Winston Cheng
 *
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class ProjectOverviewActionTest extends AbstractBaseStrutsTest {
    private final ProjectOverviewAction action = new ProjectOverviewAction();
    private static final LocalArrayDesignServiceStub arrayDesignServiceStub = new LocalArrayDesignServiceStub();
    private static final LocalGenericDataServiceStub genericDataServiceStub = new LocalGenericDataServiceStub();
    private static final ProjectManagementServiceStub projectManagementServiceStub = new ProjectManagementServiceStub();
    private static final VocabularyServiceStub vocabularyServiceStub = new VocabularyServiceStub();

    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(ArrayDesignService.JNDI_NAME, arrayDesignServiceStub);
        locatorStub.addLookup(GenericDataService.JNDI_NAME, genericDataServiceStub);
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, projectManagementServiceStub);
        locatorStub.addLookup(VocabularyService.JNDI_NAME, vocabularyServiceStub);
    }

    @Test
    public void testPrepare() throws Exception {
        // no manufacturer
        setProjectId(1L);
        this.action.prepare();
        assertEquals(vocabularyServiceStub.getOrganisms().size(), this.action.getOrganisms().size());
        assertEquals(arrayDesignServiceStub.getArrayDesignProviders().size(), this.action.getManufacturers().size());
        assertEquals(0, this.action.getArrayDesigns().size());
        // project with manufacturer
        setProjectId(2L);
        this.action.prepare();
        assertEquals(vocabularyServiceStub.getOrganisms().size(), this.action.getOrganisms().size());
        assertEquals(arrayDesignServiceStub.getArrayDesignProviders().size(), this.action.getManufacturers().size());
        assertEquals(1, this.action.getArrayDesigns().size());
    }


    @Test
    public void testLoad() throws Exception {
        setProjectId(1L);
        assertEquals(Action.INPUT, this.action.load());
        assertEquals(projectManagementServiceStub.getTissueSitesForExperiment(null).size(), this.action.getTissueSites().size());
        assertEquals(projectManagementServiceStub.getDiseaseStatesForExperiment(null).size(), this.action.getDiseaseState().size());
        assertEquals(projectManagementServiceStub.getMaterialTypesForExperiment(null).size(), this.action.getMaterialTypes().size());
        assertEquals(projectManagementServiceStub.getCellTypesForExperiment(null).size(), this.action.getCellTypes().size());
    }

    @Test
    public void testRetrieveArrayDesigns() throws Exception {
        this.action.setManufacturerId(1L);
        SortedSet <AssayType>assayTypes = new TreeSet<AssayType>();
        assayTypes.add(new AssayType("Gene Expression"));
        this.action.setAssayTypeValues(assayTypes);
        assertEquals("xmlArrayDesigns", this.action.retrieveArrayDesigns());
        assertEquals(1, this.action.getArrayDesigns().size());
    }

    @SuppressWarnings("deprecation")
    private void setProjectId(Long id) {
        Project proj = new Project() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean hasReadPermission(User user) {
                return (this.getId() == 1l);
            }

            @Override
            public boolean hasWritePermission(User user) {
                return (this.getId() == 1l);
            }

        };
        proj.setId(id);
        proj.getExperiment().setId(id);
        this.action.setProject(proj);
    }

    @SuppressWarnings("deprecation")
    private static class LocalArrayDesignServiceStub extends ArrayDesignServiceStub {
        @Override
        public List<Organization> getAllProviders() {
            List<Organization> providers = new ArrayList<Organization>();
            Organization p1 = new Organization();
            p1.setId(1L);
            providers.add(p1);
            return providers;
        }
        @Override
        public List<Organization> getArrayDesignProviders() {
            List<Organization> providers = new ArrayList<Organization>();
            Organization p1 = new Organization();
            p1.setId(1L);
            providers.add(p1);
            return providers;
        }
        @Override
        public List<ArrayDesign> getImportedArrayDesignsForProvider(Organization provider) {
            if (provider != null && Long.valueOf(1L).equals(provider.getId())) {
                List<ArrayDesign> designs = new ArrayList<ArrayDesign>();
                ArrayDesign d1 = new ArrayDesign();
                d1.setId(1L);
                designs.add(d1);
                return designs;
            }
            return null;
        }
        @Override
        public List<ArrayDesign> getImportedArrayDesigns(Organization provider, Set<AssayType> assayTypes) {
            AssayType DUMMY_ASSAYTYPE_1 = new AssayType("Gene Expression");
            if (provider != null && Long.valueOf(1L).equals(provider.getId()) &&
                    assayTypes.contains(DUMMY_ASSAYTYPE_1)) {
                List<ArrayDesign> designs = new ArrayList<ArrayDesign>();
                ArrayDesign d1 = new ArrayDesign();
                d1.setId(1L);
                SortedSet <AssayType>dummyAssayTypes = new TreeSet<AssayType>();
                dummyAssayTypes.add(DUMMY_ASSAYTYPE_1);
                d1.setAssayTypes(dummyAssayTypes);
                designs.add(d1);
                return designs;
            }
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    private static class LocalGenericDataServiceStub extends GenericDataServiceStub {
        @Override
        @SuppressWarnings("unchecked")
        public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
            if (Organization.class.equals(entityClass) && Long.valueOf(1L).equals(entityId)) {
                Organization o = new Organization();
                o.setId(1L);
                return (T) o;
            }
            if (Project.class.equals(entityClass)) {
                Project p = new Project();
                p.setId(entityId);
                p.getExperiment().setId(1L);
                if (2L == entityId) {
                    Organization o = new Organization();
                    o.setId(1L);
                    p.getExperiment().setManufacturer(o);
                    SortedSet <AssayType>assayTypes = new TreeSet<AssayType>();
                    assayTypes.add(new AssayType("Gene Expression"));
                    p.getExperiment().setAssayTypes(assayTypes);
                }
                return (T) p;                
            }
            
            return null;
        }
    }
}
