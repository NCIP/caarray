package org.twdata.pkgscanner;

import junit.framework.TestCase;

import java.util.Collection;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.twdata.pkgscanner.PackageScanner.*;

public class PackageScannerTest extends TestCase
{
    public void testPerformance()
    {
        new PackageScanner()
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
        
        long start = System.currentTimeMillis();
        for (int x=0; x<100; x++)
        {
            new PackageScanner()
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
                                        "foo.*",
                                        "com.*",
                                        "javax.*",
                                        "org.twdata.pkgscanner.*"),

                                exclude(
                                        "com.intellij.*")))
                .withMappings(
                        mapPackage("org.twdata.pkgscanner.foo").toVersion("2.0.4"))
                .scan();
        }
        long end = System.currentTimeMillis();

        System.out.println("Total: "+(end-start)+"ms, each: "+((end-start)/100)+"ms");
    }

}
