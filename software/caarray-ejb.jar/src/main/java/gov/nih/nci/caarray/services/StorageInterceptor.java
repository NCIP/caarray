//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services;

import gov.nih.nci.caarray.dataStorage.spi.StorageUnitOfWork;
import gov.nih.nci.caarray.injection.InjectorFactory;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import com.google.inject.Injector;

/**
 * Interceptor for EJBs that begins and ends a storage unit of work.
 * 
 * @author dkokotov
 */
public class StorageInterceptor {
    /**
     * Wraps a service call with begin() and end() calls on the configured unit of work.
     * 
     * @param invContext the method context
     * @return the method result
     * @throws Exception if invoking the method throws an exception.
     */
    @AroundInvoke
    @SuppressWarnings({ "PMD.SignatureDeclareThrowsException", "ucd" })
    public Object prepareReturnValue(InvocationContext invContext) throws Exception {
        final Injector injector = InjectorFactory.getInjector();
        final StorageUnitOfWork unit = injector.getInstance(StorageUnitOfWork.class);
        unit.begin();
        try {
            return invContext.proceed();
        } finally {
            unit.end();
        }
    }
}
