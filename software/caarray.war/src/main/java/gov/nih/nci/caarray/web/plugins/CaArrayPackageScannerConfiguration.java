//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.plugins;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.plugin.spring.SpringAwarePackageScannerConfiguration;

/**
 * Configuration for the Atlassian package scanner that defines what packages from the host system to make available to
 * plugins.
 *
 * @author dkokotov
 */
public class CaArrayPackageScannerConfiguration extends SpringAwarePackageScannerConfiguration {
    /**
     * Constructor - adds in packages to those defined in the base class and Spring.
     */
    public CaArrayPackageScannerConfiguration() {
        super();
        getPackageIncludes().add("gnu.regexp*");
        getPackageIncludes().add("com.google*");
        getPackageIncludes().add("com.ctc*");
        getPackageIncludes().add("org.codehaus*");
        getPackageIncludes().add("com.fiveamsolutions*");
        getPackageIncludes().add("gov.nih.nci.caarray*");
        getPackageIncludes().add("org.hibernate*");
        getPackageIncludes().add("com.opensymphony.xwork2*");
        getPackageIncludes().add("org.slf4j*");
        getPackageIncludes().add("affymetrix.*");
        getPackageIncludes().add("com.csvreader*");
        getPackageExcludes().add("org.apache.commons.logging*");

        final Map<String, String> pkgVersions = new HashMap<String, String>();
        pkgVersions.put("gov.nih.nci.caarray*", "2.4.0");
        setPackageVersions(pkgVersions);
    }
}
