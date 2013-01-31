//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.filter;

import gov.nih.nci.caarray.web.upgrade.UpgradeManager;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Winston Cheng
 *
 */
public class UpgradeFilter implements Filter {
    private static final String STATUS_UPDATE = "/upgradeStatus.action";

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
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        boolean update = UpgradeManager.getInstance().isUpgradeRequired();
        if (!update || httpRequest.getRequestURI().endsWith(STATUS_UPDATE)) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(httpRequest.getContextPath() + STATUS_UPDATE);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void init(FilterConfig arg0) throws ServletException {
        // Do nothing
    }
}
