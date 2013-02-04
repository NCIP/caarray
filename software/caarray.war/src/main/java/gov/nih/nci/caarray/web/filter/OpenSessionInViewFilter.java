//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.filter;

import gov.nih.nci.caarray.util.CaArrayHibernateHelper;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.google.inject.Inject;

/**
 * A Filter that enables OpenSessionInView behavior. It leverages the contextual session facility in Hibernate, opening
 * a session at the beginning of a request and closing it at the end For this to take effect you must set the hibernate
 * property "hibernate.current_session_context_class" to have the value "managed"
 * 
 * @author Dan Kokotov
 */
public class OpenSessionInViewFilter implements Filter {
    @Inject
    private static CaArrayHibernateHelper hibernateHelper;

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        // set up a new session at beginning of request and close it afterwards
        try {
            hibernateHelper.openAndBindSession();
            chain.doFilter(request, response);
        } finally {
            hibernateHelper.unbindAndCleanupSession();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // no-op
    }
}
