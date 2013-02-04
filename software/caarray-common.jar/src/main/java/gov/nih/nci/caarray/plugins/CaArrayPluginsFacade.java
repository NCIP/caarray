//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import org.osgi.framework.Bundle;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.PluginInstaller;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.main.HotDeployer;
import com.atlassian.plugin.manager.DefaultPluginManager;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import com.atlassian.plugin.web.WebInterfaceManager;

/**
 * Facade class for the plugin system. handles initialization and providing access to various components needed to
 * interact with the plugin system.
 * 
 * It's treated as a quasi-singleton for convenience - it's constructed via Spring, but then provides static
 * getInstance/setInstance to get the one instance, as it needs to be accessed from classes that do not participate in
 * Spring.
 * 
 * @author dkokotov
 */
public class CaArrayPluginsFacade {
    private final OsgiContainerManager osgiContainerManager;
    private final PluginEventManager pluginEventManager;
    private final DefaultPluginManager pluginManager;
    private final HotDeployer hotDeployer;
    private WebInterfaceManager webInterfaceManager;

    private static CaArrayPluginsFacade instance;

    /**
     * Suffix for temporary directories which will be removed on shutdown.
     */
    public static final String TEMP_DIRECTORY_SUFFIX = ".tmp";

    /**
     * Constructs an instance of the plugin framework with the specified config. No additional validation is performed
     * on the configuration, so it is recommended you use the PluginsConfigurationBuilder class to create a
     * configuration instance.
     * 
     * @param osgiContainerManager OsgiContainerManager instance to use
     * @param pluginManager PluginManager instance to use
     * @param pluginEventManager PluginEventManager instance to use
     * @param hotDeployer HotDeployer instance to use
     * @param pluginInstaller PluginInstaller instance to use
     * @param webInterfaceManager WebInterfaceManager instance to use
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public CaArrayPluginsFacade(OsgiContainerManager osgiContainerManager, DefaultPluginManager pluginManager,
            PluginEventManager pluginEventManager, HotDeployer hotDeployer, PluginInstaller pluginInstaller,
            WebInterfaceManager webInterfaceManager) {
        this.pluginEventManager = pluginEventManager;
        this.pluginManager = pluginManager;
        this.osgiContainerManager = osgiContainerManager;
        this.hotDeployer = hotDeployer;
        this.webInterfaceManager = webInterfaceManager;

        pluginManager.setPluginInstaller(pluginInstaller);

    }

    /**
     * Starts the plugins framework. Will return once the plugins have all been loaded and started. Should only be
     * called once.
     * 
     * @throws PluginParseException If there was any problems parsing any of the plugins
     */
    public void start() throws PluginParseException {
        this.pluginManager.init();
        if (this.hotDeployer != null && !this.hotDeployer.isRunning()) {
            this.hotDeployer.start();
        }
    }

    /**
     * Stops the framework.
     */
    public void stop() {
        if (this.hotDeployer != null && this.hotDeployer.isRunning()) {
            this.hotDeployer.stop();
        }
        this.pluginManager.shutdown();
    }

    /**
     * @return the underlying OSGi container manager
     */
    public OsgiContainerManager getOsgiContainerManager() {
        return this.osgiContainerManager;
    }

    /**
     * @return the plugin event manager
     */
    public PluginEventManager getPluginEventManager() {
        return this.pluginEventManager;
    }

    /**
     * @return the plugin controller for manipulating plugins
     */
    public PluginController getPluginController() {
        return this.pluginManager;
    }

    /**
     * @return the plugin accessor for accessing plugins
     */
    public PluginAccessor getPluginAccessor() {
        return this.pluginManager;
    }

    /**
     * A bundle is a jar, and a bunble URL will be useless to clients, this method translates a URL to a resource
     * inside. a bundle from "bundle:something/path" to "jar:file:bundlelocation!/path"
     * 
     * @param bundleUrl a bundle: url for the budle
     * @param bundle the bundle itself
     * 
     * @return url for bundle usable by clients
     * @throws MalformedURLException if the jar url would be invalid
     */
    public static URL translateBundleURLToJarURL(URL bundleUrl, Bundle bundle) throws MalformedURLException {
        if (bundleUrl != null && "bundle".equalsIgnoreCase(bundleUrl.getProtocol())) {
            final StringBuilder sb = new StringBuilder("jar:");
            sb.append(bundle.getLocation());
            sb.append("!");
            sb.append(bundleUrl.getFile());
            return new URL(sb.toString());
        }

        return bundleUrl;
    }

    /**
     * @return the web interface manager in use
     */
    public WebInterfaceManager getWebInterfaceManager() {
        return this.webInterfaceManager;
    }

    /**
     * @param webInterfaceManager the web interace manager to use
     */
    public void setWebInterfaceManager(WebInterfaceManager webInterfaceManager) {
        this.webInterfaceManager = webInterfaceManager;
    }

    /**
     * @return the currently enabled set of plugins
     */
    public Collection<Plugin> getPlugins() {
        return getPluginAccessor().getEnabledPlugins();
    }

    /**
     * @return the singleton instance of this
     */
    public static CaArrayPluginsFacade getInstance() {
        return instance;
    }

    /**
     * @param instance the singleton instance to use
     */
    public static void setInstance(CaArrayPluginsFacade instance) {
        CaArrayPluginsFacade.instance = instance;
    }
}
