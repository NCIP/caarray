//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
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
