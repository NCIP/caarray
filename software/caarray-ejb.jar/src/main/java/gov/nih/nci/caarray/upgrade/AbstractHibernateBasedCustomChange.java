//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.upgrade;

import gov.nih.nci.caarray.application.ApplicationModule;
import gov.nih.nci.caarray.application.file.FileModule;
import gov.nih.nci.caarray.application.util.UtilModule;
import gov.nih.nci.caarray.dao.DaoModule;
import gov.nih.nci.caarray.magetab.MageTabModule;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.services.ServicesModule;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;

import java.sql.Connection;

import liquibase.database.Database;
import liquibase.exception.CustomChangeException;

import org.hibernate.Transaction;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Base class for custom Liquibase migrators that use hibernate persistence.
 * @author dharley
 *
 */
public abstract class AbstractHibernateBasedCustomChange extends AbstractCustomChange {
    
    private Injector defaultInjector;
    private static final PlatformModule PLATFORM_MODULE = new PlatformModule();
    
    /**
     * {@inheritDoc}
     */
    public void doExecute(final Database database) throws CustomChangeException {
        SingleConnectionHibernateHelper hibernateHelper =
            createHibernateHelper(database.getConnection().getUnderlyingConnection());
        Transaction transaction = hibernateHelper.beginTransaction();
        try {
            doHibernateExecute(hibernateHelper);
            transaction.commit();
        } catch (Exception exception) {
            transaction.rollback();
            throw new CustomChangeException(exception);
        }
    }
    
    /**
     * Executes the update work using the specified connection.  Clients do not need to handle the transaction as it
     * will be managed by calling execute(Database) method.
     * @param singleConnectionHibernateHelper the SingleConnectionHibernateHelper to use.
     */
    protected abstract void doHibernateExecute(final SingleConnectionHibernateHelper singleConnectionHibernateHelper);
    
    /**
     * Gets the default list of Guice Modules (ArrayDataModule, ServicesModule, ApplicationModule).
     * @return the default list of Guice Modules.
     */
    protected Module[] getGuiceModules() {
        return new Module[] {
                new DaoModule(), 
                new ServicesModule(),
                new FileModule(),
                new ApplicationModule(),
                new MageTabModule(),
                new UtilModule(),
                PLATFORM_MODULE,
            };
    }
    
    /**
     * Creates the guice injector.
     * @return the guice injector.
     */
    protected Injector getInjector() {
        if (null == defaultInjector) {
            Module localModule = new AbstractModule() {
                @Override
                protected void configure() {
                    bind(CaArrayHibernateHelper.class).toInstance(new SingleConnectionHibernateHelper());             
                }   
            };
            final Module[] defaultModules = getGuiceModules();
            final Module[] allModules = new Module[defaultModules.length + 1];
            System.arraycopy(defaultModules, 0, allModules, 0, defaultModules.length);
            allModules[allModules.length - 1] = localModule;
            defaultInjector = Guice.createInjector(allModules);
        }
        return defaultInjector;
    }
    
    /**
     * Creates a hibernate helper instance using the default guice injector.
     * @param connection the connection to use
     * @return SingleConnectionHibernateHelper
     */
    protected SingleConnectionHibernateHelper createHibernateHelper(final Connection connection) {
        return createHibernateHelper(connection, getInjector());
    }

    /**
     * Creates a hibernate helper instance.
     * @param connection the connection to use.
     * @param injector the injector to use.
     * @return hibernate helper instance.
     */
    protected SingleConnectionHibernateHelper createHibernateHelper(final Connection connection,
            final Injector injector) {
        SingleConnectionHibernateHelper hibernateHelper = (SingleConnectionHibernateHelper) injector
                .getInstance(CaArrayHibernateHelper.class);
        hibernateHelper.initialize(connection);
        return hibernateHelper;
    }
}
