//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application;

import java.security.Identity;
import java.security.Principal;
import java.util.Properties;

import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import javax.ejb.SessionContext;
import javax.ejb.TimerService;
import javax.transaction.UserTransaction;
import javax.xml.rpc.handler.MessageContext;

/**
 * No-op stub for EJB <code>SessionContext</code>. Clients will likely need to subclass and override operations of interest.
 */
@SuppressWarnings("deprecation")
public class SessionContextStub implements SessionContext {

    /**
     * {@inheritDoc}
     */
    public <T> T getBusinessObject(Class<T> arg0) throws IllegalStateException {
        // no-op
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public EJBLocalObject getEJBLocalObject() throws IllegalStateException {
        // no-op
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public EJBObject getEJBObject() throws IllegalStateException {
        // no-op
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Class<?> getInvokedBusinessInterface() throws IllegalStateException {
        // no-op
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public MessageContext getMessageContext() throws IllegalStateException {
        // no-op
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Identity getCallerIdentity() {
        // no-op
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Principal getCallerPrincipal() {
        // no-op
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public EJBHome getEJBHome() {
        // no-op
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public EJBLocalHome getEJBLocalHome() {
        // no-op
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Properties getEnvironment() {
        // no-op
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD")
    public boolean getRollbackOnly() throws IllegalStateException {
        // no-op
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public TimerService getTimerService() throws IllegalStateException {
        // no-op
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public UserTransaction getUserTransaction() throws IllegalStateException {
        // no-op
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCallerInRole(Identity arg0) {
        // no-op
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCallerInRole(String arg0) {
        // no-op
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Object lookup(String arg0) {
        // no-op
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void setRollbackOnly() throws IllegalStateException {
        // no-op
    }

}
