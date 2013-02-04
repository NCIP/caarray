//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 * Stub for tests.
 */
public class UserTransactionStub implements UserTransaction {


    public void begin() throws NotSupportedException, SystemException {
        // no-op
    }


    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
            SecurityException, IllegalStateException, SystemException {
        // no-op
    }

    public int getStatus() throws SystemException {
        return 0;
    }
    
    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        // no-op
    }

    public void setRollbackOnly() throws IllegalStateException, SystemException {
        // no-op
    }

    public void setTransactionTimeout(int arg0) throws SystemException {
        // no-op
    }

}
