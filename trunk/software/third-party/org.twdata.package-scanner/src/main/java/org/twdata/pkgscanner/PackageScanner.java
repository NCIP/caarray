package org.twdata.pkgscanner;

import org.twdata.pkgscanner.pattern.CompiledPattern;
import org.twdata.pkgscanner.pattern.PatternFactory;
import org.twdata.pkgscanner.pattern.SimpleWildcardPatternFactory;

import java.util.*;
import java.net.URL;

/**
 * Scans the classpath for packages and tries to determine their versions
 */
public class PackageScanner {
    private Patterns packagePatterns;
    private Patterns jarPatterns;
    private ClassLoader classLoader;
    private VersionMapping[] versionMappings;
    private PatternFactory patternFactory;
    private boolean debug = false;

    /**
     * Example application that uses this class to generate an OSGi Export-Package header
     */
    public static void main(String[] args) {
        Collection<ExportPackage> exports = new PackageScanner()
                .select(
                    jars(
                            include(
                                    "*.jar",
                                    "bar-*.jar"),
                            exclude(
                                    "*dira*.jar")),
                    packages(
                            include(
                                    "org.*",
                                    "com.*",
                                    "javax.*",
                                    "org.twdata.pkgscanner.*"),

                            exclude(
                                    "com.intellij.*")))
                .withMappings(
                        mapPackage("org.twdata.pkgscanner.foo").toVersion("2.0.4"))
                .scan();

        StringBuilder sb = new StringBuilder();
        sb.append("Export-Package: \n");
        for (Iterator<ExportPackage> i = exports.iterator(); i.hasNext(); ) {
            ExportPackage pkg = i.next();
            sb.append("\t");
            sb.append(pkg.getPackageName());
            if (pkg.getVersion() != null) {
                sb.append(";version=").append(pkg.getVersion());
            }
            if (i.hasNext()) {
                sb.append(", \n");
            }
        }
        System.out.println(sb.toString());
    }

    /**
     * Constructor
     */
    public PackageScanner() {
        packagePatterns = new Patterns(new String[]{"com.*","net.*","org.*"}, new String[]{});
        jarPatterns = new Patterns(new String[]{"*"}, new String[]{});
        versionMappings = new VersionMapping[]{};
        patternFactory = new SimpleWildcardPatternFactory();
    }

    /**
     * Sets what jar and package patterns to scan
     * @param jars The jar patterns
     * @param packages The package patterns
     */
    public PackageScanner select(Patterns jars, Patterns packages) {
        this.jarPatterns = jars;
        this.packagePatterns = packages;
        return this;
    }

    /**
     * Scans the classloader as configured.
     * @return A list of discovered packages and their guessed version
     */
    public Collection<ExportPackage> scan() {
        // Initialize the pattern factories
        initPatterns();

        // Determine which packages to start from
        List<String> roots = packagePatterns.getRoots();
        InternalScanner scanner = new InternalScanner(getClassLoader(), versionMappings, debug);

        // Kick off the scanning
        Collection<ExportPackage> exports = scanner.findInPackages(new PatternTest(), roots.toArray(new String[roots.size()]));

        return exports;
    }

    /**
     * Scans the passed set of URLs.
     * @param urls A list of urls that should be scanned
     * @return A list of discovered packages and their guessed version
     */
    public Collection<ExportPackage> scan(URL... urls) {
        // Initialize the pattern factories
        initPatterns();

        // Kick off the scanning
        InternalScanner scanner = new InternalScanner(getClassLoader(), versionMappings, debug);
        Collection<ExportPackage> exports = scanner.findInUrls(new PatternTest(), urls);

        return exports;
    }

    private void initPatterns()
    {
        this.jarPatterns.setPatternFactory(patternFactory);
        this.packagePatterns.setPatternFactory(patternFactory);
        for (VersionMapping mapping : versionMappings) {
            mapping.setPatternFactory(patternFactory);
        }
    }

    // DSL methods and classes
    /**
     * Sets the classloader to scan
     * @param classLoader The classloader
     */
    public PackageScanner useClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    /**
     * Sets the explicit package-version mappings
     * @param mappings The package-version mappings
     */
    public PackageScanner withMappings(VersionMapping... mappings) {
        this.versionMappings = mappings;
        return this;
    }

    /**
     * Sets the explicit package-version mappings
     * @param mappings The package-version mappings
     */
    public PackageScanner withMappings(Map<String,String> mappings) {
        List<VersionMapping> versions = new ArrayList<VersionMapping>();
        if (mappings != null) {
            for (Map.Entry<String,String> entry : mappings.entrySet())
            {
                versions.add(new VersionMapping(entry.getKey()).toVersion(entry.getValue()));
            }
        }
        return withMappings(versions.toArray(new VersionMapping[versions.size()]));
    }

    /**
     * Enables debugging output
     * @return this
     */
    public PackageScanner enableDebug()
    {
        this.debug = true;
        return this;
    }

    /**
     * Sets the pattern factory to use
     * @param factory The pattern factory
     */
    public PackageScanner usePatternFactory(PatternFactory factory) {
        this.patternFactory = factory;
        return this;
    }

    /**
     * Creates a version mapping for the specified package
     * @param name The package name
     */
    public static VersionMapping mapPackage(String name) {
        return new VersionMapping(name);
    }

    /**
     * Sets what patterns to include
     * @param includes The included patterns
     */
    public static String[] include(String... includes) {
        return includes;
    }

    /**
     * Sets what patterns to exclude
     * @param includes The excluded patterns
     */
    public static String[] exclude(String... includes) {
        return includes;
    }

    /**
     * Sets the jar patterns to scan
     * @param includes The patterns to include
     * @param excludes The patterns to exclude
     */
    public static Patterns jars(String[] includes, String[] excludes) {
        return new Patterns(includes, excludes);
    }

    /**
     * Sets the jar patterns to scan
     * @param includes The patterns to include
     */
    public static Patterns jars(String[] includes) {
        return new Patterns(includes, new String[]{});
    }

    /**
     * Sets the package patterns to scan
     * @param includes The patterns to include
     * @param excludes The patterns to exclude
     */
    public static Patterns packages(String[] includes, String[] excludes) {
        return new Patterns(includes, excludes);
    }

    /**
     * Sets the package patterns to scan
     * @param includes The patterns to include
     */
    public static Patterns packages(String[] includes) {
        return new Patterns(includes, new String[]{});
    }

    ClassLoader getClassLoader() {
        return classLoader == null ? Thread.currentThread().getContextClassLoader() : classLoader;
    }

    /**
     * Maps a package pattern to an explicit version
     */
    public static class VersionMapping {
        private CompiledPattern compiledPattern;
        private String packagePattern;
        private String toVersion;
        private PatternFactory factory;

        /**
         * Constructs a mapping for a given package pattern
         * @param packagePattern The package pattern
         */
        public VersionMapping(String packagePattern) {
            this(packagePattern, null);
        }

        public VersionMapping(String packagePattern, String version) {
            this.packagePattern = packagePattern;
            this.toVersion = version;
        }

        void setPatternFactory(PatternFactory factory) {
            this.factory = factory;
        }

        /**
         * Sets the version to map the packages to
         * @param toVersion The version
         */
        public VersionMapping toVersion(String toVersion) {
            this.toVersion = toVersion;
            return this;
        }

        String getVersion() {
            return toVersion;
        }

        String getPackagePattern() {
            return packagePattern;
        }

        boolean matches(String pkg) {
            if (compiledPattern == null) {
                compiledPattern = factory.compile(packagePattern);
            }
            return compiledPattern.matches(pkg);
        }
    }

    /**
     * The patterns to include and exclude
     */
    public static class Patterns {

        private String[] origIncludes;
        private String[] origExcludes;

        private List<CompiledPattern> includes;
        private List<CompiledPattern> excludes;
        private PatternFactory factory;

        /**
         * Constructs a set of patterns
         * @param includes The patterns to include
         * @param excludes The patterns to exclude
         */
        public Patterns(String[] includes, String[] excludes) {
            this.origIncludes = includes;
            this.origExcludes = excludes;
        }

        void setPatternFactory(PatternFactory factory) {
            this.factory = factory;
        }

        boolean match(String val) {
            if (includes == null) {
                compilePatterns();
            }
            for (CompiledPattern ptn : includes) {
                if (ptn.matches(val)) {
                    for (CompiledPattern exptn : excludes) {
                        if (exptn.matches(val)) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        List<String> getRoots() {
            List<String> roots = new ArrayList<String>();
            for (String inc : origIncludes) {
                String root = inc;
                int starPos = root.indexOf("*");
                if (starPos > -1) {
                    int dotPos = root.lastIndexOf(".", starPos);
                    if (dotPos > -1) {
                        root = root.substring(0, dotPos);
                    }
                }
                roots.add(root);
            }
            return roots;
        }

        private void compilePatterns() {
            this.includes = new ArrayList<CompiledPattern>();
            for (String ptn : origIncludes) {
                this.includes.add(factory.compile(ptn));
            }

            this.excludes = new ArrayList<CompiledPattern>();
            for (String ptn : origExcludes) {
                this.excludes.add(factory.compile(ptn));
            }
        }
    }

    private class PatternTest implements InternalScanner.Test {
        public boolean matchesPackage(String pkg) {
            return packagePatterns.match(pkg);
        }

        public boolean matchesJar(String name) {
            return jarPatterns.match(name);
        }
    }
}
