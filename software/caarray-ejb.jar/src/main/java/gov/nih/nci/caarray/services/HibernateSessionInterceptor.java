//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.services;

import gov.nih.nci.caarray.util.CaArrayHibernateHelper;

import com.fiveamsolutions.nci.commons.ejb.AbstractHibernateSessionInterceptor;
import com.fiveamsolutions.nci.commons.util.HibernateHelper;
import com.google.inject.Inject;

/**
 * Handles opening and closing of the Hibernate session as necessary for EJB calls.
 */
public class HibernateSessionInterceptor extends AbstractHibernateSessionInterceptor {
    @Inject private static CaArrayHibernateHelper hibernateHelper; 

    /**
     * {@inheritDoc}
     */
    @Override
    protected HibernateHelper getHelper() {
        return (HibernateHelper) hibernateHelper;
    }

}
