//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao.stub;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.dao.OrganismDao;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class OrganismDaoStub extends AbstractDaoStub implements OrganismDao {
    /**
     * {@inheritDoc}
     */
    public Organism getOrganism(long id) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Organism> getAllOrganisms() {
        return new ArrayList<Organism>();
    }

    /**
     * {@inheritDoc}
     */
    public List<Organism> searchForOrganismNames(String keyword) {
        return new ArrayList<Organism>();
    }
}
