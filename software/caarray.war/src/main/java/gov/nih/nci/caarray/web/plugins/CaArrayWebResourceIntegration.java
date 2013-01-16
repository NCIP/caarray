//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.plugins;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.servlet.ServletContextFactory;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceIntegration;

/**
 * Implementation of Atlassian interfaces to support web resource plugins.
 * 
 * @author dkokotov
 */
public class CaArrayWebResourceIntegration implements WebResourceIntegration, ServletContextFactory,
        ServletContextAware {
    private final PluginAccessor pluginAccessor;
    private final String systemBuildNumber;
    private ServletContext servletContext;

    private final ThreadLocal<Map<String, Object>> requestCache = new ThreadLocal<Map<String, Object>>() {
        @Override
        protected Map<String, Object> initialValue() {
            // if it's null, we just create a new one.. tho this means results from one request will affect the next
            // request
            // on this same thread because we don't ever clean it up from a filter or anything - definitely not for use
            // in
            // production!
            return new HashMap<String, Object>();
        }
    };

    /**
     * Constructor.
     * 
     * @param pluginAccessor PluginAccessor instance to use for locating plugins
     */
    public CaArrayWebResourceIntegration(PluginAccessor pluginAccessor) {
        // we fake the build number by using the startup time which will force anything cached by clients to be
        // reloaded after a restart
        this.systemBuildNumber = String.valueOf(System.currentTimeMillis());
        this.pluginAccessor = pluginAccessor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBaseUrl() {
        return getBaseUrl(UrlMode.AUTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBaseUrl(UrlMode urlMode) {
        final String baseUrl = "http://localhost:8080/caarray";
        if (urlMode == UrlMode.ABSOLUTE) {
            return baseUrl;
        }
        return URI.create(baseUrl).getPath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PluginAccessor getPluginAccessor() {
        return this.pluginAccessor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getRequestCache() {
        return this.requestCache.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSystemBuildNumber() {
        return this.systemBuildNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSystemCounter() {
        return "1";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSuperBatchVersion() {
        return "1";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
