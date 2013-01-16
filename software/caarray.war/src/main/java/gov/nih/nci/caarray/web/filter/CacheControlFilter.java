//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

/**
 * Filter that ensures dynamic pages are not cached.
 * @author Dan Kokotov
 */
public class CacheControlFilter implements Filter {
    private static final String DYNAMIC_URL_EXTENSION = "action";

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

        if (!(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if (StringUtils.substringAfterLast(httpRequest.getRequestURI(), ".").equals(DYNAMIC_URL_EXTENSION)) {
            if (!request.isSecure()) { // workaround for IE files bug http://support.microsoft.com/kb/812935
                httpResponse.addHeader("Cache-control", "no-cache");
                httpResponse.addHeader("Pragma", "no-cache");
            }
            httpResponse.addIntHeader("Expires", 0);
        }
        chain.doFilter(request, response);
    }

    /**
     * {@inheritDoc}
     */
    public void init(FilterConfig config) throws ServletException {
        // Do nothing
    }
}
