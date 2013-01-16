//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.plugins;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.staticinjection.CaArrayWarStaticInjectionModule;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.logging.api.logger.util.ThreadVariable;
import gov.nih.nci.logging.api.user.UserInfo;

import org.dom4j.Element;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.google.inject.Module;

/**
 * ModuleDescritor for plugin modules that configure an array platform Guice module. Expects a single configuration
 * element, 'platform-module' with attribute 'class', giving the fully qualified name of the Guice module class.
 * 
 * @author dkokotov
 */
public class ArrayPlatformModuleDescriptor extends AbstractModuleDescriptor<Void> {
    private String moduleClassName;

    /**
     * Constructor.
     * 
     * @param factory the factory to use for creating the module
     */
    public ArrayPlatformModuleDescriptor(ModuleFactory factory) {
        super(factory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void getModule() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);

        this.moduleClassName = element.element("platform-module").attributeValue("class");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enabled() {
        super.enabled();

        final Class<Module> moduleClass = loadClass(this.moduleClassName);
        Module platformModule = null;
        try {
            platformModule = moduleClass.newInstance();
        } catch (final IllegalAccessException e) {
            throw new IllegalArgumentException("Could not instantiate class: " + this.moduleClassName, e);
        } catch (final InstantiationException e) {
            throw new IllegalArgumentException("Could not instantiate class: " + this.moduleClassName, e);
        }

        InjectorFactory.addPlatform(platformModule);

        // refresh static references in war layer
        InjectorFactory.getInjector().createChildInjector(new CaArrayWarStaticInjectionModule());

        refreshArrayTypes(InjectorFactory.getInjector().getInstance(CaArrayHibernateHelper.class));
    }

    private void refreshArrayTypes(CaArrayHibernateHelper hibernateHelper) {
        try {
            hibernateHelper.openAndBindSession();
            // hack needed for CLM
            ThreadVariable.set(new UserInfo());

            ServiceLocatorFactory.getArrayDataService().initialize();
        } finally {
            hibernateHelper.unbindAndCleanupSession();
        }

    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> loadClass(String className) {
        try {
            return (Class<T>) getPlugin().getClassLoader().loadClass(className);
        } catch (final ClassNotFoundException e) {
            throw new IllegalArgumentException("Could not load class " + className, e);
        }
    }
}
