//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao.stub;

import gov.nih.nci.caarray.dao.ContactDao;
import gov.nih.nci.caarray.domain.contact.Organization;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class ContactDaoStub extends AbstractDaoStub implements ContactDao {
    /**
     * {@inheritDoc}
     */
    public List<Organization> getAllProviders() {
        return new ArrayList<Organization>();
    }
}
