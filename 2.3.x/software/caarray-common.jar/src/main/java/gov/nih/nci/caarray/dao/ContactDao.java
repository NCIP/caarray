//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.contact.Organization;

import java.util.List;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.contact</code> package.
 *
 * @author Dan Kokotov
 */
public interface ContactDao extends CaArrayDao {
    /**
     * @return all Organizations in system.
     */
    List<Organization> getAllProviders();
}
