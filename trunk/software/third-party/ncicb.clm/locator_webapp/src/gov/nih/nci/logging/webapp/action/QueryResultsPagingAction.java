//Created by MyEclipse Struts
// XSL source (default): platform:/plugin/com.genuitec.eclipse.cross.easystruts.eclipse_4.0.0/xslt/JavaClass.xsl

package gov.nih.nci.logging.webapp.action;

import gov.nih.nci.logging.api.applicationservice.Query;
import gov.nih.nci.logging.api.applicationservice.QueryImpl;
import gov.nih.nci.logging.api.applicationservice.SearchCriteria;
import gov.nih.nci.logging.webapp.form.QueryForm;
import gov.nih.nci.logging.webapp.form.QueryResultsPagingForm;
import gov.nih.nci.logging.webapp.util.Constants;
import gov.nih.nci.logging.webapp.util.StringUtils;
import gov.nih.nci.logging.webapp.viewobjects.SearchResultPage;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

/** 
 */
public class QueryResultsPagingAction extends Action
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

		ActionErrors errors = new ActionErrors();
		ActionMessages messages = new ActionMessages();

		HttpSession session = request.getSession();
		QueryResultsPagingForm queryResultsPagingForm = (QueryResultsPagingForm) form;

		if (session.isNew() || (session.getAttribute(Constants.LOGIN_OBJECT) == null))
		{
			return mapping.findForward(Constants.FORWARD_PUBLIC_LOGIN);
		}

		QueryForm queryForm=null;
		if(session.getAttribute(Constants.CURRENT_FORM)!=null){
			queryForm = (QueryForm) session.getAttribute(Constants.CURRENT_FORM);
			session.setAttribute(Constants.CURRENT_FORM,queryForm);
		}
		
		
		boolean success=false;
		success = performQuery(queryResultsPagingForm,queryForm,session);
		if (success)
		{
			return mapping.findForward(Constants.FORWARD_QUERY_RESULTS);
		}

		return mapping.findForward(Constants.FORWARD_QUERY_RESULTS);

	}

	
	private boolean performQuery( QueryResultsPagingForm queryResultsPagingForm, QueryForm queryForm, HttpSession session)
	{
		try
		{
			Query query = new QueryImpl();
			query.setCriteria(getSearchCriteria(queryForm));

			int totalResultSize = query.totalResultSize();

			if (totalResultSize > 0)
			{
				// Query results
				int currentStartOffSet = 0;
				int maxSize = 0;
				currentStartOffSet = (new Integer(queryResultsPagingForm.getTargetPageNumber()).intValue()-1)* new Integer(queryForm.getRecordCount()).intValue() + 1;
				maxSize =  new Integer(queryForm.getRecordCount()).intValue();
				Collection resultCollection = query.query(currentStartOffSet,maxSize);
				List resultList = (List) resultCollection;

				// Set Search Result Page information
				SearchResultPage searchResultPage = null;
				if(session.getAttribute(Constants.SEARCH_RESULTS_PAGE)!=null){
					searchResultPage = (SearchResultPage) session.getAttribute(Constants.SEARCH_RESULTS_PAGE);
				}else{
					searchResultPage = new SearchResultPage();
				}
				
				
				searchResultPage.setPageSize(new Integer(queryForm.getRecordCount()).intValue());
				searchResultPage.setCurrentPageNumber(new Integer(queryResultsPagingForm.getTargetPageNumber()).intValue());
				searchResultPage.setSearchResultMessage(Constants.RESULTS_MESSAGE);
				searchResultPage.setSearchResultObjects(resultList);

				// Set Search Results
				session.setAttribute(Constants.SEARCH_RESULTS_PAGE, searchResultPage);
				session.setAttribute(Constants.VIEW_PAGE_NUMBER, new Integer(searchResultPage.getCurrentPageNumber()));
				return true;
			}
			else
			{
				// Set Search Result Page information
				SearchResultPage searchResultPage = new SearchResultPage();
				searchResultPage.setTotalResultSize(totalResultSize);
				searchResultPage.setSearchResultMessage(Constants.NO_RESULTS_MESSAGE);

				// Set Search Results
				session.setAttribute(Constants.SEARCH_RESULTS_PAGE, searchResultPage);
				
				return false;
			}
		}
		catch (Exception e)
		{
			// Set Search Result Page information
				SearchResultPage searchResultPage = new SearchResultPage();
				searchResultPage.setTotalResultSize(0);
				searchResultPage.setSearchResultMessage(Constants.NO_RESULTS_MESSAGE);

				// Set Search Results
				session.setAttribute(Constants.SEARCH_RESULTS_PAGE, searchResultPage);
			return false;
		}
		
	}

	private SearchCriteria getSearchCriteria(QueryForm queryForm)
	{
		SearchCriteria searchCriteria = new SearchCriteria();

		searchCriteria.setApplication(!StringUtils.isBlankOrNull(queryForm.getApplication()) ? queryForm.getApplication() : null);
		searchCriteria.setEndDate(!StringUtils.isBlankOrNull(queryForm.getEndDate()) ? queryForm.getEndDate() : null);
		searchCriteria.setEndTime(!StringUtils.isBlankOrNull(queryForm.getEndTime()) ? queryForm.getEndTime() : null);
		if(Constants.ALL.equalsIgnoreCase(queryForm.getLogLevel()) || queryForm.getLogLevel().length()==0){
			searchCriteria.setLogLevel(null);	
		}else{
			searchCriteria.setLogLevel(queryForm.getLogLevel());
		}
		searchCriteria.setMessage(!StringUtils.isBlankOrNull(queryForm.getMessage()) ? queryForm.getMessage() : null);
		searchCriteria.setNdc(!StringUtils.isBlankOrNull(queryForm.getNdc()) ? queryForm.getNdc() : null);
		searchCriteria.setObjectID(!StringUtils.isBlankOrNull(queryForm.getObjectID()) ? queryForm.getObjectID() : null);
		searchCriteria.setObjectName(!StringUtils.isBlankOrNull(queryForm.getObjectName()) ? queryForm.getObjectName() : null);
		searchCriteria.setOperation(!StringUtils.isBlankOrNull(queryForm.getOperation()) ? queryForm.getOperation() : null);
		searchCriteria.setOrganization(!StringUtils.isBlankOrNull(queryForm.getOrganization()) ? queryForm.getOrganization() : null);
		if(StringUtils.isBlankOrNull(queryForm.getServer()) || StringUtils.initString(queryForm.getServer()).equalsIgnoreCase("ALL")){
			searchCriteria.setServer(null);
		}else{
			searchCriteria.setServer(queryForm.getServer());
		}
		searchCriteria.setSessionID(!StringUtils.isBlankOrNull(queryForm.getSessionID()) ? queryForm.getSessionID() : null);
		searchCriteria.setStartDate(!StringUtils.isBlankOrNull(queryForm.getStartDate()) ? queryForm.getStartDate() : null);
		searchCriteria.setStartTime(!StringUtils.isBlankOrNull(queryForm.getStartTime()) ? queryForm.getStartTime() : null);
		searchCriteria.setThreadName(!StringUtils.isBlankOrNull(queryForm.getThread()) ? queryForm.getThread() : null);
		searchCriteria.setThrowable(!StringUtils.isBlankOrNull(queryForm.getThrowable()) ? queryForm.getThrowable() : null);
		searchCriteria.setUserName(!StringUtils.isBlankOrNull(queryForm.getUser()) ? queryForm.getUser() : null);
		searchCriteria.addDescendingSortOrderFor(SearchCriteria.SORT_BY_PARAMETER_DATE);
		
		return searchCriteria;
	}
	
}
