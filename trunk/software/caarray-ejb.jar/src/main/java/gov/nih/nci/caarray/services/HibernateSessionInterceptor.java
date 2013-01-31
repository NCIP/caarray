//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services;

import gov.nih.nci.caarray.util.HibernateUtil;

import com.fiveamsolutions.nci.commons.ejb.AbstractHibernateSessionInterceptor;
import com.fiveamsolutions.nci.commons.util.HibernateHelper;

/**
 * Handles opening and closing of the Hibernate session as necessary for EJB calls.
 */
public class HibernateSessionInterceptor extends AbstractHibernateSessionInterceptor {

    /**
     * {@inheritDoc}
     */
    @Override
    protected HibernateHelper getHelper() {
        return HibernateUtil.getHibernateHelper();
    }

}
