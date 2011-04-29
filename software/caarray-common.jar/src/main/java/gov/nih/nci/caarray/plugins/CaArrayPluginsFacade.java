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
