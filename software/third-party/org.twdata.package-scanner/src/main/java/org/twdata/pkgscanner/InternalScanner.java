package org.twdata.pkgscanner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Does the actual work of scanning the classloader
 */
class InternalScanner {
    private final Logger log = LoggerFactory.getLogger(InternalScanner.class);
    private final Map<String,Set<String>> jarContentCache = new HashMap<String,Set<String>>();
    private final ClassLoader classloader;
    private final PackageScanner.VersionMapping[] versionMappings;
    private OsgiVersionConverter versionConverter = new DefaultOsgiVersionConverter();
    private final boolean debug;

    static interface Test {
        boolean matchesPackage(String pkg);

        boolean matchesJar(String name);
    }

    InternalScanner(ClassLoader cl, PackageScanner.VersionMapping[] versionMappings, boolean debug) {
        this.classloader = cl;
        for (final PackageScanner.VersionMapping mapping : versionMappings)
        {
            mapping.toVersion(this.versionConverter.getVersion(mapping.getVersion()));
        }
        this.versionMappings = versionMappings;
        this.debug = debug;
    }

    void setOsgiVersionConverter(OsgiVersionConverter converter) {
        this.versionConverter = converter;
    }

    Collection<ExportPackage> findInPackages(Test test, String... roots) {
        // ExportPackageListBuilder weans out duplicates with some smarts
        final ExportPackageListBuilder exportPackageListBuilder = new ExportPackageListBuilder();
        for (final String pkg : roots) {
            for (final ExportPackage export : findInPackage(test, pkg)) {
                exportPackageListBuilder.add(export);
            }
        }

        // returns the packages sorted by name
        return exportPackageListBuilder.getPackageList();
    }

    Collection<ExportPackage> findInUrls(Test test, URL... urls) {
        // ExportPackageListBuilder weans out duplicates with some smarts
        final ExportPackageListBuilder exportPackageListBuilder = new ExportPackageListBuilder();
        final Vector<URL> list = new Vector<URL>(Arrays.asList(urls));
        for (final ExportPackage export : findInPackageWithUrls(test, "", list.elements())) {
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
        final List<ExportPackage> localExports = new ArrayList<ExportPackage>();

        packageName = packageName.replace('.', '/');
        Enumeration<URL> urls;

        try {
            urls = this.classloader.getResources(packageName);
            // test for empty
            if (!urls.hasMoreElements())
            {
                this.log.warn("Unable to find any resources for package '" + packageName + "'");
            }
        }
        catch (final IOException ioe) {
            this.log.warn("Could not read package: " + packageName);
            return localExports;
        }

        return findInPackageWithUrls(test, packageName, urls);
    }

    List<ExportPackage> findInPackageWithUrls(Test test, String packageName, Enumeration<URL> urls)
    {
        final List<ExportPackage> localExports = new ArrayList<ExportPackage>();

        final String tempDirName = new UID().toString().replace(':', '_').replace('-', '_');
        final File tempDir = new File(new File(System.getProperty("java.io.tmpdir")), tempDirName);
        if (!tempDir.mkdirs()) {
            throw new IllegalStateException("Couldn't create directory: " + tempDir.getAbsolutePath());
        }

        while (urls.hasMoreElements()) {
            try {
                final URL url = urls.nextElement();
                String urlProtocol = url.getProtocol();
                String urlPath = url.getPath();

                log.debug("url = " + url.toString());
                log.debug("urlProtocol = " + urlProtocol);
                log.debug("Initial urlPath = " + urlPath);

                /*
                 *  For any urls that match file:/path, replace file: with file://. Example below,
                 *  url = jar:file:/C:/apps/local_install/jboss-5.1.0.GA-nci/lib/jboss-classloader.jar!/org
                 *  urlProtocol = jar
                 *  Initial urlPath = file:/C:/apps/local_install/jboss-5.1.0.GA-nci/lib/jboss-classloader.jar!/org
                 */
                urlPath = urlPath.replace("file:", "file://");
                log.debug("After replacing with file://,  urlPath = " + urlPath);

                // special handling for jboss VFS - we cache the jar to a local copy, use it to do the scanning
                // as usual then discard it.
                // it's somewhat inefficient, but requires less rewriting of other parts of the code which make
                // assumptions that a JAR can be accessed as a file.
                // should consider eventually rewriting this to use jboss-vfs API directly for jar inspection
                // a possible starting point is http://community.jboss.org/message/8432
                if (urlProtocol.startsWith("vfs") && urlPath.contains(".jar")) {
                    // Example:  url = vfszip:/C:/apps/local_install/jboss-5.1.0.GA-nci/lib/jboss-deployers-spi.jar/org/
                    final String pathToJar = urlPath.substring(0, urlPath.lastIndexOf(".jar") + 4);
                    final String jarName = pathToJar.substring(pathToJar.lastIndexOf("/") + 1, pathToJar.length());
                    log.debug("pathToJar = " + pathToJar);
                    log.debug("jarName = " + jarName);

                    final File tmp = new File(tempDir, jarName);
                    tmp.deleteOnExit();

                    final URL jarUrl = new URL(url.getProtocol() + ":" + pathToJar);
                    FileUtils.copyURLToFile(jarUrl, tmp);
                    // append the file:// to the files absolute path, to make it a valid url.
                    urlPath = "file://" + tmp.getAbsolutePath();
                } else if (urlPath.lastIndexOf('!') > 0) {
                    // it's in a JAR, grab the path to the jar
                    urlPath = urlPath.substring(0, urlPath.lastIndexOf('!'));
                    if (urlPath.startsWith("/"))
                    {
                        urlPath = "file://" + urlPath;
                    }
                } else if (!urlPath.startsWith("file:")) {
                    urlPath = "file://"+urlPath;
                }

                this.log.debug("Scanning for packages in [" + urlPath + "].");
                File file = null;
                final URL fileURL = new URL(urlPath);
                // only scan elements in the classpath that are local files
                if("file".equals(fileURL.getProtocol().toLowerCase())) {
                    log.debug("Protocol = file");
                    String fileName = urlPath.substring("file://".length());
                    // replace any %20 in the filename with spaces.
                    fileName = fileName.replaceAll("%20", " ");
                    log.debug("fileName = " + fileName);
                    file = new File(fileName);
                    log.debug("absolute file path = " + file.getAbsolutePath());

                } else {
                    this.log.debug("Skipping non file classpath element [ "+urlPath+ " ]");
                }

                if (file!=null && file.isDirectory()) {
                    localExports.addAll(loadImplementationsInDirectory(test, packageName, file));
                } else if (file!=null) {
                    if (test.matchesJar(file.getName())) {
                        localExports.addAll(loadImplementationsInJar(test, file));
                    }
                }
            }
            catch (final IOException ioe) {
                this.log.error("could not read entries: " + ioe);
            }
        }
        FileUtils.deleteQuietly(tempDir);
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
        this.log.debug("Scanning directory " + location.getAbsolutePath() + " parent: '" + parent + "'.");
        final File[] files = location.listFiles();
        final List<ExportPackage> localExports = new ArrayList<ExportPackage>();
        final Set<String> scanned = new HashSet<String>();

        for (final File file : files) {
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
                final int lastSlash = pkg.lastIndexOf('/');
                if (lastSlash > 0) {
                    pkg = pkg.substring(0, lastSlash);
                }
                pkg = pkg.replace('/', '.');
                if (!scanned.contains(pkg)) {
                    if (test.matchesPackage(pkg)) {
                        this.log.debug(String.format("loadImplementationsInDirectory: [%s] %s", pkg, file));
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

        final List<ExportPackage> localExports = new ArrayList<ExportPackage>();
        Set<String> packages = this.jarContentCache.get(file.getPath());
        if (packages == null)
        {
            packages = new HashSet<String>();
            try {
                final JarFile jarFile = new JarFile(file);


                for (final Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements(); ) {
                    final JarEntry entry = e.nextElement();
                    final String name = entry.getName();
                    if (!entry.isDirectory()) {
                        String pkg = name;
                        final int pos = pkg.lastIndexOf('/');
                        if (pos > -1) {
                            pkg = pkg.substring(0, pos);
                        }
                        pkg = pkg.replace('/', '.');
                        final boolean newlyAdded = packages.add(pkg);
                        if (newlyAdded && this.log.isDebugEnabled())
                        {
                            // Use newlyAdded as we don't want to log duplicates
                            this.log.debug(String.format("Found package '%s' in jar file [%s]", pkg, file));
                        }
                    }
                }
            }
            catch (final IOException ioe) {
                this.log.error("Could not search jar file '" + file + "' for classes matching criteria: " +
                        test + " due to an IOException" + ioe);
                return Collections.emptyList();
            }
            finally
            {
                // set the cache, even if the scan produced an error
                this.jarContentCache.put(file.getPath(), packages);
            }
        }

        final Set<String> scanned = new HashSet<String>();
        for (final String pkg : packages)
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
        for (final PackageScanner.VersionMapping mapping : this.versionMappings) {
            if (mapping.matches(pkg)) {
                version = mapping.getVersion();
            }
        }
        if (version == null && jar != null) {
            // TODO: Look for osgi headers

            // Try to guess the version from the jar name
            final String name = jar.getName();
            version = extractVersion(name);
        }

        if (version == null && this.debug)
        {
            if (jar != null)
            {
                this.log.warn("Unable to determine version for '" + pkg + "' in jar '" + jar.getPath() + "'");
            }
            else
            {
                this.log.warn("Unable to determine version for '" + pkg + "'");
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
            final char c = filename.charAt(x);
            if (c == '-' || c == '_') {
                lastWasSeparator = true;
            } else
            {
                if (Character.isDigit(c) && lastWasSeparator && version == null) {
                    version = new StringBuilder();
                }
                lastWasSeparator = false;
            }

            if (version != null) {
                version.append(c);
            }
        }

        if (version != null)
        {
            if (".jar".equals(version.substring(version.length() - 4))) {
                version.delete(version.length() - 4, version.length());
            }
            return this.versionConverter.getVersion(version.toString());
        } else {
            return null;
        }
    }
}
