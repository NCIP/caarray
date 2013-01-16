//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application;

import javax.ejb.ApplicationException;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.log4j.Logger;

/**
 * EJB interceptor class that logs any <code>Exceptions</code> thrown by business methods.
 */
public class ExceptionLoggingInterceptor {
    /**
     * Logs any exceptions thrown by the EJB method invoked.
     *
     * @param invContext the method context
     * @return the method result
     * @throws Exception if invoking the method throws an exception.
     */
    @AroundInvoke
    @SuppressWarnings({"PMD.SignatureDeclareThrowsException", "ucd" }) 
    public Object logException(InvocationContext invContext) throws Exception {
        try {
            return invContext.proceed();
        } catch (Exception e) {
            // do not log exceptions annotated w/AnnotationException - they are expected
            if (e.getClass().getAnnotation(ApplicationException.class) == null) {
                Logger log = Logger.getLogger(invContext.getTarget().getClass());
                log.error("Exception thrown while invoking " + invContext.getMethod().getName(), e);               
            }
            throw e;
        }
    }
}
