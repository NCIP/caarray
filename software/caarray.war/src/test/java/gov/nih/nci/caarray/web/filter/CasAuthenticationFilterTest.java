//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.filter;

import gov.nih.nci.caarray.AbstractCaarrayTest;

import org.jasig.cas.client.authentication.DefaultGatewayResolverImpl;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.AssertionImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.FilterChain;
import java.net.URLEncoder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the CasCasAuthenticationFilter. These tests were copied from the original JASIG AuthenticatorFilterTests
 * included in the cas-client-core library. They were duplicated due to the necessary duplication of the
 * Filter class and have as such been improved to test new functionality as well as improve the structure
 * of the class.
 *
 * Remove this class when working on ARRAY-2516
 */
public final class CasAuthenticationFilterTest extends AbstractCaarrayTest {

    private static final String CAS_SERVICE_URL = "https://localhost:8443/service";

    private static final String CAS_LOGIN_URL = "https://localhost:8443/cas/login";

    private CasAuthenticationFilter filter;

    final MockHttpSession session = new MockHttpSession();
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final FilterChain filterChain = mock(FilterChain.class);

    @Before
    public void setUp() throws Exception {
        this.filter = new CasAuthenticationFilter();
        final MockFilterConfig config = new MockFilterConfig();
        config.addInitParameter("casServerLoginUrl", CAS_LOGIN_URL);
        config.addInitParameter("service", "https://localhost:8443/service");
        request.setSession(session);
        this.filter.init(config);
    }

    @After
    public void tearDown() throws Exception {
        this.filter.destroy();
    }

    @Test
    public void testRedirect() throws Exception {
        this.filter.doFilter(request, response, filterChain);

        assertEquals(CAS_LOGIN_URL + "?service=" + URLEncoder.encode(CAS_SERVICE_URL, "UTF-8"),
                response.getRedirectedUrl());
    }

    @Test
    public void testRedirectWithQueryString() throws Exception {
        request.setQueryString("test=12456");
        request.setRequestURI("/test");
        request.setSecure(true);
        this.filter = new CasAuthenticationFilter();

        final MockFilterConfig config = new MockFilterConfig();
        config.addInitParameter("casServerLoginUrl", CAS_LOGIN_URL);
        config.addInitParameter("serverName", "localhost:8443");
        this.filter.init(config);

        this.filter.doFilter(request, response, filterChain);

        assertEquals(
                CAS_LOGIN_URL
                        + "?service="
                        + URLEncoder.encode("https://localhost:8443" + request.getRequestURI() + "?"
                                + request.getQueryString(), "UTF-8"), response.getRedirectedUrl());
    }

    @Test
    public void testAssertion() throws Exception {
        session.setAttribute(AbstractCasFilter.CONST_CAS_ASSERTION, new AssertionImpl("test"));
        this.filter.doFilter(request, response, filterChain);

        assertNull(response.getRedirectedUrl());
    }

    @Test
    public void testRenew() throws Exception {
        this.filter.setRenew(true);
        this.filter.doFilter(request, response, filterChain);

        assertNotNull(response.getRedirectedUrl());
        assertTrue(response.getRedirectedUrl().indexOf("renew=true") != -1);
    }

    @Test
    public void testGateway() throws Exception {
        this.filter.setRenew(true);
        this.filter.setGateway(true);
        this.filter.doFilter(request, response, filterChain);
        assertNotNull(session.getAttribute(DefaultGatewayResolverImpl.CONST_CAS_GATEWAY));
        assertNotNull(response.getRedirectedUrl());

        final MockHttpServletResponse response2 = new MockHttpServletResponse();
        this.filter.doFilter(request, response2, filterChain);
        assertNull(session.getAttribute(DefaultGatewayResolverImpl.CONST_CAS_GATEWAY));
        assertNull(response2.getRedirectedUrl());
    }

    @Test
    public void testInitExcludeURL_SinglePattern() throws Exception {
        final MockFilterConfig config = new MockFilterConfig();
        config.addInitParameter("casServerLoginUrl", CAS_LOGIN_URL);
        config.addInitParameter("serverName", "localhost:8443");
        config.addInitParameter(CasAuthenticationFilter.EXCLUDE_PARAMETERS_INIT_PARAM, "*\\.action");
        this.filter.init(config);

        assertNotNull(filter.getExcludePatterns());
        assertEquals("Expected a single pattern but received: " + filter.getExcludePatterns(), 1,
                filter.getExcludePatterns().length);
    }

    @Test
    public void testInitExcludeURL_MultiplePattern() throws Exception {
        final MockFilterConfig config = new MockFilterConfig();
        config.addInitParameter("casServerLoginUrl", CAS_LOGIN_URL);
        config.addInitParameter("serverName", "localhost:8443");
        config.addInitParameter(CasAuthenticationFilter.EXCLUDE_PARAMETERS_INIT_PARAM, ".*\\.action,.*/ajax/.*");
        this.filter.init(config);

        assertNotNull(filter.getExcludePatterns());
        assertEquals("Expected a two patterns but received: " + filter.getExcludePatterns(), 2,
                filter.getExcludePatterns().length);
    }

    @Test
    public void testExcludedUrl_SingleException() throws Exception {
        request.setServletPath("/ajax/details.action");
        filter.setExcludePatterns(new String[]{".*/ajax/.*"});
        filter.doFilter(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testExcludedUrl_MultipleException() throws Exception {
        request.setServletPath("/ajax/details.action");
        filter.setExcludePatterns(new String[]{".*\\.jsp", ".*/ajax/.*"});
        filter.doFilter(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testNotExcludedUrl() throws Exception {
        request.setServletPath("/ajax/details.action");
        filter.setExcludePatterns(new String[]{".*\\.jsp", ".*/protected/.*"});
        filter.doFilter(request, response, filterChain);
        verify(filterChain, never()).doFilter(request, response);
    }

}
