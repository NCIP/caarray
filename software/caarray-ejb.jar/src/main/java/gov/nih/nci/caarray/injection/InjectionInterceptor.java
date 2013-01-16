//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.injection;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import com.google.inject.Injector;

/**
 * This class provides guice injection to the ejb tier.
 * @author jscott
 */
public class InjectionInterceptor {
    /**
     * Allows guice to inject in to the EJB.
     * @param ctx the context.
     * @return the object
     * @throws Exception on error.
     */
    @AroundInvoke
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public Object injectByGuice(InvocationContext ctx) throws Exception {
        final Injector injector = InjectorFactory.getInjector();
        Object target = ctx.getTarget();
        injector.injectMembers(target);
        return ctx.proceed();
    }

}
