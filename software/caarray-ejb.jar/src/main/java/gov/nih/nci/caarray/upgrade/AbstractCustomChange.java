//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.upgrade;

import liquibase.FileOpener;
import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.InvalidChangeDefinitionException;
import liquibase.exception.SetupException;

/**
 * Base class with useful resources and default implementations
 * for custom Liquibase migrators.
 */
public abstract class AbstractCustomChange implements CustomTaskChange {    
    private FileOpener fileOpener;
    private ClassLoader oldClassLoader;

    /**
     * {@inheritDoc}
     */
    public final void execute(Database database) throws CustomChangeException {
        fixClassLoader();
        try {
            doExecute(database);
        } finally {
            restoreClassLoader();
        }
    }

    /**
     * Running from an Ant updateDatabase task will fail due to class loader issues
     * unless the context class loader is changed. This appears to be a known
     * problem in liquibase 1.9.x and might be fixed when liquibase 2.0 is released.
     */
    private void fixClassLoader() {
        ClassLoader currentClassLoader = this.getClass().getClassLoader();
        oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(currentClassLoader);
    }
    
    private void restoreClassLoader() {
        Thread.currentThread().setContextClassLoader(oldClassLoader);
    }
 
    /**
     * Implementing classes override to preform actual database changes.
     * @param database the liquibase database
     * @throws CustomChangeException if any error occurs
     */
    protected abstract void doExecute(Database database) throws CustomChangeException;

    /**
     * {@inheritDoc}
     */
    public String getConfirmationMessage() {
        return "Custom change successful";
    }

    /**
     * {@inheritDoc}
     */
    public void setFileOpener(FileOpener fileOpener) {        
        this.fileOpener = fileOpener;
    }
    
    /**
     * @return the fileOpener
     */
    protected FileOpener getFileOpener() {
        return fileOpener;
    }

    /**
     * {@inheritDoc}
     */
    public void setUp() throws SetupException {
        // no-op
    }

    /**
     * This default implementation is a no-op.
     * {@inheritDoc}
     */
    public void validate(Database db) throws InvalidChangeDefinitionException {
        // nop-op
    }
}


