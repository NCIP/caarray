//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
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
public class LongCacheFilterTest extends AbstractCaarrayTest {

    @Test
    public void testFilter() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockChain chain = new MockChain();
        LongCacheFilter ccf = new LongCacheFilter();
        ccf.init(null);
        ccf.doFilter(request, response, chain);


        request.setRequestURI("/index.action");
        response = new MockHttpServletResponse();
        ccf.doFilter(request, response, chain);

        assertEquals("max-age=86400", response.getHeader("Cache-Control"));

        ccf.destroy();
    }
}
