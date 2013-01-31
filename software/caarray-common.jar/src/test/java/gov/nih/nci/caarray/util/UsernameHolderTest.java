//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.security.SecurityUtils;

import org.junit.Test;

/**
 * Tests username holder.
 */
public class UsernameHolderTest extends AbstractCaarrayTest {

    @Test
    public void testHolder() {
        UsernameHolder.setUser(null);
        assertEquals(SecurityUtils.ANONYMOUS_USERNAME, UsernameHolder.getUser());
        UsernameHolder.setUser("test");
        assertEquals("test", UsernameHolder.getUser());
    }
}
