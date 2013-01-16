//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.state;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @author Winston Cheng
 *
 */
public class StateTest {
    @Test
    public void testState() {
        State state = new State();
        state.setCode("code");
        state.setName("name");
        assertEquals("code", state.getCode());
        assertEquals("name", state.getName());
    }
}
