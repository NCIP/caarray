/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package gov.nih.nci.caarray.web.filter;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.jaas.AssertionPrincipal;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.util.CommonUtils;

import org.jboss.web.tomcat.security.login.WebAuthentication;

/**
 * This servlet filter performs a programmatic JAAS login using the JBoss
 * <a href="http://community.jboss.org/wiki/WebAuthentication">WebAuthentication</a> class.
 * The filter executes when it receives a CAS ticket and expects the
 * {@link org.jasig.cas.client.jaas.CasLoginModule} JAAS module to perform the CAS
 * ticket validation in order to produce an {@link AssertionPrincipal} from which
 * the CAS assertion is obtained and inserted into the session to enable SSO.
 * <p>
 * If a <code>service</code> init-param is specified for this filter, it supersedes
 * the service defined for the {@link org.jasig.cas.client.jaas.CasLoginModule}.
 *
 * This class has been modified from it's original form to allow for usage with the
 * SessionFixationProtectionLoginModule. These include the removal of logging statements
 * as well as noted below.
 *
 * TODO ARRAY-2453 Remove this class when moving back to the official CAS WebAuthenticationFilter class.
 *
 * @author  Daniel Fisher
 * @author  Marvin S. Addison
 * @version  $Revision$
 * @since 3.1.11
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public final class CasWebAuthenticationFilter extends AbstractCasFilter {

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
            final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        final String ticket = CommonUtils.safeGetParameter(request, getArtifactParameterName());

        if (session != null && session.getAttribute(CONST_CAS_ASSERTION) == null && ticket != null) {
            try {
                final String service = constructServiceUrl(request, response);
                if (!new WebAuthentication().login(service, ticket)) {
                    throw new GeneralSecurityException("JBoss Web authentication failed.");
                }
                /*
                 * This line of obtaining the session again was necessary as following the login with
                 * the WebAuthentication above, the original Session that was obtained was no longer
                 * valid.
                 */
                session = request.getSession();
                if (request.getUserPrincipal() instanceof AssertionPrincipal) {
                    final AssertionPrincipal principal = (AssertionPrincipal) request.getUserPrincipal();
                    session.setAttribute(CONST_CAS_ASSERTION, principal.getAssertion());
                } else {
                    throw new GeneralSecurityException(
                            "JBoss Web authentication did not produce CAS AssertionPrincipal.");
                }
            } catch (final GeneralSecurityException e) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            }
        } else if (session != null && request.getUserPrincipal() == null) {
            // There is evidence that in some cases the principal can disappear
            // in JBoss despite a valid session.
            // This block forces consistency between principal and assertion.
            session.removeAttribute(CONST_CAS_ASSERTION);
        }
        chain.doFilter(request, response);
    }
}
