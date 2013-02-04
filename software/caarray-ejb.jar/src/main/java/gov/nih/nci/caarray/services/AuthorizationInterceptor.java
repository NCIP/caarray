//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.services;

import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;

import java.security.Principal;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.jboss.security.SecurityAssociation;

/**
 * Associates the current authenticated user (if any) of remote EJBs with the current
 * session.
 */
public class AuthorizationInterceptor {

    @Resource private SessionContext sessionContext;

    /**
     * Ensures that the current authenticated user is associated with the current
     * session so that security filtering is correct.
     *
     * @param invContext the method context
     * @return the method result
     * @throws Exception if invoking the method throws an exception.
     */
    @AroundInvoke
    @SuppressWarnings({"PMD.SignatureDeclareThrowsException", "ucd" }) 
    public Object prepareReturnValue(InvocationContext invContext)
            throws Exception {
        String username = null;
        try {
            for (Principal p : SecurityAssociation.getSubject().getPrincipals()) {
                if (p instanceof org.jasig.cas.client.jaas.AssertionPrincipal) {
                    username = p.getName();
                    break;
                }
            }
            if (username == null) {
                username = sessionContext.getCallerPrincipal().getName();
            }
        } catch (IllegalStateException e) {
            username = SecurityUtils.ANONYMOUS_USERNAME;
        }
        CaArrayUsernameHolder.setUser(username);
        return invContext.proceed();
    }
}
