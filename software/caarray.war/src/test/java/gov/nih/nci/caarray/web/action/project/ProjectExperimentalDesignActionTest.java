//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Winston Cheng
 *
 */
public class ProjectExperimentalDesignActionTest extends AbstractCaarrayTest {
    private final ProjectExperimentalDesignAction action = new ProjectExperimentalDesignAction();

    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
    }

    @Test
    public void testPrepare() throws Exception {
        Set<Term> terms = new HashSet<Term>();
        action.setExperimentDesignTypes(terms);
        action.setReplicateTypes(terms);
        action.setQualityControlTypes(terms);
        action.prepare();
        assertEquals(10, action.getExperimentDesignTypes().size());
        assertEquals(10, action.getReplicateTypes().size());
        assertEquals(10, action.getQualityControlTypes().size());
    }
}
