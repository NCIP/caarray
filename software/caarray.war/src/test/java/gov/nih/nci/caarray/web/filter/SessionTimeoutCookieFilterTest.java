//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.filter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author wcheng
 *
 */
public class SessionTimeoutCookieFilterTest {
    private static final int SESSION_TIMEOUT = 30;
    
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
    @Mock private FilterChain filterChain;
    private SessionTimeoutCookieFilter filter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(request.getSession()).thenReturn(session);
        when(session.getMaxInactiveInterval()).thenReturn(SESSION_TIMEOUT);
        this.filter = new SessionTimeoutCookieFilter();
    }

    @Test
    public void testFilterLoggedIn() throws Exception {
        when(request.getRemoteUser()).thenReturn("username");
        filter.doFilter(request, response, filterChain);
        
        ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(2)).addCookie(captor.capture());
        List<Cookie> cookies = captor.getAllValues();
        Cookie serverTime = cookies.get(0);
        Cookie sessionExpiry = cookies.get(1);
        assertEquals("serverTime", serverTime.getName());
        assertEquals("sessionExpiry", sessionExpiry.getName());
        assertEquals(Long.parseLong(sessionExpiry.getValue()),
                Long.parseLong(serverTime.getValue()) + SESSION_TIMEOUT*1000);
    }

    @Test
    public void testFilterLoggedOut() throws Exception {
        filter.doFilter(request, response, filterChain);
        
        ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(2)).addCookie(captor.capture());
        List<Cookie> cookies = captor.getAllValues();
        Cookie serverTime = cookies.get(0);
        Cookie sessionExpiry = cookies.get(1);
        assertEquals("serverTime", serverTime.getName());
        assertEquals("sessionExpiry", sessionExpiry.getName());
        assertEquals(serverTime.getValue(), sessionExpiry.getValue());
    }
}
