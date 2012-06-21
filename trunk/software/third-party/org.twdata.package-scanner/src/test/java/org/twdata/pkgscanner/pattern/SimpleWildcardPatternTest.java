package org.twdata.pkgscanner.pattern;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 24/05/2008
 * Time: 8:38:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleWildcardPatternTest extends TestCase {

    public void testMatches() {
        assertTrue(new SimpleWildcardPattern("foo").matches("foo"));
        assertFalse(new SimpleWildcardPattern("foo").matches("foobar"));
        assertTrue(new SimpleWildcardPattern("foo*").matches("foobar"));
        assertTrue(new SimpleWildcardPattern("foo.*").matches("foo.bar"));
        assertFalse(new SimpleWildcardPattern("foo.*").matches("foobar"));
    }
}
