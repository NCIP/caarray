package org.twdata.pkgscanner;

import java.io.File;

/**
 * Represents an export consisting of a package name and version
 * 
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
public class ExportPackage implements Comparable<ExportPackage> {
    private final String packageName;
    private final String version;
    private final File location;

    /**
     * Constructs an ExportPackage object.
     *
     * @param packageName name of the package. Cannot be null.
     * @param version Version for this package. A null value means the version is "unknown".
     * @param location The file where this package was found. Cannot be null.
     */
    public ExportPackage(String packageName, String version, File location) {
        if (packageName == null)
            throw new IllegalArgumentException("packageName must not be null");
        if (location == null)
            throw new IllegalArgumentException("location must not be null");
        this.version = version;
        this.location = location;
        if (packageName.startsWith(".")) {
            packageName = packageName.substring(1);
        }
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getVersion() {
        return version;
    }

    public File getLocation() {
        return location;
    }

    public int compareTo(ExportPackage exportPackage) {
        return packageName.compareTo(exportPackage.getPackageName());
    }

    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExportPackage that = (ExportPackage) o;

        if (!packageName.equals(that.packageName)) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    public int hashCode()
    {
        int result;
        result = packageName.hashCode();
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}
