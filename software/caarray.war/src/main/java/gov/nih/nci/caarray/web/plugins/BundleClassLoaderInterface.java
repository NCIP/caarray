//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.plugins;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;

import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;

/**
 * ClassLoaderInterface instance that delegates to the singleton of DefaultBundleAccessor.
 * 
 * Adapted from the Struts2 OSGi integration module.
 * 
 * @author dkokotov
 */
public class BundleClassLoaderInterface implements ClassLoaderInterface {
    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return DefaultBundleAccessor.getInstance().loadClass(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getResource(String name) {
        return DefaultBundleAccessor.getInstance().loadResource(name, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return Collections.enumeration(DefaultBundleAccessor.getInstance().loadResources(name, true));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getResourceAsStream(String name) throws IOException {
        return DefaultBundleAccessor.getInstance().loadResourceAsStream(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassLoaderInterface getParent() {
        return null;
    }
}
