//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services;

import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 * Calls the TemporaryFileCache.closeFiles() method to make sure any temporary files used by an API call are 
 * cleaned up.
 * 
 * @author dkokotov 
 */
public class TemporaryFileCleanupInterceptor {
    /**
     * Calls the TemporaryFileCache.closeFiles() method to make sure any temporary files used by an API call are cleaned
     * up.
     * 
     * @param invContext the method context
     * @return the method result
     * @throws Exception if invoking the method throws an exception.
     */
    @AroundInvoke
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // method invocation wrapper requires throws Exception
    public Object prepareReturnValue(InvocationContext invContext) throws Exception {
        try {
            return invContext.proceed();
        } finally {
            TemporaryFileCacheLocator.getTemporaryFileCache().closeFiles();            
        }
    }
}
