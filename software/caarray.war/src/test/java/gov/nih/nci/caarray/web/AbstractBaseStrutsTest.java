//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import gov.nih.nci.caarray.AbstractCaarrayTest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.util.StrutsTestCaseHelper;
import org.junit.After;
import org.junit.Before;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.providers.XWorkConfigurationProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.XWorkTestCaseHelper;

/**
 * Test case that sets up some struts framework stuff.
 * @author Scott Miller
 */
public abstract class AbstractBaseStrutsTest extends AbstractCaarrayTest {

    protected ConfigurationManager configurationManager;
    protected Configuration configuration;
    protected Container container;
    protected ActionProxyFactory actionProxyFactory;
    protected MockHttpServletResponse mockResponse;

    @Before
    public void setUpStruts() {
        this.configurationManager = new ConfigurationManager();
        this.configurationManager.addContainerProvider(new XWorkConfigurationProvider());
        this.configuration = configurationManager.getConfiguration();
        this.container = this.configuration.getContainer();

        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();
        stack.getContext().put(ActionContext.CONTAINER, container);
        ActionContext.setContext(new ActionContext(stack.getContext()));

        assertNotNull(ActionContext.getContext());
        this.actionProxyFactory = this.container.getInstance(ActionProxyFactory.class);
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(new MockHttpSession());
        ServletActionContext.setRequest(request);
        this.mockResponse = new MockHttpServletResponse();
        ServletActionContext.setResponse(this.mockResponse);
        
        ServletActionContext.getRequest().getSession().setAttribute("messages", new ArrayList<String>());
    }
    
    @After
    @SuppressWarnings("PMD")
    public void tearDownStruts() throws Exception {
        XWorkTestCaseHelper.tearDown(this.configurationManager);
        this.configurationManager = null;
        this.configuration = null;
        this.container = null;
        this.actionProxyFactory = null;
        StrutsTestCaseHelper.tearDown();
    }
}
