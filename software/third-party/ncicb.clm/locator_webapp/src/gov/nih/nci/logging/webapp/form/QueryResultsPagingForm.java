//Created by MyEclipse Struts
// XSL source (default): platform:/plugin/com.genuitec.eclipse.cross.easystruts.eclipse_4.0.0/xslt/JavaClass.xsl

package gov.nih.nci.logging.webapp.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/** 
 * Creation date: 08-31-2006
 */
public class QueryResultsPagingForm extends ActionForm {

	private String pageSize;
	private String targetPageNumber;


	
	
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
	 * @return Returns the pageSize.
	 */
	public String getPageSize()
	{
		return pageSize;
	}

	
	/**
	 * @param pageSize The pageSize to set.
	 */
	public void setPageSize(String pageSize)
	{
		this.pageSize = pageSize;
	}

	
	/**
	 * @return Returns the targetPageNumber.
	 */
	public String getTargetPageNumber()
	{
		return targetPageNumber;
	}

	
	/**
	 * @param targetPageNumber The targetPageNumber to set.
	 */
	public void setTargetPageNumber(String targetPageNumber)
	{
		this.targetPageNumber = targetPageNumber;
	}


	
	
}

