package gov.nih.nci.logging.api.applicationservice;

/**
 * Constants interface defines constants that are available to Query API as well the Client of Querys API.
 * 
 * @author Vijay Parmar (Ekagra Software Technologies Limited.)
 */
public interface Constants {

	
	// Log Message fields.
	 static String _APPLICATION = "application";
	 static String _LOG_LEVEL = "logLevel";
	 static String _DATE = "createdOn";
	 static String _OBJECT_ID = "objectID";
	 static String _OBJECT_NAME = "objectName";
	 static String _OPERATION = "operation";
	 static String _ORGANIZATION = "organization";
	 static String _PRIORITY = "priority";
	 static String _SERVER = "server";
	 static String _SESSION_ID = "sessionID";
	 static String _THROWABLE = "throwable";
	 static String _USERNAME = "userName";
	 static String _MESSAGE = "message";
	 static String _NDC = "ndc";
	 static String _THREAD = "thread";
	 

	// Sort Parameters
	public static String SORT_BY_PARAMETER_APPLICATION = "application";
	public static String SORT_BY_PARAMETER_LOG_LEVEL = "category";
	public static String SORT_BY_PARAMETER_DATE = "createdOn";
	public static String SORT_BY_PARAMETER_OBJECT_ID = "object_id";
	public static String SORT_BY_PARAMETER_OBJECT_NAME = "object_name";
	public static String SORT_BY_PARAMETER_OPERATION = "operation";
	public static String SORT_BY_PARAMETER_ORGANIZATION = "organization";
	public static String SORT_BY_PARAMETER_PRIORITY = "priority";
	public static String SORT_BY_PARAMETER_SERVER = "server";
	public static String SORT_BY_PARAMETER_SESSION_ID = "sessionID";
	public static String SORT_BY_PARAMETER_THROWABLE = "throwable";
	public static String SORT_BY_PARAMETER_USERNAME = "userName";

	
	//	Log Type
	public static String EVENT_LOG_TYPE = "eventLog";
	public static String OBJECT_STATE_LOG_TYPE= "objectStateLog";
	
	// Sort Order
	public static String SORT_ORDER_ASCENDING = "ascending";
	public static String SORT_ORDER_DESCENDING= "descending";

}
