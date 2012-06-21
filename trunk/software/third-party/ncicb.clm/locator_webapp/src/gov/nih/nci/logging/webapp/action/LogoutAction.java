package gov.nih.nci.logging.webapp.action;

import gov.nih.nci.logging.webapp.util.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


public class LogoutAction extends Action {

		/** 
		 * Method execute
		 * @param mapping
		 * @param form
		 * @param request
		 * @param response
		 * @return ActionForward
		 */
		public ActionForward execute(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) {
			
			HttpSession session = request.getSession();
			session.invalidate();
			
			return mapping.findForward(Constants.FORWARD_PUBLIC_LOGIN);
		}

}
