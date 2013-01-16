//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.filter;

import static org.junit.Assert.*;
import gov.nih.nci.caarray.AbstractCaarrayTest;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.tuckey.web.MockChain;

/**
 * Tests for web user filter.
 */
public class NeverExpireFilterTest extends AbstractCaarrayTest {

    @Test
    public void testFilter() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockChain chain = new MockChain();
        NeverExpireFilter ccf = new NeverExpireFilter();
        ccf.init(null);
        ccf.doFilter(request, response, chain);
        assertNotNull(response.getHeader("Expires"));

        request.setRequestURI("/index.action");
        response = new MockHttpServletResponse();
        ccf.doFilter(request, response, chain);
        assertNotNull(response.getHeader("Expires"));
        assertNull(response.getHeader("Cache-control"));
        assertNull(response.getHeader("Pragma"));

        ccf.destroy();
    }
}
