//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web;

import java.util.Map;

import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.util.StrutsTestCaseHelper;
import org.junit.After;
import org.junit.Before;

import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.XWorkTestCaseHelper;

/**
 * Test case that sets up some struts framework stuff.
 * @author Scott Miller
 */
public abstract class AbstractBaseStrutsTest {

    protected ConfigurationManager configurationManager;
    protected Configuration configuration;
    protected Container container;
    protected ActionProxyFactory actionProxyFactory;

    @Before
    public void setUpStruts() {
        initDispatcher(null);
        this.actionProxyFactory = this.container.getInstance(ActionProxyFactory.class);
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

    protected Dispatcher initDispatcher(Map<String, String> params) {
        Dispatcher du = StrutsTestCaseHelper.initDispatcher(params);
        this.configurationManager = du.getConfigurationManager();
        this.configuration = this.configurationManager.getConfiguration();
        this.container = this.configuration.getContainer();
        return du;
    }
}
