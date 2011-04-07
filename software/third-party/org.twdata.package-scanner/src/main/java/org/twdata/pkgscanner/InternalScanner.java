package org.twdata.pkgscanner;

import java.net.URL;
import java.net.URISyntaxException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.io.IOException;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Does the actual work of scanning the classloader
 */
class InternalScanner {
    private final Logger log = LoggerFactory.getLogger(InternalScanner.class);
    private Map<String,Set<String>> jarContentCache = new HashMap<String,Set<String>>();
    private ClassLoader classloader;
    private PackageScanner.VersionMapping[] versionMappings;
    private OsgiVersionConverter versionConverter = new DefaultOsgiVersionConverter();
    private final boolean debug;

    static interface Test {
        boolean matchesPackage(String pkg);

        boolean matchesJar(String name);
    }

    InternalScanner(ClassLoader cl, PackageScanner.VersionMapping[] versionMappings, boolean debug) {
        this.classloader = cl;
        for (PackageScanner.VersionMapping mapping : versionMappings)
        {
            mapping.toVersion(versionConverter.getVersion(mapping.getVersion()));
        }
        this.versionMappings = versionMappings;
        this.debug = debug;
    }

    void setOsgiVersionConverter(OsgiVersionConverter converter) {
        this.versionConverter = converter;
    }

    Collection<ExportPackage> findInPackages(Test test, String... roots) {
        // ExportPackageListBuilder weans out duplicates with some smarts
        ExportPackageListBuilder exportPackageListBuilder = new ExportPackageListBuilder();
        for (String pkg : roots) {
            for (ExportPackage export : findInPackage(test, pkg)) {
                exportPackageListBuilder.add(export);
            }
        }

        // returns the packages sorted by name
        return exportPackageListBuilder.getPackageList();
    }

    Collection<ExportPackage> findInUrls(Test test, URL... urls) {
        // ExportPackageListBuilder weans out duplicates with some smarts
        ExportPackageListBuilder exportPackageListBuilder = new ExportPackageListBuilder();
        Vector<URL> list = new Vector<URL>(Arrays.asList(urls));
        for (ExportPackage export : findInPackageWithUrls(test, "", list.elements())) {
            exportPackageListBuilder.add(export);
        }

        // returns the packages sorted by name
        return exportPackageListBuilder.getPackageList();
    }

    /**
     * Scans for classes starting at the package provided and descending into subpackages.
     * Each class is offered up to the Test as it is discovered, and if the Test returns
     * true the class is retained.
     *
     * @param test        an instance of {@link Test} that will be used to filter classes
     * @param packageName the name of the package from which to start scanning for
     *                    classes, e.g. {@code net.sourceforge.stripes}
     * @return List of packages to export.
     */
    List<ExportPackage> findInPackage(Test test, String packageName) {
        List<ExportPackage> localExports = new ArrayList<ExportPackage>();

        packageName = packageName.replace('.', '/');
        Enumeration<URL> urls;

        try {
            urls = classloader.getResources(packageName);
            // test for empty
            if (!urls.hasMoreElements())
            {
                log.warn("Unable to find any resources for package '" + packageName + "'");
            }
        }
        catch (IOException ioe) {
            log.warn("Could not read package: " + packageName);
            return localExports;
        }

        return findInPackageWithUrls(test, packageName, urls);
    }

    List<ExportPackage> findInPackageWithUrls(Test test, String packageName, Enumeration<URL> urls)
    {
        List<ExportPackage> localExports = new ArrayList<ExportPackage>();
        while (urls.hasMoreElements()) {
            try {
                URL url = urls.nextElement();
                String urlPath = url.getPath();

                // it's in a JAR, grab the path to the jar
                if (urlPath.lastIndexOf('!') > 0) {
                    urlPath = urlPath.substring(0, urlPath.lastIndexOf('!'));
                    if (urlPath.startsWith("/"))
                    {
                        urlPath = "file:" + urlPath;
                    }
                } else if (!urlPath.startsWith("file:")) {
                    urlPath = "file:"+urlPath;
                }

                log.debug("Scanning for packages in [" + urlPath + "].");
                File file = null;
                try
                {
                    URL fileURL = new URL(urlPath);
                    // only scan elements in the classpath that are local files
                    if("file".equals(fileURL.getProtocol().toLowerCase()))
                        file = new File(fileURL.toURI());
                    else
                        log.info("Skipping non file classpath element [ "+urlPath+ " ]");
                }
                catch (URISyntaxException e)
                {
                    //Yugh, this is necessary as the URL might not be convertible to a URI, so resolve it by the file path
                    file = new File(urlPath.substring("file:".length()));
                }

                if (file!=null && file.isDirectory()) {
                    localExports.addAll(loadImplementationsInDirectory(test, packageName, file));
                } else if (file!=null) {
                    if (test.matchesJar(file.getName())) {
                        localExports.addAll(loadImplementationsInJar(test, file));
                    }
                }
            }
            catch (IOException ioe) {
                log.error("could not read entries: " + ioe);
            }
        }
        return localExports;
    }


    /**
     * Finds matches in a physical directory on a filesystem.  Examines all
     * files within a directory - if the File object is not a directory, and ends with <i>.class</i>
     * the file is loaded and tested to see if it is acceptable according to the Test.  Operates
     * recursively to find classes within a folder structure matching the package structure.
     *
     * @param test     a Test used to filter the classes that are discovered
     * @param parent   the package name up to this directory in the package hierarchy.  E.g. if
     *                 /classes is in the classpath and we wish to examine files in /classes/org/apache then
     *                 the values of <i>parent</i> would be <i>org/apache</i>
     * @param location a File object representing a directory
     * @return List of packages to export.
     */
    List<ExportPackage> loadImplementationsInDirectory(Test test, String parent, File location) {
        log.debug("Scanning directory " + location.getAbsolutePath() + " parent: '" + parent + "'.");
        File[] files = location.listFiles();
        List<ExportPackage> localExports = new ArrayList<ExportPackage>();
        Set<String> scanned = new HashSet<String>();

        for (File file : files) {
            final String packageOrClass;
            if (parent == null || parent.length() == 0)
            {
                packageOrClass = file.getName();
            }
            else
            {
                packageOrClass = parent + "/" + file.getName();
            }

            if (file.isDirectory()) {
                localExports.addAll(loadImplementationsInDirectory(test, packageOrClass, file));

            // If the parent is empty, then assume the directory's jars should be searched
            } else if ("".equals(parent) && file.getName().endsWith(".jar") && test.matchesJar(file.getName())) {
                localExports.addAll(loadImplementationsInJar(test, file));
            } else {
                String pkg = packageOrClass;
                int lastSlash = pkg.lastIndexOf('/');
                if (lastSlash > 0) {
                    pkg = pkg.substring(0, lastSlash);
                }
                pkg = pkg.replace('/', '.');
                if (!scanned.contains(pkg)) {
                    if (test.matchesPackage(pkg)) {
                        log.debug(String.format("loadImplementationsInDirectory: [%s] %s", pkg, file));
                        localExports.add(new ExportPackage(pkg, determinePackageVersion(null, pkg), location));
                    }
                    scanned.add(pkg);
                }
            }
        }
        return localExports;
    }

    /**
     * Finds matching classes within a jar files that contains a folder structure
     * matching the package structure.  If the File is not a JarFile or does not exist a warning
     * will be logged, but no error will be raised.
     *
     * @param test    a Test used to filter the classes that are discovered
     * @param file the jar file to be examined for classes
     * @return List of packages to export.
     */
    List<ExportPackage> loadImplementationsInJar(Test test, File file) {

        List<ExportPackage> localExports = new ArrayList<ExportPackage>();
        Set<String> packages = jarContentCache.get(file.getPath());
        if (packages == null)
        {
            packages = new HashSet<String>();
            try {
                JarFile jarFile = new JarFile(file);


                for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements(); ) {
                    JarEntry entry = e.nextElement();
                    String name = entry.getName();
                    if (!entry.isDirectory()) {
                        String pkg = name;
                        int pos = pkg.lastIndexOf('/');
                        if (pos > -1) {
                            pkg = pkg.substring(0, pos);
                        }
                        pkg = pkg.replace('/', '.');
                        boolean newlyAdded = packages.add(pkg);
                        if (newlyAdded && log.isDebugEnabled())
                        {
                            // Use newlyAdded as we don't want to log duplicates
                            log.debug(String.format("Found package '%s' in jar file [%s]", pkg, file));
                        }
                     }
                }
            }
            catch (IOException ioe) {
                log.error("Could not search jar file '" + file + "' for classes matching criteria: " +
                        test + " due to an IOException" + ioe);
                return Collections.emptyList();
            }
            finally
            {
                // set the cache, even if the scan produced an error
                jarContentCache.put(file.getPath(), packages);
            }
        }

        Set<String> scanned = new HashSet<String>();
        for (String pkg : packages)
        {
            if (!scanned.contains(pkg)) {
                if (test.matchesPackage(pkg)) {
                    localExports.add(new ExportPackage(pkg, determinePackageVersion(file, pkg), file));
                }
                scanned.add(pkg);
            }
        }

        return localExports;
    }

    String determinePackageVersion(File jar, String pkg) {
        // Look for an explicit mapping
        String version = null;
        for (PackageScanner.VersionMapping mapping : versionMappings) {
            if (mapping.matches(pkg)) {
                version = mapping.getVersion();
            }
        }
        if (version == null && jar != null) {
            // TODO: Look for osgi headers

            // Try to guess the version from the jar name
            String name = jar.getName();
            version = extractVersion(name);
        }

        if (version == null && debug)
        {
            if (jar != null)
            {
                log.warn("Unable to determine version for '" + pkg + "' in jar '" + jar.getPath() + "'");
            }
            else
            {
                log.warn("Unable to determine version for '" + pkg + "'");
            }
        }

        return version;
    }

    /**
     * Tries to guess the version by assuming it starts as the first number after a '-' or '_' sign, then converts
     * the version into an OSGi-compatible one.
     * @param filename the filename
     * @return The extracted version.
     */
    String extractVersion(String filename)
    {
        StringBuilder version = null;
        boolean lastWasSeparator = false;
        for (int x=0; x<filename.length(); x++)
        {
            char c = filename.charAt(x);
            if (c == '-' || c == '_')
                lastWasSeparator = true;
            else
            {
                if (Character.isDigit(c) && lastWasSeparator && version == null)
                    version = new StringBuilder();
                lastWasSeparator = false;
            }

            if (version != null)
                version.append(c);
        }

        if (version != null)
        {
            if (".jar".equals(version.substring(version.length() - 4)))
                version.delete(version.length() - 4, version.length());
            return versionConverter.getVersion(version.toString());
        } else
            return null;
    }
}
