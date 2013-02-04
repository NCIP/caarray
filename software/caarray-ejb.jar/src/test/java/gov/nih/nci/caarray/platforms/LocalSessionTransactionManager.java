//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.platforms;

import gov.nih.nci.caarray.util.CaArrayHibernateHelper;

import com.google.inject.Inject;

/**
 * @author dkokotov
 * 
 */
public class LocalSessionTransactionManager implements SessionTransactionManager {
    private final CaArrayHibernateHelper hibernateHelper;

    @Inject
    public LocalSessionTransactionManager(CaArrayHibernateHelper hibernateHelper) {
        this.hibernateHelper = hibernateHelper;
    }

    @Override
    public void beginTransaction() {
        this.hibernateHelper.beginTransaction();
    }

    @Override
    public void clearSession() {
        this.hibernateHelper.getCurrentSession().clear();
    }

    @Override
    public void commitTransaction() {
        this.hibernateHelper.getCurrentSession().getTransaction().commit();
    }

    @Override
    public void flushSession() {
        this.hibernateHelper.getCurrentSession().flush();
    }

    @Override
    public void rollbackTransaction() {
        this.hibernateHelper.getCurrentSession().getTransaction().rollback();
    }
}
