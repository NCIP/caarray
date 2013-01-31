//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.filter;

import gov.nih.nci.caarray.application.permissions.PermissionsManagementService;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.web.action.CaArrayActionHelper;
import gov.nih.nci.caarray.web.action.registration.UserRole;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * Request filter that places the currently logged in user credentials into
 * the UsernameHolder and adds app scoped params to support JSTL role checking.
 *
 * @see UsernameHolder
 */
public class UserFilter implements Filter {
    private static final Logger LOG = Logger.getLogger(UserFilter.class);
    private static final String ADDED_ANON_GROUP = "AddedAnonymousGroup";

    /**
     * {@inheritDoc}
     */
    public void destroy() {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String username = httpRequest.getRemoteUser();
        UsernameHolder.setUser(username);
        boolean hasUserRole = false;

        for (UserRole ur : UserRole.values()) {
            if (httpRequest.isUserInRole(ur.getRoleName())) {
                httpRequest.getSession().setAttribute(ur.getSessionVar(), true);
                hasUserRole = true;
            }
        }

        httpRequest.getSession().setAttribute("isUserHasRole", hasUserRole);

        addAnonymousGroup(username, httpRequest);
        chain.doFilter(request, response);
    }

    /**
     * {@inheritDoc}
     */
    public void init(FilterConfig config) throws ServletException {
        // Do nothing
    }

    private void addAnonymousGroup(String username, HttpServletRequest request) {
        if (username != null
                && !request.isUserInRole(SecurityUtils.ANONYMOUS_GROUP)
                && request.getSession().getAttribute(ADDED_ANON_GROUP) == null) {
            try {
                PermissionsManagementService pms = CaArrayActionHelper.getPermissionsManagementService();
                pms.addUsers(SecurityUtils.ANONYMOUS_GROUP, username);
                request.getSession().setAttribute(ADDED_ANON_GROUP, true);
            } catch (Exception e) {
                LOG.error("Unable to add " + username + " to anonymous group", e);
            }
        }
    }
}
