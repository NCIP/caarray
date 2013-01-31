//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;

import java.util.List;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.contact</code> package.
 *
 * @author Dan Kokotov
 */
class ContactDaoImpl extends AbstractCaArrayDaoImpl implements ContactDao {
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Organization> getAllProviders() {
        return getCurrentSession().createQuery("FROM " + Organization.class.getName()
                + " where provider = true order by name").list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Person> getAllPrincipalInvestigators() {
        return getCurrentSession().createQuery("select distinct p FROM " + Experiment.class.getName()
                + " e join e.experimentContacts ec join ec.roles r join ec.contact p " 
                + " where r.value = :piRole").setString("piRole", ExperimentContact.PI_ROLE).list();
    }
}
