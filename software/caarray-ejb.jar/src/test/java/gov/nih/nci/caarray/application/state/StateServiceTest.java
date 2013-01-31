//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.state;

import static org.junit.Assert.assertNotNull;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.domain.state.State;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Winston Cheng
 *
 */
public class StateServiceTest extends AbstractCaarrayTest {
    private StateService stateService;
    private final DaoFactoryStub daoFactoryStub = new DaoFactoryStub();

    @Before
    public void setUp() {
        StateServiceBean stateServiceBean = new StateServiceBean();
        stateServiceBean.setDaoFactory(this.daoFactoryStub);
        this.stateService = stateServiceBean;
    }

    @Test
    public void testGetStates() {
        List<State> states = this.stateService.getStates();
        assertNotNull(states);
    }
}
