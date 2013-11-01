//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.authentication;

import gov.nih.nci.caarray.domain.audit.AuditLogSecurity;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperFactory;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.Date;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;

import com.fiveamsolutions.nci.commons.audit.AuditLogDetail;
import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.audit.AuditType;

/**
 * This module adds audit log entries for logins and logouts.
 * @author wcheng
 */
public class AuditLogin implements LoginModule {
    private static final Long SYSADMIN_GROUP_ID = 7L;
    private static final String AUDITED_LOGIN = "AuditedLogin";
    private static final String AUDIT_LOGOUT = "AuditLogout";
    private CallbackHandler callbackHandler;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Subject subject, CallbackHandler callback, Map<String, ?> sharedState,
            Map<String, ?> options) {
        callbackHandler = callback;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean abort() throws LoginException {
        User user = new User();
        user.setUserId(0L);
        user.setLoginName(getLoginName());
        addAuditRecord("Login", "login failed", user);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean commit() throws LoginException {
        HttpSession session = getSession();
        if (session != null && session.getAttribute(AUDITED_LOGIN) == null) {
            User user = SecurityUtils.getAuthorizationManager().getUser(getLoginName());
            addAuditRecord("Login", "logged in successfully", user);
            session.setAttribute(AUDITED_LOGIN, true);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean login() throws LoginException {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean logout() throws LoginException {
        HttpSession session = getSession();
        if (session != null && session.getAttribute(AUDIT_LOGOUT) != null) {
            User user = CaArrayUsernameHolder.getCsmUser();
            addAuditRecord("Logout", "logged out", user);
        }
        return true;
    }

    private void addAuditRecord(String attribute, String message, User user) {
        AuditLogRecord record =
                new AuditLogRecord(AuditType.INSERT, "csm_user", user.getUserId(), user.getLoginName(), new Date());
        AuditLogDetail detail = new AuditLogDetail(record, attribute, null, null);
        AuditLogSecurity security = new AuditLogSecurity(AuditLogSecurity.GROUP, SYSADMIN_GROUP_ID, null, record);
        detail.setMessage(user.getLoginName() + ": " + message);
        record.getDetails().add(detail);

        Session s = null;
        try {
            CaArrayHibernateHelper hibernateHelper = CaArrayHibernateHelperFactory.getCaArrayHibernateHelper();
            s = hibernateHelper.getSessionFactory().openSession();
            s.save(record);
            s.save(security);
        } finally {
            if (s != null) {
                s.close();
            }
        }

    }
    
    private HttpSession getSession() {
        try {
            HttpServletRequest req = (HttpServletRequest) PolicyContext.getContext(HttpServletRequest.class.getName());
            return req.getSession();
        } catch (PolicyContextException e) {
            return null;
        }
    }

    private String getLoginName() {
        NameCallback nameCallback = new NameCallback("userid: ");
        try {
            callbackHandler.handle(new Callback[] {nameCallback});
        } catch (Exception e) {
            return null;
        }
        return nameCallback.getName();
    }
}
