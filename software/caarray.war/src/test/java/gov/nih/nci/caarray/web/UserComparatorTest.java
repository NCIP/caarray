//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.web.util.UserComparator;

import org.displaytag.model.Cell;
import org.junit.Test;

/**
 * Test cases for comparator.
 */
@SuppressWarnings("PMD")
public class UserComparatorTest extends AbstractCaarrayTest {

    @Test
    public void testComparator() {
        final UserComparator uc = new UserComparator();
        final String prefix = "<a href=\"example.action\">";
        final String suffix = "</a>";
        String s1 = prefix + "last, first" + suffix;
        String[] vals = UserComparator.getNames(s1);
        assertEquals("LAST", vals[0]);
        assertEquals("FIRST", vals[1]);

        String s2 = prefix + " last<> ,first<> " + suffix;
        vals = UserComparator.getNames(s2);
        assertEquals("LAST<>", vals[0]);
        assertEquals("FIRST<>", vals[1]);

        Cell c1 = new Cell(s1);
        Cell c2 = new Cell(s2);
        assertEquals(0, uc.compare(c1, c1));
        assertTrue(uc.compare(c1, c2) < 0);

        s2 = prefix + "last2, first" + suffix;
        c2 = new Cell(s2);
        assertTrue(uc.compare(c1, c2) < 0);

        s2 = prefix + "a, first" + suffix;
        c2 = new Cell(s2);
        assertTrue(uc.compare(c1, c2) > 0);

        s2 = prefix + "last, a" + suffix;
        c2 = new Cell(s2);
        assertTrue(uc.compare(c1, c2) > 0);
    }
}
