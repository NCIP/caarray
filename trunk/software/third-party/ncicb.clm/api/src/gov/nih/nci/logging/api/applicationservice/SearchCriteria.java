package gov.nih.nci.logging.api.applicationservice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SearchCriteria object that is passed to Query to specify the search criteria.
 * 
 * <br><br> 
 * Example Use:
 * <br>&nbsp;	SearchCriteria searchCriteria = new SearchCriteria();
 * <br>&nbsp;	searchCriteria.setApplication("CSM");
 * <br>&nbsp;	searchCriteria.addAscendingSortOrderFor(SearchCriteria.SORT_BY_PARAMETER_APPLICATION);
 * <br>&nbsp;	searchCriteria.addDescendingSortOrderFor(SearchCriteria.SORT_BY_PARAMETER_USERNAME);
 * 
 * 
 * @author Vijay Parmar (Ekagra Software Technologies Limited.)
 */
public class SearchCriteria implements Constants {

	private String logLevel;

	private String application;

	private String server;

	/**
	 * Date format "MM/dd/yyyy"
	 */
	private String startDate;

	
	/**
	 * "h:mm a";
	 * example: "05:03 PM" 
	 */
	private String startTime;
	/**
	 * Date format "MM/dd/yyyy"
	 */
	private String endDate;
	/**
	 * "h:mm a";
	 * example: "05:03 PM" 
	 */
	private String endTime;

	private String userName;

	private String sessionID;

	private String message;

	private String isXmlView;

	private String ndc;

	private String threadName;

	private String throwable;

	private String objectID;

	private String objectName;

	private String organization;

	private String operation;

	private Map sortByOrder;

	/**
	 * @return Returns the logLevel.
	 */
	public String getLogLevel() {
		return logLevel;
	}

	/**
	 * @param logLevel
	 *            The logLevel to set.
	 */
	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	/**
	 * @return Returns the application.
	 */
	public String getApplication() {
		return application;
	}

	/**
	 * @param application
	 *            The application to set.
	 */
	public void setApplication(String application) {
		this.application = application;
	}

	/**
	 * Date format "MM/dd/yyyy"
	 * @return Returns the endDate.
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * Date format "MM/dd/yyyy"
	 * 
	 * @param endDate
	 *            The endDate to set.
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * Format  "h:mm a";
	 * example: "05:03 PM" 
	 * 
	 * @return Returns the endTime.
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime
	 *            The endTime to set.
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return Returns the isXmlView.
	 */
	public String getIsXmlView() {
		return isXmlView;
	}

	/**
	 * @param isXmlView
	 *            The isXmlView to set.
	 */
	public void setIsXmlView(String isXmlView) {
		this.isXmlView = isXmlView;
	}

	/**
	 * @return Returns the message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            The message to set.
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return Returns the ndc.
	 */
	public String getNdc() {
		return ndc;
	}

	/**
	 * @param ndc
	 *            The ndc to set.
	 */
	public void setNdc(String ndc) {
		this.ndc = ndc;
	}

	/**
	 * @return Returns the objectID.
	 */
	public String getObjectID() {
		return objectID;
	}

	/**
	 * @param objectID
	 *            The objectID to set.
	 */
	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}

	/**
	 * @return Returns the objectName.
	 */
	public String getObjectName() {
		return objectName;
	}

	/**
	 * @param objectName
	 *            The objectName to set.
	 */
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	/**
	 * @return Returns the operation.
	 */
	public String getOperation() {
		return operation;
	}

	/**
	 * @param operation
	 *            The operation to set.
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}

	/**
	 * @return Returns the organization.
	 */
	public String getOrganization() {
		return organization;
	}

	/**
	 * @param organization
	 *            The organization to set.
	 */
	public void setOrganization(String organization) {
		this.organization = organization;
	}

	/**
	 * @return Returns the server.
	 */
	public String getServer() {
		return server;
	}

	/**
	 * @param server
	 *            The server to set.
	 */
	public void setServer(String server) {
		this.server = server;
	}

	/**
	 * @return Returns the sessionID.
	 */
	public String getSessionID() {
		return sessionID;
	}

	/**
	 * @param sessionID
	 *            The sessionID to set.
	 */
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	/**
	 *  Date format "MM/dd/yyyy"
	 * @return Returns the startDate.
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * Date format "MM/dd/yyyy"
	 * 
	 * @param startDate
	 *            The startDate to set.
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return Returns the startTime.
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime
	 *            The startTime to set.
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return Returns the threadName.
	 */
	public String getThreadName() {
		return threadName;
	}

	/**
	 * @param threadName
	 *            The threadName to set.
	 */
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	/**
	 * @return Returns the throwable.
	 */
	public String getThrowable() {
		return throwable;
	}

	/**
	 * @param throwable
	 *            The throwable to set.
	 */
	public void setThrowable(String throwable) {
		this.throwable = throwable;
	}

	/**
	 * @return Returns the userName.
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            The userName to set.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Returns a LinkedHashMap of the parameters that the results need to be sorted by.
	 * The order in which the sort parameters are added is maintained in this map.
	 * @return Returns the sortByOrder.
	 */
	public Map getSortByOrderSequence() {
		return sortByOrder;
	}

	/**
	 * The Query Results can be sorted in Ascending or Descending order of
	 * sortable columns. The available sortable keys are specified in the
	 * Constants interface implemented by SearchCriteria Class
	 * 
	 * Example use of this method: <br>
	 * SearchCriteria searchCriteria = new SearchCriteria();
	 * searchCriteria.addAscendingSortOrderFor(SearchCriteria.SORT_BY_PARAMETER_APPLICATION);
	 * 
	 * This method adds a ascending Sort Order For sortByParameterString.
	 * 
	 * @param sortByParameterString
	 *            The property to sort the results by.
	 */
	public void addAscendingSortOrderFor(String sortByParameterString) {
		addSortByOrder(sortByParameterString, SearchCriteria.SORT_ORDER_ASCENDING);
	}

	/**
	 * The Query Results can be sorted in Ascending or Descending order of
	 * sortable columns. The available sortable keys are specified in the
	 * Constants interface implemented by SearchCriteria Class
	 * 
	 * Example use of this method: <br>
	 * SearchCriteria searchCriteria = new SearchCriteria();
	 * searchCriteria.addAscendingSortOrderFor(SearchCriteria.SORT_BY_PARAMETER_APPLICATION);
	 * 
	 * This method adds a descending Sort Order For sortByParameterString.
	 * 
	 * @param sortByParameterString
	 *            The property to sort the results by.
	 */
	public void addDescendingSortOrderFor(String sortByParameterString) {
		addSortByOrder(sortByParameterString, SearchCriteria.SORT_ORDER_DESCENDING);
	}

	/**
	 * 
	 * @param sortByParameterString
	 *            The property to sort the results by.
	 * @param ascOrDescValue
	 *            ascending or descending value
	 */
	private void addSortByOrder(String sortByParameterString,
			String ascOrDescValue) {
		if (this.sortByOrder == null) {
			this.sortByOrder = new LinkedHashMap();
		}
		this.sortByOrder.put(sortByParameterString, ascOrDescValue);
	}

}
