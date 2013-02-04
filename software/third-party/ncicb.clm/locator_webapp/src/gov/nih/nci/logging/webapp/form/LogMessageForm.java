//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.logging.webapp.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/** 
 * Creation date: 08-31-2006
 */
public class LogMessageForm extends ActionForm {

	

	private java.util.Set logMessages;
	
	/** 
	 * Method validate
	 * @param mapping
	 * @param request
	 * @return ActionErrors
	 */
	public ActionErrors validate(
		ActionMapping mapping,
		HttpServletRequest request) {

		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 * Method reset
	 * @param mapping
	 * @param request
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {

		// TODO Auto-generated method stub
	}


	
	/**
	 * @return Returns the logMessages.
	 */
	public java.util.Set getLogMessages()
	{
		return logMessages;
	}

	
	/**
	 * @param logMessages The logMessages to set.
	 */
	public void setLogMessages(java.util.Set logMessages)
	{
		this.logMessages = logMessages;
	}

}

