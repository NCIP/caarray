//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.contact.Organization;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.contact</code> package.
 *
 * @author Dan Kokotov
 */
class ContactDaoImpl extends AbstractCaArrayDaoImpl implements ContactDao {
    private static final Logger LOG = Logger.getLogger(ContactDaoImpl.class);

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Organization> getAllProviders() {
        return getCurrentSession().createQuery("FROM " + Organization.class.getName()
                + " where provider = true order by name").list();
    }

    @Override
    Logger getLog() {
        return LOG;
    }
}
