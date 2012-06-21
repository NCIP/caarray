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
