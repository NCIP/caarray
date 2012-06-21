package gov.nih.nci.logging.api.logger.util;

/**
 * <!-- LICENSE_TEXT_START -->
 * 
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * ApplicationConstants includes for managing the property file and message contents.
 * 
 * @author Ekagra Software Technologes Limited ('Ekagra')
 */

public interface ApplicationConstants
{

	// Logger ApplicationConstants.
	
	public static final String ObjectStateLoggerConfigFile = "Logger";
	public static final String ApplicationPropertyFile = "ObjectStateLoggerConfig.xml";
	
	
	// Object State Logger Configuration
	
	public static final String OBJECT_STATE_LOGGER_CONFIG = "ObjectStateLoggerConfig"; //Root Element
	public static final String LOGGER_CONFIGURATION_FILE = "LoggerConfigurationFile";
	
	public static final String OBJECT_STATE_LOGGER_MESSAGE_FORMAT = "ObjectStateLoggerMessageFormat";
	public static final String OBJECT_STATE_LOGGER_MESSAGE_FORMAT_STRING = "STRING";
	public static final String OBJECT_STATE_LOGGER_MESSAGE_FORMAT_XML = "XML";
	
	public static final String OBJECT_STATE_LOGGER_NAME= "ObjectStateLoggerName";
	public static final String LOG_LEVEL = "LogLevel";
	
	public static final String LOGGING_ENABLED = "LoggingEnabled";
	public static final String LOGGING_ENABLED_TRUE = "TRUE";
	public static final String LOGGING_ENABLED_FALSE = "FALSE";
	
	public static final String DOMAIN_OBJECT_LIST= "DomainObjectList";
	public static final String DOMAIN_OBJECT= "DomainObject";
	public static final String OBJECT_NAME= "ObjectName";
	public static final String IDENTIFIER_ATTRIBUTE = "IdentifierAttribute";
	
	// Message Constants	
	
	public static final String OBJECT_STATE_MESSAGE_OPERATION = "operation";
	public static final String OBJECT_STATE_MESSAGE_OBJECT = "object";
	public static final String OBJECT_STATE_MESSAGE_PREVIOUS_ATTRIBUTES = "previousAttributes";
	public static final String OBJECT_STATE_MESSAGE_CURRENT_ATTRIBUTES = "currentAttributes";
	public static final String OBJECT_STATE_MESSAGE_ATTRIBUTES = "attributes";
	
	public static final String OBJECT_STATE_MESSAGE_OPERATION_NAME= "name";
	public static final String OBJECT_STATE_MESSAGE_OPERATION_COMMENT= "comment";
	public static final String OBJECT_STATE_MESSAGE_OPERATION_OBJECT_NAME= "objName";
	public static final String OBJECT_STATE_MESSAGE_OPERATION_OBJECT_IDENTIFIER_ATTRIBUTE_VALUE= "objID";
	
	
	//
	public static final String DISPLAY_DATE_FORMAT =  "MM/dd/yyyy , h:mm a";
	public static final String DATE_FORMAT =  "MM/dd/yyyy";
	public static final String TIME_FORMAT =  "hh:mm aa";
	
	//Application Exception Messages
	public static final String EXCEPTION_SEARCH_CRITERIA = "Search Criteria is not specified.\n";
	public static final String EXCEPTION_APPLICATION= "Application is not provided. Application is required.\n";
	public static final String EXCEPTION_LOGLEVEL= "Log Level is not provided. Log Level is required.\n";
	public static final String EXCEPTION_LEVEL= "Log Level is not provided. Log Level is required.\n";
	public static final String EXCEPTION_START_DATE = "Start Date is not provided. Start Date is required.\n";
	public static final String EXCEPTION_START_DATE_FORMAT = "Start Date provided is not in "+ApplicationConstants.DATE_FORMAT+" \n";
	public static final String EXCEPTION_END_DATE = "End Date is not provided. End Date is required. \n";
	public static final String EXCEPTION_END_DATE_FORMAT = "End Date provided is not in "+ApplicationConstants.DATE_FORMAT+" \n";
	public static final String EXCEPTION_START_TIME = "Start Time is not provided. Start Time is required.\n";
	public static final String EXCEPTION_START_TIME_FORMAT = "Start Time provided is not in "+ApplicationConstants.TIME_FORMAT+" \n";
	public static final String EXCEPTION_END_TIME =  "End Time is not provided. End Time is required.\n";
	public static final String EXCEPTION_END_TIME_FORMAT = "End Time provided is not in "+ApplicationConstants.TIME_FORMAT+" \n";
	
	
	// Dialects
	
	public static final String MYSQL_DIALECT = "org.hibernate.dialect.MySQLDialect";
	public static final String ORACLE_DIALECT = "org.hibernate.dialect.OracleDialect";
	public static final String SQLSERVER_DIALECT = "org.hibernate.dialect.SQLServerDialect";
	
	
	
	

}
