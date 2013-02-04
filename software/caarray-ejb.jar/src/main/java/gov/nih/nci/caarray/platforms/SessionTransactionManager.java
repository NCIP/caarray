//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.platforms;

/**
 * Interface allowing platform implementations to perform session and transaction management. This is a workaround
 * to account for the need of platform implementations to manage the session and transaction for very large design
 * or data files. 
 * 
 * Eventually either the implementations should be improved to not require this, or an explicit batching model
 * should be introduced into the *Handler interface.
 * 
 * @author dkokotov
 */
public interface SessionTransactionManager {
    /**
     * Flush the current ORM session.
     */
    void flushSession();
    
    /**
     * Clear the first-level cache of the current ORM session.
     */    
    void clearSession();
    
    /**
     * Start a new transaction.
     */        
    void beginTransaction();
    
    /**
     * Commit the current transaction.
     */            
    void commitTransaction();
    
    /**
     * Roll back the current transaction.
     */                
    void rollbackTransaction();
}
