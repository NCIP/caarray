//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.injection;

import gov.nih.nci.caarray.application.ApplicationModule;
import gov.nih.nci.caarray.application.file.FileModule;
import gov.nih.nci.caarray.application.util.UtilModule;
import gov.nih.nci.caarray.dao.DaoModule;
import gov.nih.nci.caarray.magetab.MageTabModule;
import gov.nih.nci.caarray.platforms.PlatformJtaTransactionModule;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.services.ServicesModule;
import gov.nih.nci.caarray.staticinjection.CaArrayEjbStaticInjectionModule;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * @author jscott
 * 
 */
public final class InjectorFactory {
    private static final PlatformModule PLATFORM_MODULE = new PlatformModule();
    private static Module platformTransactionMoudle = new PlatformJtaTransactionModule();

    // Static utility class should not have a default constructor.
    private InjectorFactory() {
    }

    /**
     * Holds a InjectorSingleton reference. Inner classes are initialized only when first used, so the Injector is not
     * created until needed.
     * 
     * @author jscott
     */
    private static final class InjectorSingletonHolder {
        private static Injector injector;

        static {
            createInjector();
        }

        private InjectorSingletonHolder() {
        }

        private static synchronized void createInjector() {
            injector = Guice.createInjector(getModule());
        }
        
        private static synchronized Injector getInjector() {
            if (injector == null) {
                injector = Guice.createInjector(getModule());
            }
            return injector;
        }
        
    }

    /**
     * @return the single InjectorSingleton instance.
     */
    public static Injector getInjector() {
        return InjectorSingletonHolder.getInjector();
    }
    
    /**
     * 
     */
    public static void resetInjector() {
        InjectorSingletonHolder.injector = null;
    }
    
    /**
     * Intended for integration tests that need to override modules and create their own injectors.
     * 
     * @return the module used to create the injector singleton;
     */
    public static Module getModule() {
        final Module[] modules =
                new Module[] {new CaArrayEjbStaticInjectionModule(), new CaArrayHibernateHelperModule(),
                        new DaoModule(), new ServicesModule(), new FileModule(), new ApplicationModule(),
                        new MageTabModule(), new UtilModule(), PLATFORM_MODULE, platformTransactionMoudle };

        return Modules.combine(modules);
    }

    /**
     * Add a new platform module. Causes the injector to be recereated.
     * 
     * @param platformModule the module to add
     */
    public static void addPlatform(Module platformModule) {
        PLATFORM_MODULE.addPlatform(platformModule);
        InjectorSingletonHolder.createInjector();
    }

    /**
     * Set the module which configures the transaction implementation for platforms. Primarily for supporting unit
     * tests.
     * 
     * @param platformTransactionModule module to set
     */
    public static void setPlatformTransactionModule(Module platformTransactionModule) {
        platformTransactionMoudle = platformTransactionModule;
    }

}
