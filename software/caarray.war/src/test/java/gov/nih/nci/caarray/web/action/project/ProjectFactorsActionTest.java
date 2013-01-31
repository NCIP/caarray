//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceStub;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.FactorValue;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.web.AbstractDownloadTest;

import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.opensymphony.xwork2.Action;

/**
 * @author Winston Cheng
 *
 */
public class ProjectFactorsActionTest extends AbstractDownloadTest {
    private final ProjectFactorsAction action = new ProjectFactorsAction();
    private static final Factor DUMMY_FACTOR = new Factor();

    @SuppressWarnings("deprecation")
    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
        locatorStub.addLookup(GenericDataService.JNDI_NAME, new LocalGenericDataService());
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, new ProjectManagementServiceStub());
        DUMMY_FACTOR.setId(1L);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testPrepare() throws Exception {
        // no current factor id
        action.prepare();
        assertNull(action.getCurrentFactor().getId());

        // valid current factor id
        Factor factor = new Factor();
        factor.setId(1L);
        action.setCurrentFactor(factor);
        action.prepare();
        assertEquals(DUMMY_FACTOR, action.getCurrentFactor());

        // invalid current factor id
        factor = new Factor();
        factor.setId(2L);
        action.setCurrentFactor(factor);
        try {
            action.prepare();
            fail("Expected PermissionDeniedException");
        } catch (PermissionDeniedException pde) {}
    }

    @Test
    public void testView() {
        assertEquals(Action.INPUT, action.view());
        assertFalse(action.isEditMode());
        assertEquals(10, action.getCategories().size());
    }

    @Test
    public void testEdit() {
        assertEquals(Action.INPUT, action.edit());
        assertTrue(action.isEditMode());
        assertEquals(10, action.getCategories().size());
    }

    @Test
    public void testValidate() {
        action.validate();
    }

    @Test
    public void testCopy() throws Exception {
        action.setCurrentFactor(DUMMY_FACTOR);
        assertEquals("list", action.copy());
    }

    @Test
    public void testDelete() throws Exception {
        Factor f = new Factor();
        Hybridization h = new Hybridization();
        FactorValue fv = new FactorValue();
        fv.setFactor(f);
        fv.setHybridization(h);
        fv.setValue("Foo");
        f.getFactorValues().add(fv);
        h.getFactorValues().add(fv);
        action.setCurrentFactor(f);
        String result = action.delete();
        assertTrue(h.getFactorValues().isEmpty());
        assertEquals("list", result);
    }

    private static class LocalGenericDataService extends GenericDataServiceStub {
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
            if (entityClass.equals(Factor.class) && entityId.equals(1L)) {
                return (T)DUMMY_FACTOR;
            }
            return null;
        }
    }
}
