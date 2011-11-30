//Created by MyEclipse Struts
// XSL source (default): platform:/plugin/com.genuitec.eclipse.cross.easystruts.eclipse_4.0.0/xslt/JavaClass.xsl

package gov.nih.nci.logging.webapp.action;

import gov.nih.nci.logging.webapp.form.LoginForm;
import gov.nih.nci.logging.webapp.util.Constants;
import gov.nih.nci.logging.webapp.util.SecurityManager;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

/**
 * Creation date: 08-18-2006
 */
public class LoginAction extends Action
{

	/**
	 * Method execute
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		LoginForm loginForm = (LoginForm) form;

		
		ActionErrors errors = new ActionErrors();
		ActionMessages messages = new ActionMessages();
		
		errors.clear();
		try
		{
			if (isAuthenticated(request, loginForm))
			{
				// Perform Authentication here.
				HttpSession session = request.getSession(true);
				session.setAttribute(Constants.LOGIN_OBJECT, form);
				session.setAttribute(Constants.APPLICATION_NAME, loginForm.getApplication());
				loadProtectedAttributes(request,loginForm);
				return (mapping.findForward(Constants.FORWARD_HOME));
			}
			
			
		}
		catch (Exception ex)
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(Constants.ERROR_ID, ex.getMessage()));
			saveErrors(request, errors);
		}
		return mapping.findForward(Constants.FORWARD_PUBLIC_LOGIN);

	}

	private boolean isAuthenticated(HttpServletRequest request, LoginForm loginForm) throws Exception
	{
		boolean loginResult = SecurityManager.login(loginForm.getLoginID(),loginForm.getPassword());
		if (false == loginResult)
		{
			throw new Exception("Invalid User Credentials");
		}
		else
		{
			loginResult = SecurityManager.checkPermission(request,loginForm.getApplication(),loginForm.getLoginID(),Constants.APPLICATION_NAME_ATTRIBUTE, loginForm.getApplication());
			if (false == loginResult)
			{
				throw new Exception ("User does not have access permission to view the logs for the " + loginForm.getApplication() + " Application.");
			}
		}
		return loginResult;
	}

	private void loadProtectedAttributes(HttpServletRequest request, LoginForm loginForm)
	{
		HashMap protectedAttributes = SecurityManager.loadProtectedAttributes(loginForm.getLoginID());
		
		request.getSession().setAttribute(Constants.PROTECTED_ATTRIBUTES,protectedAttributes);
	}

}
