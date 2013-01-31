//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao.stub;

import gov.nih.nci.caarray.dao.StateDao;
import gov.nih.nci.caarray.domain.state.State;

import java.util.Collections;
import java.util.List;

/**
 * @author Akhil Bhaskar (Amentra, Inc.)
 *
 */
public class StateDaoStub extends AbstractDaoStub implements StateDao {

    /**
     * @see gov.nih.nci.caarray.dao.StateDao#getStates()
     */
    public List<State> getStates() {
        return Collections.emptyList();
    }
}
