package gov.nih.nci.logging.webapp.action;

import gov.nih.nci.logging.webapp.util.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


public class HomeAction extends Action
{

	
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{
		
		HttpSession session = request.getSession();
		if (session.isNew() || (session.getAttribute(Constants.LOGIN_OBJECT) == null)) {
			
			return mapping.findForward(Constants.FORWARD_PUBLIC_LOGIN);
		}
		/*
		 * clear the junk in the session here
		 */
		
		session.removeAttribute(Constants.CURRENT_FORM);
		

		
		return mapping.findForward(Constants.FORWARD_HOME);
	}

}
