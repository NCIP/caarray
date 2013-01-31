//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services;

import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.UsernameHolder;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

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
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // method invocation wrapper requires throws Exception
    public Object prepareReturnValue(InvocationContext invContext) throws Exception {
        String username;
        try {
            username = sessionContext.getCallerPrincipal().getName();
        } catch (IllegalStateException e) {
            username = SecurityUtils.ANONYMOUS_USERNAME;
        }
        UsernameHolder.setUser(username);
        return invContext.proceed();
    }
}
