//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.plugins;

import org.dom4j.Element;
import org.osgi.framework.Bundle;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.osgi.factory.OsgiPlugin;

/**
 * Module descriptor for a module that loads a Struts2 package, along with action classes and views implementing it.
 * 
 * @author dkokotov
 */
public class Struts2ConfigModuleDescriptor extends AbstractModuleDescriptor<Void> {
    /**
     * Constructor.
     * 
     * @param factory ModuleFactory to use
     */
    public Struts2ConfigModuleDescriptor(ModuleFactory factory) {
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enabled() {
        super.enabled();

        final Bundle bundle = ((OsgiPlugin) getPlugin()).getBundle();
        final OsgiConfigurationProvider provider = OsgiConfigurationProvider.getInstance();
        if (provider != null) {
            provider.loadConfigFromBundle(bundle);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disabled() {
        super.disabled();

        final Bundle bundle = ((OsgiPlugin) getPlugin()).getBundle();
        final OsgiConfigurationProvider provider = OsgiConfigurationProvider.getInstance();
        if (provider != null) {
            provider.onBundleStopped(bundle);
        }
    }
}
