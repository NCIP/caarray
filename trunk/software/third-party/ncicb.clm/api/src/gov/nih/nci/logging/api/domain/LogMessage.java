package gov.nih.nci.logging.api.domain;

import gov.nih.nci.logging.api.logger.util.ApplicationConstants;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * LogMessage represents a log message created by Common Logging Module. It can
 * represent event logs and/or object state log messages.
 * 
 * @author Vijay Parmar (Ekagra Software Technologies Limited.)
 * @version 1.0
 
 */
public class LogMessage implements Serializable {
	/**
	 * Primary Key of the Log Message Object
	 */
	private long id;

	/**
	 * LogLevel indicates the log level to be logged. Possible values are the Log4j
	 * Log Levels specified in org.apache.log4j.Level. Check the class for all
	 * possible values. Example values are OFF, FATAL, ERROR, WARN, INFO, DEBUG and
	 * ALL.
	 */
	private String logLevel;
	/**
	 * The name of thread object in which this log was generated.
	 * 
	 */
	private String thread;
	/**
	 * The login username of the user. The user is the end user of the client
	 * application which uses CLM for logging. This way we can retrieve and audit all
	 * the logs generated by a particular user.
	 */
	private String userName;
	/**
	 * The HTTP Session Id of the logged in user in case of a web application. This
	 * field is useful to track all the operations that are performed by the user
	 * during a session.
	 */
	private String sessionID;
	/**
	 * This is a free form text message provided by the application during logging of
	 * the event. It could represent an event, data etc. In case of Object State
	 * Change logs, this field would store the comment or reason for the object state
	 * change.
	 */
	private String message;
	/**
	 * Throwable error message. It is the string reprentation of the throwable (throwable.toString()).
	 * 
	 * ThrowableInformation is log4j's internal representation of throwables. It essentially consists of a string array,
	 * called 'rep', where the first element, that is rep[0], represents the
	 * string representation of the throwable (i.e. the value you get when you
	 * do throwable.toString()) and subsequent elements correspond the stack
	 * trace with the top most entry of the stack corresponding to the second
	 * entry of the 'rep' array that is rep[1].
	 */
	private String throwable;
	/**
	 * Nested Diagnostic Context. A Nested Diagnostic Context, or NDC in short,
	 * is an instrument to distinguish interleaved log output from different
	 * sources. Log output is typically interleaved when a server handles
	 * multiple clients near-simultaneously.
	 */
	private String ndc;
	


	
	/**
	 * The date and time  of when the log is created.
	 */
	private String createdDate;
	
	
	/**
	 * The date and time in milliseconds when the log is created.
	 */
	private Long createdOn;
	
	
	/**
	 * The name of the application from which the log message originated.
	 * 
	 * Example: CSM, caMatch..
	 * 
	 */
	private String application;
	/**
	 * The name of the server from which the log message originated.
	 */
	private String server;
	/**
	 * ObjectID is used when the log message is of Object state. For event log
	 * message objectID its value is null. The object property value of the
	 * Identifier Attribute is saved in ObjectID column. The value of
	 * ObjectID will be matched with query Object ID for retrieval.
	 * 
	 * <br><br>Example: For object 'Customer' with attributes: 'personname', 'street', 'zip'.
	 * If the Identifier Attribute is 'personname', objectID will have possible values of
	 * personname attribute.
	 * 
	 * <br><br>Example: For object 'Customer' with attributes: first, last, street, zip. If the
	 * Identifier Attribute is 'first,last', objectID will have possible values
	 * of 'first+" "+last'. if first = "John", last = "Smith", then the value of objectID = "John Smith".
	 */
	private String objectID;
	/**
	 * The fully qualified name of the object whose object state is available in this
	 * log message.  Example : For object 'Customer', its qualified class name is
	 * objectName. Example : 'test.package.Customer' (without quotes)
	 */
	private String objectName;
	/**
	 * The name of the organization to which the user or the logs generated belongs.
	 */
	private String organization;
	/**
	 * It is the name of the database operation which generated this object state log .
	 * For event log messages its value is null. Example values : update, insert,
	 * delete
	 */
	private String operation;

	/**
	 * 
	 * The set of ObjectAttribute containing all attributes of the object for
	 * which the object state log message is generated.
	 * 
	 * 
	 */
	private Set objectAttributeSet;



	/**
	 * @return true if this log message is an object state log, else returns
	 *         false.
	 */
	public boolean isObjectStateLog() {

		if (objectAttributeSet == null || objectAttributeSet.size()==0) {
			return false;
		} else {
			return true;
		}
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
	 * @return Returns the thread.
	 */
	public String getThread() {
		return thread;
	}

	/**
	 * @param thread
	 *            The thread to set.
	 */
	public void setThread(String thread) {
		this.thread = thread;
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

	public String getObjectID() {
		return objectID;
	}

	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return Returns the createdDate.
	 */
	public String getCreatedDate() {
		
		final java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(ApplicationConstants.DISPLAY_DATE_FORMAT);
		
		java.util.Date d = new java.util.Date();
		long l = new Long(this.createdOn.longValue()).longValue();
		d.setTime(l);
		return sdf.format(d);
	}

	/**
	 * @param createdDate The createdDate to set.
	 */
	public void setCreatedDate(Date createdDate) {		
		this.createdOn = new Long(createdDate.getTime());
	}
	
	

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

	public Set getObjectAttributeSet() {
		return objectAttributeSet;
	}

	public void setObjectAttributeSet(Set objectAttributeSet) {
		this.objectAttributeSet = objectAttributeSet;
	}

	public void addObjectAttribute(ObjectAttribute objectAttribute) {
		if (objectAttributeSet == null) {
			objectAttributeSet = new HashSet();
		}
		this.objectAttributeSet.add(objectAttribute);
	}

	/**
	 * @return Returns the createdOn.
	 */
	public Long getCreatedOn() {
		return createdOn;
	}

	/**
	 * @param createdOn The createdOn to set.
	 */
	public void setCreatedOn(Long created_on) {
		this.createdOn = created_on;
	}



}
