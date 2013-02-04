//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.filter;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.caarray.web.action.registration.UserRole;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.tuckey.web.MockChain;

/**
 * Tests for web user filter.
 */
public class UserFilterTest extends AbstractCaarrayTest {

    @Test
    public void testFilter() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteUser("test");
        request.addUserRole(UserRole.LAB_SCIENTIST.getRoleName());
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockChain chain = new MockChain();
        UserFilter uf = new UserFilter();
        uf.init(null);
        uf.doFilter(request, response, chain);
        assertEquals("test", CaArrayUsernameHolder.getUser());
        uf.destroy();
    }
}
