//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.action.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.domain.publication.Publication;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.web.AbstractBaseStrutsTest;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * @author Winston Cheng
 *
 */
public class ProjectPublicationsActionTest extends AbstractBaseStrutsTest {
    private final ProjectPublicationsAction action = new ProjectPublicationsAction();
    private static Publication DUMMY_PUBLICATION = new Publication();

    @SuppressWarnings("deprecation")
    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(GenericDataService.JNDI_NAME, new LocalGenericDataService());
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
        DUMMY_PUBLICATION.setId(1L);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testPrepare() throws Exception {
        action.setPublicationTypes(null);
        action.setPublicationStatuses(null);

        // no current publication id
        action.prepare();
        assertNull(action.getCurrentPublication().getId());
        assertEquals(10, action.getPublicationTypes().size());
        assertEquals(10, action.getPublicationStatuses().size());

        // valid current publication id
        Publication publication = new Publication();
        publication.setId(1L);
        action.setCurrentPublication(publication);
        action.prepare();
        assertEquals(DUMMY_PUBLICATION, action.getCurrentPublication());

        // invalid current publication id
        publication = new Publication();
        publication.setId(2L);
        action.setCurrentPublication(publication);
        try {
            action.prepare();
            fail("Expected PermissionDeniedException");
        } catch (PermissionDeniedException pde) {}
    }

    @Test(expected=NotImplementedException.class)
    public void testCopy() {
        action.copy();
    }

    private static class LocalGenericDataService extends GenericDataServiceStub {
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
            if (entityClass.equals(Publication.class) && entityId.equals(1L)) {
                return (T)DUMMY_PUBLICATION;
            }
            return null;
        }
    }
}
