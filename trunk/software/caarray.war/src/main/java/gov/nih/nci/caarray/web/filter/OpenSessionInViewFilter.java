//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.filter;

import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * A Filter that enables OpenSessionInView behavior. It leverages the contextual session facility
 * in Hibernate, opening a session at the beginning of a request and closing it at the end
 * For this to take effect you must set the hibernate property "hibernate.current_session_context_class"
 * to have the value "managed"
 *
 * @author Dan Kokotov
 */
public class OpenSessionInViewFilter implements Filter {

    /**
     * {@inheritDoc}
     */
    public void destroy() {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        // set up a new session at beginning of request and close it afterwards
        boolean isSysAdmin = false;
        try {
            HibernateUtil.openAndBindSession();
            try {
                User csmUser = SecurityUtils.getAuthorizationManager().getUser(
                        ((HttpServletRequest) request).getRemoteUser());
                isSysAdmin = SecurityUtils.isSystemAdministrator(csmUser);
            } catch (CSObjectNotFoundException e) {
                // assume the user is not a sys admin
                isSysAdmin = false;
            }
            if (isSysAdmin) {
                HibernateUtil.setFiltersEnabled(false);
                HibernateUtil.disableFilters();
            }
            chain.doFilter(request, response);
        } finally {
            HibernateUtil.setFiltersEnabled(true);
            HibernateUtil.unbindAndCleanupSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void init(FilterConfig arg0) throws ServletException {
        // no-op
    }
}
