//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.platforms;


/**
 * Provides empty implementations for all SessionTransactionManager functions.
 * 
 * Use this class directly if need a SessionTransactionManager that does nothing.
 * 
 * Inherit from this class if you need to implement SessionTransactionManager in a limited context,
 * such as a test, where not all functions are needed.  Override only the functions
 * needed.
 * 
 * @author jscott
 *
 */
public class SessionTransactionManagerNoOpImpl implements SessionTransactionManager {
    /**
     * {@inheritDoc}
     */
    public void beginTransaction() {
        // No-op
    }

    /**
     * {@inheritDoc}
     */
    public void clearSession() {
        // No-op
    }

    /**
     * {@inheritDoc}
     */
    public void commitTransaction() {
        // No-op
    }

    /**
     * {@inheritDoc}
     */
    public void flushSession() {
        // No-op
    }

    /**
     * {@inheritDoc}
     */
    public void rollbackTransaction() {
        // No-op
    }
}
