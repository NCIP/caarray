//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;

import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.opensymphony.xwork2.Action;

/**
 * @author Scott Miller
 *
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class ProtocolManagementActionTest extends AbstractCaarrayTest {

    private static GenericDataService genericDataService;
    private static VocabularyService vocabularyService;

    ProtocolManagementAction action;

    @BeforeClass
    @SuppressWarnings("PMD")
    public static void beforeClass() {
        genericDataService = new GenericDataServiceStub() {

            /**
             * {@inheritDoc}
             */
            @Override
            @SuppressWarnings({"unchecked", "deprecation"})
            public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
                if (entityClass.equals(Protocol.class) && entityId.equals(new Long(1))) {
                    Protocol p = new Protocol();
                    p.setId(1l);
                    p.setName("test protocol");
                    return (T) p;
                }
                return super.getPersistentObject(entityClass, entityId);
            }

        };

        vocabularyService = new VocabularyServiceStub();
        ServiceLocatorStub stub = ServiceLocatorStub.registerEmptyLocator();
        stub.addLookup(GenericDataService.JNDI_NAME, genericDataService);
        stub.addLookup(VocabularyService.JNDI_NAME, vocabularyService);
    }

    @Before
    public void before() {
        this.action = new ProtocolManagementAction();
        ServletActionContext.setRequest(new MockHttpServletRequest());
    }

    @Test(expected = PermissionDeniedException.class)
    @SuppressWarnings("deprecation")
    public void testPrepare() {
        this.action.prepare();
        assertEquals(null, this.action.getProtocol());
        this.action.setProtocol(new Protocol());
        this.action.prepare();
        assertEquals(null, this.action.getProtocol().getId());
        this.action.setCreateNewSource(true);
        this.action.setNewSource(new TermSource());
        this.action.prepare();
        assertEquals(null, this.action.getProtocol().getId());
        
        this.action.setCreateNewSource(false);
        this.action.getProtocol().setId(1l);
        this.action.prepare();
        assertEquals("test protocol", this.action.getProtocol().getName());
        assertNull(this.action.getProtocol().getSource());

        this.action.setCreateNewSource(true);
        this.action.setNewSource(new TermSource());
        this.action.prepare();
        assertEquals("test protocol", this.action.getProtocol().getName());
        assertNotNull(this.action.getProtocol().getSource());

        this.action.getProtocol().setId(2l);
        this.action.prepare();        
    }

    @Test
    public void testManage() {
        assertEquals(Action.SUCCESS, this.action.manage());
        assertEquals(null, ServletActionContext.getRequest().getSession().getAttribute("returnProjectId"));
    }

    @Test
    public void testStartOnEditPage() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("startWithEdit", "true");
        ServletActionContext.setRequest(mockRequest);
        this.action.setReturnInitialTab1("test1");
        this.action.setReturnInitialTab2("test2");
        this.action.setReturnInitialTab2Url("test2Url");
        this.action.setReturnProjectId(1L);
        this.action.setReturnInitialTab1("test1");

        assertEquals(Action.SUCCESS, this.action.manage());
        assertEquals(1L, ServletActionContext.getRequest().getSession().getAttribute("returnProjectId"));
        assertEquals("test1", ServletActionContext.getRequest().getSession().getAttribute("returnInitialTab1"));
        assertEquals("test2", ServletActionContext.getRequest().getSession().getAttribute("returnInitialTab2"));
        assertEquals("test2Url", ServletActionContext.getRequest().getSession().getAttribute("returnInitialTab2Url"));

        mockRequest.removeParameter("startWithEdit");
        this.action = new ProtocolManagementAction();
        assertEquals(Action.INPUT, this.action.list());
        assertTrue(this.action.isEditMode());
        assertEquals(null, ServletActionContext.getRequest().getSession().getAttribute("returnProjectId"));
        assertEquals(null, ServletActionContext.getRequest().getSession().getAttribute("returnInitialTab1"));
        assertEquals(null, ServletActionContext.getRequest().getSession().getAttribute("returnInitialTab2"));
        assertEquals(null, ServletActionContext.getRequest().getSession().getAttribute("returnInitialTab2Url"));

        assertEquals(1L, this.action.getReturnProjectId());
        assertEquals("test1", this.action.getReturnInitialTab1());
        assertEquals("test2", this.action.getReturnInitialTab2());
        assertEquals("test2Url", this.action.getReturnInitialTab2Url());
    }

    @Test
    public void testList() throws Exception {
        assertEquals(Action.SUCCESS, this.action.list());
        assertEquals(0, this.action.getProtocols().size());
    }

    @Test
    public void testEditAndDetailsAndProjectEdit() {
        this.action.setEditMode(false);
        assertEquals(Action.INPUT, this.action.edit());
        assertTrue(this.action.isEditMode());
        assertEquals(Action.INPUT, this.action.details());
        assertFalse(this.action.isEditMode());

        assertEquals("projectEdit", this.action.projectEdit());
    }
}
