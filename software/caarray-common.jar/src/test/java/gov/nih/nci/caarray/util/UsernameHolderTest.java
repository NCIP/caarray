//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
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
        CaArrayUsernameHolder.setUser(null);
        assertEquals(SecurityUtils.ANONYMOUS_USERNAME, CaArrayUsernameHolder.getUser());
        CaArrayUsernameHolder.setUser("test");
        assertEquals("test", CaArrayUsernameHolder.getUser());
    }
}
