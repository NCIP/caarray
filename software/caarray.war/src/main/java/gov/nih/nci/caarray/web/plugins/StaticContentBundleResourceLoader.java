//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.plugins;

import java.io.IOException;
import java.net.URL;

import org.apache.struts2.dispatcher.DefaultStaticContentLoader;

/**
 * Loads static resources from bundles.
 * 
 * Adapted from the Struts2 OSGi integration module.
 * 
 * @author dkokotov
 */
public class StaticContentBundleResourceLoader extends DefaultStaticContentLoader {
    /**
     * {@inheritDoc}
     */
    @Override
    protected URL findResource(String path) throws IOException {
        return DefaultBundleAccessor.getInstance().loadResourceFromAllBundles(path);
    }
}
