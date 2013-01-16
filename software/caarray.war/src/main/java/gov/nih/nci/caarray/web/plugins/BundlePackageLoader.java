//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.plugins;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;

/**
 * Class that can load Struts2 packages from an OSGi bundle.
 * 
 * Adapted from the Struts2 OSGi integration module.
 * 
 * @author dkokotov
 */
public class BundlePackageLoader {
    private static final Logger LOG = Logger.getLogger(BundlePackageLoader.class);

    /**
     * load packages from given bundle.
     * 
     * @param bundle the OSGi bundle to load from
     * @param objectFactory ObjectFactory to use for creating instances
     * @param pkgConfigs existing package configurations
     * @return list of new package configurations loaded
     * 
     * @throws ConfigurationException on error
     */
    public List<PackageConfig> loadPackages(Bundle bundle, ObjectFactory objectFactory,
            Map<String, PackageConfig> pkgConfigs) throws ConfigurationException {
        final Configuration config = new DefaultConfiguration("struts.xml");
        final BundleConfigurationProvider prov = new BundleConfigurationProvider("struts.xml", bundle);
        for (final PackageConfig pkg : pkgConfigs.values()) {
            config.addPackageConfig(pkg.getName(), pkg);
        }
        prov.setObjectFactory(objectFactory);
        prov.init(config);
        prov.loadPackages();

        final List<PackageConfig> list = new ArrayList<PackageConfig>(config.getPackageConfigs().values());
        list.removeAll(pkgConfigs.values());

        return list;
    }

    /**
     * XmlConfigurationProvider implementation that loads struts2 xml config files from an OSGi bundle.
     */
    private static class BundleConfigurationProvider extends XmlConfigurationProvider {
        private final Bundle bundle;

        public BundleConfigurationProvider(String filename, Bundle bundle) {
            super(filename, false);
            this.bundle = bundle;
        }

        @Override
        protected Iterator<URL> getConfigurationUrls(String fileName) throws IOException {
            final Enumeration<URL> e = this.bundle.getResources("struts.xml");
            return e.hasMoreElements() ? new EnumeratorIterator<URL>(e) : null;
        }
    }

    /**
     * Adapts an anumeration to an Iterator interface.
     */
    private static class EnumeratorIterator<E> implements Iterator<E> {
        private final Enumeration<E> e;

        public EnumeratorIterator(Enumeration<E> e) {
            this.e = e;
        }

        @Override
        public boolean hasNext() {
            return this.e.hasMoreElements();
        }

        @Override
        public E next() {
            return this.e.nextElement();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
