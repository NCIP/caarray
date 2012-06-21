/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray2
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray2 Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caArray2 Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray2 Software; (ii) distribute and
 * have distributed to and by third parties the caArray2 Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.web.plugins;

import gov.nih.nci.caarray.plugins.CaArrayPluginsFacade;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.views.velocity.VelocityManager;
import org.apache.velocity.app.Velocity;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.PackageProvider;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;

/**
 * XWork PackageProvider implementation that can load cconfigs from OSGi bundles.
 *
 * Adapted from the Struts2 OSGi integration module.
 *
 * @author dkokotov
 */
public class OsgiConfigurationProvider implements PackageProvider, BundleListener {
    private static final Logger LOG = Logger.getLogger(OsgiConfigurationProvider.class);

    private Configuration configuration;
    private ObjectFactory objectFactory;

    private DefaultBundleAccessor bundleAccessor;
    private boolean bundlesChanged = false;
    private ServletContext servletContext;

    private static OsgiConfigurationProvider instance;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Configuration config) throws ConfigurationException {
        this.bundleAccessor = new DefaultBundleAccessor();
        this.configuration = config;

        // this class loader interface can be used by other plugins to lookup resources
        // from the bundles. A temporary class loader interface is set during other configuration
        // loading as well
        this.servletContext.setAttribute(ClassLoaderInterface.CLASS_LOADER_INTERFACE, new BundleClassLoaderInterface());
        instance = this;
    }

    /**
     * @return the currently active instance of this. a hack, but cannot use a true singleton because this class is
     *         created by the struts2 container
     */
    public static OsgiConfigurationProvider getInstance() {
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void loadPackages() throws ConfigurationException {
        LOG.debug("Loading packages from XML and Convention on startup");

        // init action contect
        ActionContext ctx = ActionContext.getContext();
        if (ctx == null) {
            ctx = new ActionContext(new HashMap());
            ActionContext.setContext(ctx);
        }

        final Set<String> bundleNames = new HashSet<String>();

        // iterate over the bundles and load packages from them
        for (final Bundle bundle : CaArrayPluginsFacade.getInstance().getOsgiContainerManager().getBundles()) {
            final String bundleName = bundle.getSymbolicName();
            if (shouldProcessBundle(bundle) && !bundleNames.contains(bundleName)) {
                bundleNames.add(bundleName);
                // load XML and COnvention config
                loadConfigFromBundle(bundle);
            }
        }

        this.bundlesChanged = false;
    }

    /**
     * Loads XML config as well as Convention config from a bundle. Limitation: Constants and Beans are ignored on XML
     * config
     *
     * @param bundle bundle to load from
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    protected void loadConfigFromBundle(Bundle bundle) {
        final String bundleName = bundle.getSymbolicName();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Loading packages from bundle " + bundleName);
        }

        // init action context
        ActionContext ctx = ActionContext.getContext();
        if (ctx == null) {
            ctx = new ActionContext(new HashMap());
            ActionContext.setContext(ctx);
        }

        try {
            // the Convention plugin will use BundleClassLoaderInterface from the ActionContext to find resources
            // and load classes
            ctx.put(ClassLoaderInterface.CLASS_LOADER_INTERFACE, new BundleClassLoaderInterface());
            ctx.put(DefaultBundleAccessor.CURRENT_BUNDLE_NAME, bundleName);

            LOG.debug("Loading XML config from bundle " + bundleName);

            // XML config
            final BundlePackageLoader loader = new BundlePackageLoader();
            for (final PackageConfig pkg : loader.loadPackages(bundle, this.objectFactory,
                    this.configuration.getPackageConfigs())) {
                this.configuration.addPackageConfig(pkg.getName(), pkg);
                this.bundleAccessor.addPackageFromBundle(bundle, pkg.getName());
            }

            // Convention
            // get the existing packages before reloading the provider (se we can figure out what are the new packages)
            final Set<String> packagesBeforeLoading = new HashSet(this.configuration.getPackageConfigNames());

            final PackageProvider conventionPackageProvider =
                    this.configuration.getContainer().getInstance(PackageProvider.class, "convention.packageProvider");
            if (conventionPackageProvider != null) {
                LOG.debug("Loading Convention config from bundle " + bundleName);
                conventionPackageProvider.loadPackages();
            }

            final Set<String> packagesAfterLoading = new HashSet(this.configuration.getPackageConfigNames());
            packagesAfterLoading.removeAll(packagesBeforeLoading);
            if (!packagesAfterLoading.isEmpty()) {
                // add the new packages to the map of bundle -> package
                for (final String packageName : packagesAfterLoading) {
                    this.bundleAccessor.addPackageFromBundle(bundle, packageName);
                }
            }

            if (this.configuration.getRuntimeConfiguration() != null) {
                // if there is a runtime config, it meas that this method was called froma bundle start event
                // instead of the initial load, in that case, reload the config
                this.configuration.rebuildRuntimeConfiguration();
            }
        } finally {
            ctx.put(DefaultBundleAccessor.CURRENT_BUNDLE_NAME, null);
            ctx.put(ClassLoaderInterface.CLASS_LOADER_INTERFACE, null);
        }
    }

    /**
     * Test for whether we should try to extract Struts2 configs from a bundle. Currently a hack - checks whether the
     * name contains caarray. Should be fixed to do something more generic.
     *
     * @param bundle bundle to check
     *
     * @return whether to process the bundle
     */
    protected boolean shouldProcessBundle(Bundle bundle) {
        return bundle.getSymbolicName().contains("caarray");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean needsReload() {
        return this.bundlesChanged;
    }

    /**
     * @param factory ObjectFactory to use when creating beans
     */
    @Inject
    public void setObjectFactory(ObjectFactory factory) {
        this.objectFactory = factory;
    }

    /**
     * @param vm VelocityManager to use for rendering velocity views in the bundle
     */
    @Inject
    public void setVelocityManager(VelocityManager vm) {
        final Properties props = new Properties();
        props.setProperty("osgi.resource.loader.description", "OSGI bundle loader");
        props.setProperty("osgi.resource.loader.class", VelocityBundleResourceLoader.class.getName());
        props.setProperty(Velocity.RESOURCE_LOADER, "strutsfile,strutsclass,osgi");
        vm.setVelocityProperties(props);
    }

    /**
     * @param servletContext the current ServletContext
     */
    @Inject
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bundleChanged(BundleEvent bundleEvent) {
        final Bundle bundle = bundleEvent.getBundle();
        final String bundleName = bundle.getSymbolicName();
        if (bundleName != null && shouldProcessBundle(bundle)) {
            switch (bundleEvent.getType()) {
            case BundleEvent.STARTED:
                LOG.debug("The bundlde " + bundleName
                        + " has been activated and will be scanned for struts configuration");
                loadConfigFromBundle(bundle);
                break;
            case BundleEvent.STOPPED:
                onBundleStopped(bundle);
                break;
            default:
                break;
            }
        }
    }

    /**
     * This method is called when a bundle is stopped, so the config that is related to it is removed.
     *
     * @param bundle the bundle that stopped
     */
    protected void onBundleStopped(Bundle bundle) {
        final Set<String> packages = this.bundleAccessor.getPackagesByBundle(bundle);
        if (!packages.isEmpty()) {
            LOG.debug(String.format("The bundle %s has been stopped. The packages %s will be disabled",
                    bundle.getSymbolicName(), StringUtils.join(packages, ",")));
            for (final String packageName : packages) {
                this.configuration.removePackageConfig(packageName);
            }
        }
    }

    /**
     * Should be called when the bundle is destroyed.
     */
    public void destroy() {
        // no-op
    }
}
