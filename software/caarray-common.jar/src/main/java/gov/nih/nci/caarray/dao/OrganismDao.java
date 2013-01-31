//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import edu.georgetown.pir.Organism;

import java.util.List;

/**
 * DAO for Organism entities.
 *
 * @author Dan Kokotov
 */
public interface OrganismDao extends CaArrayDao {
    /**
     * Returns the <code>Organism</code> with the id given.
     *
     * @param id get <code>Organism</code> matching this id
     * @return the <code>Organism</code>.
     */
    Organism getOrganism(long id);

    /**
     * Returns the list of all Organisms in the system.
     * @return List of the array designs
     */
    List<Organism> getAllOrganisms();

    /**
     * Returns a list of organisms starting with keyword.
     * @param keyword the keyword
     * @return list of Organisms
     */
    List<Organism> searchForOrganismNames(String keyword);

}
