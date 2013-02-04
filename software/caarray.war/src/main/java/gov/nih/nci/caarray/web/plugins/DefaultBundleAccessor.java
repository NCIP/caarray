//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.plugins;

import gov.nih.nci.caarray.plugins.CaArrayPluginsFacade;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.entities.ActionConfig;

/**
 * Helper class that find resources and loads classes from the list of bundles.
 * 
 * Adapted from the Struts2 OSGi integration module.
 * 
 * @author dkokotov
 */
public class DefaultBundleAccessor {
    /**
     * Key under which the current bundle is stored in the Struts2 ActionContext.
     */
    public static final String CURRENT_BUNDLE_NAME = "__bundle_name__";

    private static final Logger LOG = Logger.getLogger(DefaultBundleAccessor.class);

    private static DefaultBundleAccessor self; // NOPMD - on-purpose usage as a quasi-singleton field

    private final Map<String, String> packageToBundle = new HashMap<String, String>();
    private final Map<Bundle, Set<String>> packagesByBundle = new HashMap<Bundle, Set<String>>();

    /**
     * Constructor.
     */
    public DefaultBundleAccessor() {
        self = this;
        LOG.info("Created DBA");
    }

    /**
     * @return the currently active instance of this. a hack, but cannot use a true singleton because this class is
     *         created by the struts2 container.
     */
    public static DefaultBundleAccessor getInstance() {
        return self;
    }

    /**
     * Add as Bundle -> Package mapping.
     * 
     * @param bundle the bundle where the package was loaded from
     * @param packageName the anme of the loaded package
     */
    public void addPackageFromBundle(Bundle bundle, String packageName) {
        this.packageToBundle.put(packageName, bundle.getSymbolicName());
        Set<String> pkgs = this.packagesByBundle.get(bundle);
        if (pkgs == null) {
            pkgs = new HashSet<String>();
            this.packagesByBundle.put(bundle, pkgs);
        }
        pkgs.add(packageName);
    }

    /**
     * load a class from the current bundle.
     * 
     * @param className class to load
     * @return the loaded class
     * @throws ClassNotFoundException if cannot resolve the class in the bundle
     */
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        final Bundle bundle = getCurrentBundle();
        if (bundle != null) {
            final Class cls = bundle.loadClass(className);
            LOG.debug(String.format("Located class [%s] in bundle [%s]", className, bundle.getSymbolicName()));
            return cls;
        }

        throw new ClassNotFoundException("Unable to find class " + className);
    }

    private Bundle getCurrentBundle() {
        final ActionContext ctx = ActionContext.getContext();
        String bundleName = (String) ctx.get(CURRENT_BUNDLE_NAME);
        if (bundleName == null) {
            final ActionInvocation inv = ctx.getActionInvocation();
            final ActionProxy proxy = inv.getProxy();
            final ActionConfig actionConfig = proxy.getConfig();
            bundleName = this.packageToBundle.get(actionConfig.getPackageName());
        }
        if (bundleName != null) {
            return getActiveBundles().get(bundleName);
        }
        return null;
    }

    /**
     * load all resources with given name from the current bundle.
     * 
     * @param name name of resource to load
     * @param translate whether translate from bundle URL to JAR url
     * @return the list of resources, as URLs, matching the name, or null if none
     * @throws IOException on error
     */
    public List<URL> loadResources(String name, boolean translate) throws IOException {
        final Bundle bundle = getCurrentBundle();
        if (bundle != null) {
            final List<URL> resources = new ArrayList<URL>();
            final Enumeration e = bundle.getResources(name);
            while (e.hasMoreElements()) {
                resources.add(translate ? CaArrayPluginsFacade.translateBundleURLToJarURL((URL) e.nextElement(),
                        getCurrentBundle()) : (URL) e.nextElement());
            }
            return resources;
        }

        return null;
    }

    /**
     * Try to load resource with given name from all known bundle.
     * 
     * @param name of resource to load
     * @return URL for resource if found, null otherwise
     * @throws IOException on error
     */
    public URL loadResourceFromAllBundles(String name) throws IOException {
        for (final Map.Entry<String, Bundle> entry : getActiveBundles().entrySet()) {
            final Enumeration e = entry.getValue().getResources(name);
            if (e.hasMoreElements()) {
                return (URL) e.nextElement();
            }
        }

        return null;
    }

    /**
     * load the first resource with given name from the current bundle.
     * 
     * @param name name of resource to load
     * @param translate whether translate from bundle URL to JAR url
     * @return the resource as URL if found, null otherwise
     */
    public URL loadResource(String name, boolean translate) {
        final Bundle bundle = getCurrentBundle();
        if (bundle != null) {
            final URL url = bundle.getResource(name);
            try {
                return translate ? CaArrayPluginsFacade.translateBundleURLToJarURL(url, getCurrentBundle()) : url;
            } catch (final Exception e) {
                LOG.error("Unable to translate bundle URL to jar URL", e);
                return null;
            }
        }

        return null;
    }

    /**
     * Get the struts2 packages that were loaded from the given bundle, if any.
     * 
     * @param bundle the bundle to check
     * @return the struts2 packages loaded from the bundle, null if none
     */
    public Set<String> getPackagesByBundle(Bundle bundle) {
        return this.packagesByBundle.get(bundle);
    }

    /**
     * load the first resource with given name from the current bundle.
     * 
     * @param name name of resource to load
     * @return the InputStream for the resource if found, null otherwise
     * @throws IOException on error
     */
    public InputStream loadResourceAsStream(String name) throws IOException {
        final URL url = loadResource(name, false);
        if (url != null) {
            return url.openStream();
        }
        return null;
    }

    private static Map<String, Bundle> getActiveBundles() {
        final Map<String, Bundle> bundles = new HashMap<String, Bundle>();
        for (final Bundle bundle : CaArrayPluginsFacade.getInstance().getOsgiContainerManager().getBundles()) {
            if (bundle.getState() == Bundle.ACTIVE) {
                bundles.put(bundle.getSymbolicName(), bundle);
            }
        }

        return Collections.unmodifiableMap(bundles);
    }

}
