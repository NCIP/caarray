package org.twdata.pkgscanner;

import junit.framework.TestCase;

public class DefaultOsgiVersionConverterTest extends TestCase
{

    private OsgiVersionConverter maven2Osgi = new DefaultOsgiVersionConverter();


    public void testConvertVersionToOsgi()
    {
        String osgiVersion;

        osgiVersion = maven2Osgi.getVersion( "2.1.0-SNAPSHOT" );
        assertEquals( "2.1.0.SNAPSHOT", osgiVersion );

        osgiVersion = maven2Osgi.getVersion( "2.1-SNAPSHOT" );
        assertEquals( "2.1.0.SNAPSHOT", osgiVersion );

        osgiVersion = maven2Osgi.getVersion( "2-SNAPSHOT" );
        assertEquals( "2.0.0.SNAPSHOT", osgiVersion );

        osgiVersion = maven2Osgi.getVersion( "2" );
        assertEquals( "2.0.0", osgiVersion );

        osgiVersion = maven2Osgi.getVersion( "2.1" );
        assertEquals( "2.1.0", osgiVersion );

        osgiVersion = maven2Osgi.getVersion( "2.1.3" );
        assertEquals( "2.1.3", osgiVersion );

        osgiVersion = maven2Osgi.getVersion( "2.1.3.4" );
        assertEquals( "2.1.3.4", osgiVersion );

        osgiVersion = maven2Osgi.getVersion( "4aug2000r7-dev" );
        assertEquals( "0.0.0.4aug2000r7_dev", osgiVersion );

        osgiVersion = maven2Osgi.getVersion( "1.1-alpha-2" );
        assertEquals( "1.1.0.alpha_2", osgiVersion );

        osgiVersion = maven2Osgi.getVersion( "1.0-alpha-16-20070122.203121-13" );
        assertEquals( "1.0.0.alpha_16_20070122_203121_13", osgiVersion );

        osgiVersion = maven2Osgi.getVersion( "1.0-20070119.021432-1" );
        assertEquals( "1.0.0.20070119_021432_1", osgiVersion );

        osgiVersion = maven2Osgi.getVersion( "1-20070119.021432-1" );
        assertEquals( "1.0.0.20070119_021432_1", osgiVersion );

        osgiVersion = maven2Osgi.getVersion( "1.4.1-20070217.082013-7" );
        assertEquals( "1.4.1.20070217_082013_7", osgiVersion );

        osgiVersion = maven2Osgi.getVersion( "0.0.0.4aug2000r7-dev" );
        assertEquals( "0.0.0.4aug2000r7-dev", osgiVersion );

        osgiVersion = maven2Osgi.getVersion( "4aug2000r7-dev" );
        assertEquals( "0.0.0.4aug2000r7_dev", osgiVersion );
    }

    public void testBlank()
    {
        assertEquals("0.0.0", maven2Osgi.getVersion(""));
    }

    public void testBlankComponents()
    {
        assertEquals("0.0.0", maven2Osgi.getVersion(".."));
        assertEquals("0.5.0", maven2Osgi.getVersion(".5."));
        assertEquals("0.0.0", maven2Osgi.getVersion("..."));
    }

    /*
    public void testPerformance() throws Exception
    {
        long startMillis = System.currentTimeMillis();
        for (int i=0; i<10000; i++)
        {
            maven2Osgi.getVersion("1.0-20070119.021432-1");
        }
        System.out.println("Took " + (System.currentTimeMillis() - startMillis) + " ms");
    }
    */
}
