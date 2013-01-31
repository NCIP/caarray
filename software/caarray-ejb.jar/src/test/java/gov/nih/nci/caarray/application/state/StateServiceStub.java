//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.state;

import gov.nih.nci.caarray.domain.state.State;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Winston Cheng
 *
 */
public class StateServiceStub implements StateService {

    public List<State> getStates() {
        List<State> states = new ArrayList<State>();
        for (int i=0; i<50; i++) {
            states.add(new State());
        }
        return states;
    }

}
