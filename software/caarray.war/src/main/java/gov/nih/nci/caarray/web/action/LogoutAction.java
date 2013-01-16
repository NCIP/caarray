//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action;

import org.apache.struts2.ServletActionContext;

/**
 * @author wcheng
 *
 */
public class LogoutAction extends com.fiveamsolutions.nci.commons.web.struts2.action.LogoutAction {
    private final String casServerLogoutUrl;
    
    /**
     * Constructor.
     */
    public LogoutAction() {
        casServerLogoutUrl = ServletActionContext.getServletContext().getInitParameter("casServerLogoutUrl");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String logout() {
        String result = super.logout();
        return (casServerLogoutUrl != null) ? "casLogout" : result;
    }

    /**
     * @return the casServerLogoutUrl
     */
    public String getCasServerLogoutUrl() {
        return casServerLogoutUrl;
    }
}
