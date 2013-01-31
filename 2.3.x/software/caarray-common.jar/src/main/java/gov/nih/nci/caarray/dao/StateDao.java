//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.state.State;

import java.util.List;

/**
 * @author Akhil Bhaskar (Amentra, Inc.)
 *
 */
public interface StateDao extends CaArrayDao {

    /**
     * Returns all countries.
     *
     * @return all countries.
     */
    List<State> getStates();

}
