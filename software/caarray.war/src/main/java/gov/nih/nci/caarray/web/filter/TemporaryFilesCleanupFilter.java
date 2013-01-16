//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.filter;

import gov.nih.nci.caarray.dataStorage.spi.StorageUnitOfWork;
import gov.nih.nci.caarray.injection.InjectorFactory;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.google.inject.Injector;

/**
 * Filter that ensures that temporary files used to hold data are cleaned up.
 * 
 * @author Dan Kokotov
 */
public class TemporaryFilesCleanupFilter implements Filter {
    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        final Injector injector = InjectorFactory.getInjector();
        final StorageUnitOfWork unit = injector.getInstance(StorageUnitOfWork.class);
        unit.begin();
        try {
            chain.doFilter(request, response);
        } finally {
            unit.end();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig config) throws ServletException {
        // Do nothing
    }
}
