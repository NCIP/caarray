/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-ejb.jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-ejb.jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-ejb.jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-ejb.jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-ejb.jar Software and any
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
package gov.nih.nci.caarray.web.filter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jasig.cas.client.authentication.DefaultGatewayResolverImpl;
import org.jasig.cas.client.authentication.GatewayResolver;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.Assertion;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter implementation to intercept all requests and attempt to authenticate
 * the user by redirecting them to CAS (unless the user has a ticket).
 *
 * This is a modified version of the org.jasig.cas.client.authentication.AuthenticationFilter class
 * provided in the cas-client-core. It has been enhanced to provide URL exclusions due to problems
 * when trying to redirect AJAX calls and external Experiment calls. Due to the final nature of many of
 * the original class's methods the class needed to be reproduced in it's entirety.
 *
 * Remove this class when working on ARRAY-2516
 *
 * <p>
 * This filter allows you to specify the following parameters (at either the context-level or the filter-level):
 * <ul>
 * <li><code>casServerLoginUrl</code> - the url to LOG into CAS, i.e. https://cas.rutgers.edu/login</li>
 * <li><code>renew</code> - true/false on whether to use renew or not.</li>
 * <li><code>gateway</code> - true/false on whether to use gateway or not.</li>
 * <li><code>excludePatterns</code> - A Comma Separated (as init parameter) list of Regex expressions
 * denoting url patters that should not be forwarded to CAS</li>
 * </ul>
 *
 * <p>Please see AbstractCasFilter for additional properties.</p>
 */
public class CasAuthenticationFilter extends AbstractCasFilter {

    private static final Logger LOG = Logger.getLogger(CasAuthenticationFilter.class);

    static final String EXCLUDE_PARAMETERS_INIT_PARAM = "excludePatterns";

    private String casServerLoginUrl;
    private String[] excludePatterns;
    private boolean renew = false;
    private boolean gateway = false;
    private GatewayResolver gatewayStorage = new DefaultGatewayResolverImpl();

    /**
     * {@inheritDoc}
     *
     * @param filterConfig the filter configuration.
     * @throws ServletException if an error occurs
     */
    protected void initInternal(final FilterConfig filterConfig) throws ServletException {
        if (!isIgnoreInitConfiguration()) {
            super.initInternal(filterConfig);
            casServerLoginUrl = getPropertyFromInitParams(filterConfig, "casServerLoginUrl", null);
            LOG.trace("Loaded CasServerLoginUrl parameter: " + this.casServerLoginUrl);
            renew = parseBoolean(getPropertyFromInitParams(filterConfig, "renew", "false"));
            LOG.trace("Loaded renew parameter: " + this.renew);
            gateway = parseBoolean(getPropertyFromInitParams(filterConfig, "gateway", "false"));
            LOG.trace("Loaded gateway parameter: " + this.gateway);
            excludePatterns = parseExcludePatterns(getPropertyFromInitParams(filterConfig,
                    EXCLUDE_PARAMETERS_INIT_PARAM, "false"));
            LOG.trace("exclude patterns: " + excludePatterns);

            final String gatewayStorageClass = getPropertyFromInitParams(filterConfig, "gatewayStorageClass",
                    null);

            if (gatewayStorageClass != null) {
                try {
                    this.gatewayStorage = (GatewayResolver) Class.forName(gatewayStorageClass).newInstance();
                } catch (final Exception e) {
                    LOG.error(e, e);
                    throw new ServletException(e);
                }
            }
        }
    }

    private String[] parseExcludePatterns(String patterns) {
        if (StringUtils.isBlank(patterns)) {
            return new String[0];
        } else {
            return patterns.split(",");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void init() {
        super.init();
        CommonUtils.assertNotNull(this.casServerLoginUrl, "casServerLoginUrl cannot be null.");
    }

    /**
     * {@inheritDoc}
     *
     * @param servletRequest .
     * @param servletResponse .
     * @param filterChain .
     * @throws IOException in case of error.
     * @throws ServletException in case of error.
     */
    public final void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
            final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final HttpSession session = request.getSession(false);
        final Assertion assertion = session != null ? (Assertion) session.getAttribute(CONST_CAS_ASSERTION)
                : null;

        if (assertion != null) {
            filterChain.doFilter(request, response);
            return;
        }

        final String serviceUrl = constructServiceUrl(request, response);
        final String ticket = retrieveTicketFromRequest(request);
        final boolean wasGatewayed = this.gateway
                && this.gatewayStorage.hasGatewayedAlready(request, serviceUrl);

        if (CommonUtils.isNotBlank(ticket) || wasGatewayed || isExcludedUrl(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String modifiedServiceUrl;

        LOG.debug("no ticket and no assertion found");
        if (this.gateway) {
            LOG.debug("setting gateway attribute in session");
            modifiedServiceUrl = this.gatewayStorage.storeGatewayInformation(request, serviceUrl);
        } else {
            modifiedServiceUrl = serviceUrl;
        }

        LOG.debug("Constructed service url: " + modifiedServiceUrl);

        final String urlToRedirectTo = CommonUtils.constructRedirectUrl(this.casServerLoginUrl,
                getServiceParameterName(), modifiedServiceUrl, this.renew, this.gateway);

        LOG.debug("redirecting to \"" + urlToRedirectTo + "\"");

        response.sendRedirect(urlToRedirectTo);
    }

    /**
     * method to retrieve the ticket.
     *
     * @param request the HTTP ServletRequest.  CANNOT be NULL.
     * @return the ticket if its found, null otherwise.
     */
    protected String retrieveTicketFromRequest(final HttpServletRequest request) {
        return CommonUtils.safeGetParameter(request, getArtifactParameterName());
    }

    private boolean isExcludedUrl(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        for (String pattern : excludePatterns) {
            if (servletPath.matches(pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param renew whether or not to renew the connection.
     */
    public final void setRenew(final boolean renew) {
        this.renew = renew;
    }

    /**
     * @param gateway whether or not to return to desired page instead of cas login.
     */
    public final void setGateway(final boolean gateway) {
        this.gateway = gateway;
    }

    /**
     * @param casServerLoginUrl the url to log into.
     */
    public final void setCasServerLoginUrl(final String casServerLoginUrl) {
        this.casServerLoginUrl = casServerLoginUrl;
    }

    /**
     * @param gatewayStorage the gateway storage object.
     */
    public final void setGatewayStorage(final GatewayResolver gatewayStorage) {
        this.gatewayStorage = gatewayStorage;
    }

    /**
     * @param excludePatterns parsed regex patterns to exclude from filtering.
     */
    public final void setExcludePatterns(final String[] excludePatterns) {
        this.excludePatterns = excludePatterns;
    }

    /**
     * @return the excludePatterns
     */
    public String[] getExcludePatterns() {
        return excludePatterns;
    }
}
