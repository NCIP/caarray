package org.twdata.pkgscanner;

import junit.framework.TestCase;

import java.util.List;
import java.io.File;

public class ExportPackageListBuilderTest extends TestCase {

    public void testAddBothVersionsUnknown() throws Exception {
        ExportPackageListBuilder builder = new ExportPackageListBuilder();

        builder.add(new ExportPackage("org.foo", null, new File("/stuff/foobar.jar")));
        builder.add(new ExportPackage("org.foo", null, new File("/stuff/baz.jar")));

        assertEquals(1, builder.getPackageList().size());
        assertEquals(new ExportPackage("org.foo", null, new File("/stuff/foobar.jar")), builder.getPackageList().get(0));
    }

    public void testAddUnknownThenKnownVersion() throws Exception {
        ExportPackageListBuilder builder = new ExportPackageListBuilder();

        builder.add(new ExportPackage("org.foo", null, new File("/stuff/foobar.jar")));
        builder.add(new ExportPackage("org.foo", "1.0", new File("/stuff/baz-1.0.jar")));

        assertEquals(1, builder.getPackageList().size());
        assertEquals(new ExportPackage("org.foo", "1.0", new File("/stuff/baz-1.0.jar")), builder.getPackageList().get(0));
    }

    public void testAddKnownThenUnknownVersion() throws Exception {
        ExportPackageListBuilder builder = new ExportPackageListBuilder();

        builder.add(new ExportPackage("org.foo", "1.0", new File("/stuff/baz-1.0.jar")));
        builder.add(new ExportPackage("org.foo", null, new File("/stuff/foobar.jar")));

        assertEquals(1, builder.getPackageList().size());
        assertEquals(new ExportPackage("org.foo", "1.0", new File("/stuff/baz-1.0.jar")), builder.getPackageList().get(0));
    }

    public void testAddSameVersion() throws Exception {
        ExportPackageListBuilder builder = new ExportPackageListBuilder();

        builder.add(new ExportPackage("org.foo", "1.0", new File("/stuff/foobar-1.0.jar")));
        builder.add(new ExportPackage("org.foo", "1.0", new File("/stuff/baz-1.0.jar")));

        assertEquals(1, builder.getPackageList().size());
        assertEquals(new ExportPackage("org.foo", "1.0", new File("/stuff/foobar-1.0.jar")), builder.getPackageList().get(0));
    }

    public void testAddDifferentVersion() throws Exception {
        ExportPackageListBuilder builder = new ExportPackageListBuilder();

        builder.add(new ExportPackage("org.foo", "1.1", new File("/stuff/foobar.jar")));
        builder.add(new ExportPackage("org.foo", "1.0", new File("/stuff/baz-1.0.jar")));

        assertEquals(1, builder.getPackageList().size());
        // Should take the last one
        assertEquals(new ExportPackage("org.foo", "1.0", new File("/stuff/baz-1.0.jar")), builder.getPackageList().get(0));
    }

    public void testListOrder() throws Exception {
        ExportPackageListBuilder builder = new ExportPackageListBuilder();

        builder.add(new ExportPackage("org.foo.carrot", "1.0", new File("/stuff/foobar.jar")));
        builder.add(new ExportPackage("org.foo", "2.0", new File("/stuff/foobar.jar")));
        builder.add(new ExportPackage("org.foo.apple", "1.1", new File("/stuff/foobar.jar")));
        builder.add(new ExportPackage("org.foo.banana", "1.0", new File("/stuff/foobar.jar")));

        List<ExportPackage> packageList = builder.getPackageList();
        assertEquals(4, packageList.size());
        // Should be in alphabetical order
        assertEquals(new ExportPackage("org.foo", "2.0", new File("/stuff/foobar.jar")), packageList.get(0));
        assertEquals(new ExportPackage("org.foo.apple", "1.1", new File("/stuff/foobar.jar")), packageList.get(1));
        assertEquals(new ExportPackage("org.foo.banana", "1.0", new File("/stuff/foobar.jar")), packageList.get(2));
        assertEquals(new ExportPackage("org.foo.carrot", "1.0", new File("/stuff/foobar.jar")), packageList.get(3));
    }
}
