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
