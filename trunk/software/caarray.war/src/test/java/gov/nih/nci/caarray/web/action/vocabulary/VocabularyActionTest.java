//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.vocabulary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;

import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import com.opensymphony.xwork2.Action;

/**
 * @author Scott Miller
 *
 */
public class VocabularyActionTest extends AbstractCaarrayTest {
    private static final String START_WITH_EDIT = "startWithEdit";
    private static final String RETURN_PROJECT_ID = "returnProjectId";
    private static final String RETURN_INITIAL_TAB1 = "returnInitialTab1";
    private static final String RETURN_INITIAL_TAB2 = "returnInitialTab2";
    private static final String RETURN_INITIAL_TAB2_URL = "returnInitialTab2Url";

    private static VocabularyService vocabularyService;
    VocabularyAction action;

    @BeforeClass
    @SuppressWarnings("PMD")
    public static void beforeClass() {
        vocabularyService = new VocabularyServiceStub() {

            /**
             * {@inheritDoc}
             */
            @Override
            public Term getTerm(Long id) {
                if (id.equals(1L))  {
                    Term t = super.getTerm(id);
                    t.setValue("test term");
                    return t;
                } else if (id.equals(2L)) {
                    return null;
                }
                return super.getTerm(id);
            }

        };
        ServiceLocatorStub stub = ServiceLocatorStub.registerEmptyLocator();
        stub.addLookup(VocabularyService.JNDI_NAME, vocabularyService);
    }

    @Before
    public void before() {
        this.action = new VocabularyAction();
        ServletActionContext.setRequest(new MockHttpServletRequest());
    }

    @Test(expected = PermissionDeniedException.class)
    @SuppressWarnings("deprecation")
    public void testPrepare() {
        this.action.setCategory(ExperimentOntologyCategory.ORGANISM_PART);
        this.action.prepare();
        assertEquals(null, this.action.getCurrentTerm());
        this.action.setCurrentTerm(new Term());
        this.action.getCurrentTerm().setId(1l);
        this.action.prepare();
        assertEquals("test term", this.action.getCurrentTerm().getValue());
        this.action.getCurrentTerm().setId(2l);
        this.action.prepare();
    }

    @Test
    public void testList() {
        this.action.setCategory(ExperimentOntologyCategory.ORGANISM_PART);
        assertEquals(Action.SUCCESS, this.action.list());

        MockHttpSession session = new MockHttpSession ();
        session.setAttribute(START_WITH_EDIT, "true");
        session.setAttribute(RETURN_PROJECT_ID, "1");
        session.setAttribute(RETURN_INITIAL_TAB1, RETURN_INITIAL_TAB1);
        session.setAttribute(RETURN_INITIAL_TAB2, RETURN_INITIAL_TAB2);
        session.setAttribute(RETURN_INITIAL_TAB2_URL, RETURN_INITIAL_TAB2_URL);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        ServletActionContext.setRequest(request);

        assertEquals(Action.INPUT, this.action.list());
        assertTrue(this.action.isEditMode());
        assertEquals(1l, this.action.getReturnProjectId().longValue());
        assertEquals(RETURN_INITIAL_TAB1, this.action.getReturnInitialTab1());
        assertEquals(RETURN_INITIAL_TAB2, this.action.getReturnInitialTab2());
        assertEquals(RETURN_INITIAL_TAB2_URL, this.action.getReturnInitialTab2Url());
        assertNull(session.getAttribute(START_WITH_EDIT));
        assertNull(session.getAttribute(RETURN_PROJECT_ID));
        assertNull(session.getAttribute(RETURN_INITIAL_TAB1));
        assertNull(session.getAttribute(RETURN_INITIAL_TAB2));
        assertNull(session.getAttribute(RETURN_INITIAL_TAB2_URL));
        assertTrue(this.action.isReturnToProjectOnCompletion());
    }

    @Test
    public void testSearchForTerms() {
        this.action.setCategory(ExperimentOntologyCategory.ORGANISM_PART);
        this.action.setCurrentTerm(new Term());
        assertEquals("termAutoCompleterValues", this.action.searchForTerms());
    }

    @Test
    public void testDetails() {
        assertEquals(Action.INPUT, this.action.details());
        assertFalse(this.action.isEditMode());
    }

    @Test
    public void testProjectEdit() {
        assertEquals("projectEdit", this.action.projectEdit());
    }

    @Test
    public void testSave() {
        this.action.setCategory(ExperimentOntologyCategory.ORGANISM_PART);
        this.action.setCurrentTerm(new Term());
        assertEquals(Action.SUCCESS, this.action.save());

        this.action.setCreateNewSource(true);
        this.action.setReturnToProjectOnCompletion(true);
        assertEquals("projectEdit", this.action.save());
    }
}
