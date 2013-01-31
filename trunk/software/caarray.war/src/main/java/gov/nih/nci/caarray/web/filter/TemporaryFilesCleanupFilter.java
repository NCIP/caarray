//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.filter;

import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Filter that ensures that temporary files used to hold data are cleaned up.
 * @author Dan Kokotov
 */
public class TemporaryFilesCleanupFilter implements Filter {    
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
        chain.doFilter(request, response);
        TemporaryFileCacheLocator.getTemporaryFileCache().closeFiles();
    }

    /**
     * {@inheritDoc}
     */
    public void init(FilterConfig config) throws ServletException {
        // Do nothing
    }
}
