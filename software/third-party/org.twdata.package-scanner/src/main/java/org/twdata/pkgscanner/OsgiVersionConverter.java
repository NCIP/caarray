//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package org.twdata.pkgscanner;

/**
 * Converts a version into an OSGi-compatible version
 */
public interface OsgiVersionConverter
{
    /**
     * Gets the OSGi-compatible version of the passed version
     * @param version The version to convert
     * @return The converted version
     */
    String getVersion( String version );
}
