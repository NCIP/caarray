package gov.nih.nci.logging.api.logger.hibernate;

/**
 * <!-- LICENSE_TEXT_START -->
 * 
 * <!-- LICENSE_TEXT_END -->
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.log4j.*;
import org.hibernate.type.Type;

import gov.nih.nci.logging.api.logger.util.ApplicationProperties;
import gov.nih.nci.logging.api.logger.util.MessageGenerator;
import gov.nih.nci.logging.api.logger.util.ThreadVariable;
import gov.nih.nci.logging.api.user.UserInfo;
import gov.nih.nci.logging.api.util.StringUtils;

/**
 * This class logs the object state information.
 * 
 * 
 * @author Vijay Parmar (Ekagra Software Technologies Limited.)
 */

public class ObjectStateLogger
{
	private static Logger logger = null;
	private static ObjectStateLogger myInstance = null;

	private ObjectStateLogger()
	{	 
		logger = Logger.getLogger(ApplicationProperties.getInstance().getLoggerName());
	}

	/**
	 * This method logs the message for update operation
	 * 
	 * @param obj --
	 *            Object to be audited
	 * @param id --
	 *            Serializable id of the object
	 * @param currentState --
	 *            current states of the object after the operation
	 * @param previousState --
	 *            previous states of the object before the operation
	 * @param propertyNames
	 *            --names of the object states
	 * @param types --
	 *            Hibernate types of the object states
	 * @param operation --
	 *            the name of the operation being performed
	 * 
	 */
	public  void logMessage(Object obj, Serializable id, Object[] currentState, Object[] prevState, String[] propertyNames, Type[] types, String operation)
	{
		
		
		if (ApplicationProperties.getInstance().isLoggingEnabled())
		{						
			
			if (ApplicationProperties.getInstance().isObjectStateLoggingEnabled(obj) == true)
			{
				//get Object State Comment by Client(app).
				String comment = getComment();
				// get Object ID by Client(app).
				String objectID = getIdentifierAttributeValue(obj, propertyNames, currentState);
				
				if (ApplicationProperties.getInstance().getMessageLoggingFormat().equalsIgnoreCase(ApplicationProperties.OBJECT_STATE_LOGGER_MESSAGE_FORMAT_STRING)){
					
					String myMessage = null;
					myMessage = MessageGenerator.generateStringMessage(obj, id, currentState, prevState, propertyNames, types, operation, comment,objectID);
					logMessage(myMessage);
					
					}
				else if (ApplicationProperties.getInstance().getMessageLoggingFormat().equalsIgnoreCase(ApplicationProperties.OBJECT_STATE_LOGGER_MESSAGE_FORMAT_XML))
				{
					String outfileName = obj.getClass().getName() + System.currentTimeMillis() + ".xml";
					MessageGenerator.generateXMLMessage(obj, id, currentState, prevState, propertyNames, types, operation, comment,objectID, outfileName);
				}
			}
		}
	}
	
	/**
	 * 
	 * This method gets the identifier Attribute of the object from ApplicationProperties and returns the value for the identifier attribute.
	 * @param obj The Object whose state change is being logged.
	 * @param objpropertyNames Array of property/attribute names
	 * @param objcurrentState Array of current state values for Object attribute/property
	 * @return identifierAttributeValue 
	 */
	private String getIdentifierAttributeValue(Object obj, final  String[] objpropertyNames, final Object[] objcurrentState) {
		String identifierAttributeValue ="";
		
		if(obj==null) return null;
		
		String identifierAttributes = ApplicationProperties.getInstance().getIdentifierAttribute(obj);
		
		if(StringUtils.isBlank(identifierAttributes)) return identifierAttributeValue;
		
		StringTokenizer tknzr = new StringTokenizer(StringUtils.initString(identifierAttributes),",");
		
		while(tknzr.hasMoreElements()){
			String identifierAttribute = (String) tknzr.nextElement();
			
			for(int i=0;i<objpropertyNames.length; i++){
				if(objpropertyNames[i].equalsIgnoreCase(identifierAttribute)){
					identifierAttributeValue = identifierAttributeValue + " " + (String) objcurrentState[i];
				}
			}
		}
		return identifierAttributeValue;
	}



	/**
	 * Object State Log Comment from Client Application.
	 * The comment is reset and is only available for current operation.
	 * @return Comment.
	 */
	private String getComment(){
		String comment = "";
		
		UserInfo userInfo = (UserInfo) ThreadVariable.get();
		if (null != userInfo){
			comment = StringUtils.initString(userInfo.getComment());
			userInfo.setComment(null);
			ThreadVariable.set(userInfo);
		}
		return comment;
	}

	/**
	 * This method does the actual logging 
	 * @param message --
	 *            message to be logged
	 */
	public  void logMessage(String message)
	{
		UserInfo userInfo = (UserInfo) ThreadVariable.get();
		if (null == userInfo)
			userInfo = new UserInfo();
		if (userInfo.getIsIntransaction() == true)
		{
			logToBuffer(message);
		}
		else
		{
			// 
			log(message);
		}
	}

	/**
	 * This method saves the message to the buffer for later use
	 * @param msg --
	 *            message to be logged
	 */
	public  void logToBuffer(String msg)
	{
			UserInfo userInfo = (UserInfo) ThreadVariable.get();
			if (null == userInfo)
				userInfo = new UserInfo();
			ArrayList logs = userInfo.getTransactionLogs();
			if (logs == null)
			{
				logs = new ArrayList();
			}

			logs.add(msg);
			userInfo.setTransactionLogs(logs);
			ThreadVariable.set(userInfo);		 

	}

	/**
	 * This method logs the message
	 * 
	 * @param message --  message to be logged
	 */
	public  void log(String message)
	{
		Level level = Level.toLevel(ApplicationProperties.getInstance().getLogLevel().toUpperCase());	 
		logger.log(level, message);
	}

	/**
	 * @return -- Returns the singleton of this class
	 */
	public static ObjectStateLogger getInstance()
	{
		if (myInstance == null)
		{
			myInstance = new ObjectStateLogger();
		}
		return myInstance;
	}

}
