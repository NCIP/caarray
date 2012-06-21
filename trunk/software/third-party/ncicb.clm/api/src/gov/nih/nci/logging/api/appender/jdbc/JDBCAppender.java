package gov.nih.nci.logging.api.appender.jdbc;

/**
 * <!-- LICENSE_TEXT_START -->
 * 
 * 
 * <!-- LICENSE_TEXT_END -->
 */

import gov.nih.nci.logging.api.appender.util.AppenderUtils;
import gov.nih.nci.logging.api.applicationservice.Constants;
import gov.nih.nci.logging.api.domain.LogMessage;
import gov.nih.nci.logging.api.domain.ObjectAttribute;
import gov.nih.nci.logging.api.logger.util.ApplicationConstants;
import gov.nih.nci.logging.api.logger.util.ThreadVariable;
import gov.nih.nci.logging.api.user.UserInfo;
import gov.nih.nci.logging.api.util.StringUtils;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A custom Apache Log4J Appender will be responsible for formatting and
 * inserting Log4J messages into the configurable RDBMS.
 * 
 * <br><br>Features include: 
 * <br>--Inserts Logs Messages into the database in near real time 
 * <br>--Uses a configurable buffer to perform batch processing 
 * <br>--Spawns threads to execute the batch inserts to maximize performance 
 * <br>--Prepares all data for RDBMS by escaping quotes.
 * 
 * @author Vijay Parmar (Ekagra Software Technologies Limited.)
 * 
 */
public class JDBCAppender extends AppenderSkeleton implements Constants
{

	private String application = null;
	private static String server = null;
	private String dbUrl = null;
	private String dbDriverClass = null;
	private String dbUser = null;
	private String dbPwd = null;
	private String useFilter = null;
	private int recordCtr = 0;
	private int maxBufferSize = 0;
	private List buff = new ArrayList();
	
	static
	{
		// set the server by looking up the host name from the system
		try
		{
			setServer(InetAddress.getLocalHost().getHostName());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}

	/**
	 * 
	 */
	public JDBCAppender()
	{

	}

	/**
	 * @param layout
	 */
	public JDBCAppender(Layout layout)
	{
		super();
		setLayout(layout);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.Appender#setLayout(org.apache.log4j.Layout)
	 */
	public void setLayout(Layout layout)
	{
		super.setLayout(layout);
	}

	private String clean(String str)
	{
		if (str == null || str.length() <= 0)
		{
			return "";
		}
		if (str.indexOf("'") < 0 && str.indexOf(",") < 0)
		{
			return str;
		}
		str = replace(str, "'", "''");
		return replace(str, ",", " ");

	}

	protected String replace(String source, String find, String replacement)
	{
		int i = 0;
		int j;
		int k = find.length();
		int m = replacement.length();

		while (i < source.length())
		{
			j = source.indexOf(find, i);

			if (j == -1)
			{
				break;
			}

			source = replace(source, j, j + k, replacement);

			i = j + m;
		}

		return source;
	}

	protected String replace(String source, int start, int end, String replacement)
	{
		if (start == 0)
		{
			source = replacement + source.substring(end);
		}
		else if (end == source.length())
		{
			source = source.substring(0, start) + replacement;
		}
		else
		{
			source = source.substring(0, start) + replacement + source.substring(end);
		}

		return source;
	}

	public void append(LoggingEvent le)
	{

		if (useFilter() && JDBCAppenderFilter.isMatch(le))
		{
			// The event was filtered out
			return;
		}
		//
		UserInfo userInfo = (UserInfo) ThreadVariable.get();
		if (null == userInfo){
			userInfo = new UserInfo();
		}
		
		// Determine Log Type
		String msg = "";
		if (le.getMessage() != null)
		{
			msg = le.getMessage().toString();
		}
		String logType = getLogType(msg);
		String level = "";
		if (le.getLevel() != null){
			level = le.getLevel().toString();
		}
		
		LogMessage logMessage;
		if(EVENT_LOG_TYPE.equalsIgnoreCase(logType)){
			logMessage = new LogMessage();
			logMessage.setApplication(StringUtils.initString(clean(getApplication())));
			logMessage.setLogLevel(clean(level));
			java.util.Date d = new java.util.Date();
			d.setTime(new Long(System.currentTimeMillis()).longValue());
			logMessage.setCreatedDate(d);
			logMessage.setMessage(msg);
			logMessage.setNdc(clean(le.getNDC()));
			logMessage.setOrganization(clean(userInfo.getOrganization()));
			logMessage.setServer(clean(getServer()));
			logMessage.setSessionID(clean(userInfo.getSessionID()));
			logMessage.setThread(clean(le.getThreadName()));
			logMessage.setThrowable(clean(getThrowable(le)));
			logMessage.setUserName(clean(userInfo.getUsername()));
			addRowToBuffer(logMessage);
		}
		if(OBJECT_STATE_LOG_TYPE.equalsIgnoreCase(getLogType(msg))){
			
			logMessage = populateObjectStateLogMesage(msg);
			
			if(logMessage==null){
				logMessage = new LogMessage();
			}
			
			logMessage.setApplication(clean(getApplication()));
			logMessage.setLogLevel(clean(level));
			java.util.Date d = new java.util.Date();
			d.setTime(new Long(System.currentTimeMillis()).longValue());
			logMessage.setCreatedDate(d);
			logMessage.setNdc(clean(le.getNDC()));
			logMessage.setOrganization(clean(userInfo.getOrganization()));
			logMessage.setServer(clean(getServer()));
			logMessage.setSessionID(clean(userInfo.getSessionID()));
			logMessage.setThread(clean(le.getThreadName()));
			logMessage.setThrowable(clean(getThrowable(le)));
			logMessage.setUserName(clean(userInfo.getUsername()));

			
			addRowToBuffer(logMessage);
		}


	}

	private String getLogType(String string) {
		if(StringUtils.initString(string).startsWith("&"+ApplicationConstants.OBJECT_STATE_MESSAGE_OPERATION+"=["+ApplicationConstants.OBJECT_STATE_MESSAGE_OPERATION_NAME+"=")){
			return OBJECT_STATE_LOG_TYPE;
		}
		return EVENT_LOG_TYPE;
	}
	
	/**
	 * Method parses the msg field content to populate the object state (operation name, object name/ID, object Attributes) into logMessage
	 * @param objectAttributeMessage
	 * @param logMessage
	 */
	private  LogMessage populateObjectStateLogMesage(String objectAttributeMessage ) {
		
		LogMessage logMessage = new LogMessage();
		
		HashMap previousAttributes= new HashMap();
		HashMap currentAttributes= new HashMap();
		
		StringTokenizer stringTokenizer = new StringTokenizer(objectAttributeMessage,"&");
		while(stringTokenizer.hasMoreElements()){
			String messagetemp = (String) stringTokenizer.nextElement();

			if(messagetemp.startsWith(ApplicationConstants.OBJECT_STATE_MESSAGE_OPERATION)){
				StringTokenizer attTknzr = new StringTokenizer(messagetemp,";[]");
				while(attTknzr.hasMoreElements()){
					String temp = (String) attTknzr.nextElement();
					if(temp==null) continue;
					if(temp.startsWith(ApplicationConstants.OBJECT_STATE_MESSAGE_OPERATION)) continue;
					
					if(temp.indexOf("=")<=0) continue;
					
					String attributeName= temp.substring(0,temp.indexOf("="));
					if(ApplicationConstants.OBJECT_STATE_MESSAGE_OPERATION_NAME.equalsIgnoreCase(attributeName)){
						String operationName= temp.substring(temp.indexOf("=")+1);
						logMessage.setOperation(StringUtils.initString(operationName));
					}
					if(ApplicationConstants.OBJECT_STATE_MESSAGE_OPERATION_COMMENT.equalsIgnoreCase(attributeName)){
						String comment= temp.substring(temp.indexOf("=")+1);
						logMessage.setMessage(StringUtils.initString(comment));
					}
					
				}
			}
			if(messagetemp.startsWith(ApplicationConstants.OBJECT_STATE_MESSAGE_OBJECT)){
				StringTokenizer attTknzr = new StringTokenizer(messagetemp,";[]");
				while(attTknzr.hasMoreElements()){
					String temp = (String) attTknzr.nextElement();
					if(temp==null) continue;
					if(temp.startsWith(ApplicationConstants.OBJECT_STATE_MESSAGE_OBJECT)) continue;
					
					if(temp.indexOf("=")<=0) continue;
					
					String attributeName= temp.substring(0,temp.indexOf("="));
					if(ApplicationConstants.OBJECT_STATE_MESSAGE_OPERATION_OBJECT_NAME.equalsIgnoreCase(attributeName)){
						logMessage.setObjectName(StringUtils.initString(temp.substring(temp.indexOf("=")+1)));
					}
					if(ApplicationConstants.OBJECT_STATE_MESSAGE_OPERATION_OBJECT_IDENTIFIER_ATTRIBUTE_VALUE.equalsIgnoreCase(attributeName)){
						logMessage.setObjectID(StringUtils.initString(temp.substring(temp.indexOf("=")+1)));
					}
				}
			}
			if(messagetemp.startsWith(ApplicationConstants.OBJECT_STATE_MESSAGE_PREVIOUS_ATTRIBUTES)){
				StringTokenizer attTknzr = new StringTokenizer(messagetemp,";[]");
				while(attTknzr.hasMoreElements()){
					String temp = (String) attTknzr.nextElement();
					if(temp==null) continue;
					if(temp.startsWith(ApplicationConstants.OBJECT_STATE_MESSAGE_PREVIOUS_ATTRIBUTES)) continue;
					
					if(temp.indexOf("=")<=0) continue;
					
					String attributeName= temp.substring(0,temp.indexOf("="));
					String attributeValue = temp.substring(temp.indexOf("=")+1);
					if(attributeName !=null && attributeName.length()>0){
						previousAttributes.put(attributeName,attributeValue);
						
					}
				}
			}
			if(messagetemp.startsWith(ApplicationConstants.OBJECT_STATE_MESSAGE_CURRENT_ATTRIBUTES)){
				StringTokenizer attTknzr = new StringTokenizer(messagetemp,";[]");
				while(attTknzr.hasMoreElements()){
					String temp = (String) attTknzr.nextElement();
					if(temp==null) continue;
					if(temp.startsWith(ApplicationConstants.OBJECT_STATE_MESSAGE_CURRENT_ATTRIBUTES)) continue;
					
					if(temp.indexOf("=")<=0) continue;
					
					String attributeName= temp.substring(0,temp.indexOf("="));
					String attributeValue = temp.substring(temp.indexOf("=")+1);
					if(attributeName !=null && attributeName.length()>0){
						currentAttributes.put(attributeName,attributeValue);	
					}
				}
			}
			if(messagetemp.startsWith(ApplicationConstants.OBJECT_STATE_MESSAGE_ATTRIBUTES)){

				StringTokenizer attTknzr = new StringTokenizer(messagetemp,";[]");
				while(attTknzr.hasMoreElements()){
					String temp = (String) attTknzr.nextElement();
					if(temp==null) continue;
					if(temp.startsWith(ApplicationConstants.OBJECT_STATE_MESSAGE_ATTRIBUTES)) continue;
					
					if(temp.indexOf("=")<=0) continue;
					
					String attributeName= temp.substring(0,temp.indexOf("="));
					String attributeValue = temp.substring(temp.indexOf("=")+1);
					if(attributeName !=null && attributeName.length()>0){
						currentAttributes.put(attributeName,attributeValue);	
					}
				}
			}
		}
		
		
		// Create ObjectAttribute Set for the ObjectStateLogMessage
		if(currentAttributes.size()>0){
			Iterator iterator = currentAttributes.keySet().iterator();
			while(iterator.hasNext()){
				String attribute = (String)iterator.next();
				ObjectAttribute oa = new ObjectAttribute();
				oa.setAttributeName(attribute);
				if(previousAttributes.containsKey(attribute)){
					String temp = (String)previousAttributes.get(attribute);
					oa.setPreviousValue(StringUtils.initString(temp));
				}
				if(currentAttributes.containsKey(attribute)){
					String temp = (String)currentAttributes.get(attribute);
					oa.setCurrentValue(StringUtils.initString(temp));
				}
				//
				logMessage.addObjectAttribute(oa);
			}
		}else{
			if(previousAttributes.size()>0){
				Iterator iterator = previousAttributes.keySet().iterator();
				while(iterator.hasNext()){
					String attribute = (String)iterator.next();
					ObjectAttribute oa = new ObjectAttribute();
					oa.setAttributeName(attribute);
					if(previousAttributes.containsKey(attribute)){
						oa.setPreviousValue(StringUtils.initString((String)previousAttributes.get(attribute)));
					}
					logMessage.addObjectAttribute(oa);
				}
			}
		}
		return logMessage;
		
	}

	private void addRowToBuffer(Object o)
	{
		
		getBuff().add(o);
		setRecordCtr(getRecordCtr() + 1);
		if (getRecordCtr() >= getMaxBufferSize())
		{
			execute();
		}
	}

	private boolean useFilter()
	{
		return Boolean.valueOf(getUseFilter()).booleanValue();
	}


	private void execute()
	{
		List rows = getBuff();
		setBuff(new ArrayList());
		setRecordCtr(0);
		JDBCExecutor exe = new JDBCExecutor(rows,getJDBCProperties() );

		// execute the batch insert
		new Thread(exe).start();
	}


	
	
	/**
	 * This method returns the Hibernate Properties.
	 * 
	 * @return Properties
	 */
	private Properties getJDBCProperties() {
		Properties props = new Properties();
        props.setProperty("hibernate.connection.driver_class",getDbDriverClass());
        props.setProperty("hibernate.connection.url",getDbUrl());
        props.setProperty("hibernate.connection.username",getDbUser());	
        props.setProperty("hibernate.connection.password",getDbPwd());
        
        if(getDbUrl().indexOf(":mysql")> -1){
        	props.setProperty("hibernate.dialect",ApplicationConstants.MYSQL_DIALECT);
        }
        if(getDbUrl().indexOf(":oracle")> -1){
        	props.setProperty("hibernate.dialect",ApplicationConstants.ORACLE_DIALECT);
        }
        if(getDbUrl().indexOf(":sqlserver")> -1){
        	props.setProperty("hibernate.dialect",ApplicationConstants.SQLSERVER_DIALECT);
        }
        
        
        return props;
	}

	protected static String getThrowable(LoggingEvent le)
	{
		return AppenderUtils.getThrowable(le);
	}

	public boolean requiresLayout()
	{
		return true;
	}

	public void close()
	{
	}

	public String getApplication()
	{
		return application;
	}

	public void setApplication(String value)
	{
		application = value;
	}

	/**
	 * @return Returns the server.
	 */
	public  String getServer()
	{
		return server;
	}

	/**
	 * @param server
	 * The server to set.
	 */
	public static  void setServer(String server)
	{
		JDBCAppender.server = server;
	}

	/**
	 * @return Returns the buff.
	 */
	public List getBuff()
	{
		return buff;
	}

	/**
	 * @param buff
	 * The buff to set.
	 */
	public void setBuff(List buff)
	{
		this.buff = buff;
	}

	/**
	 * @return Returns the dbDriverClass.
	 */
	public String getDbDriverClass()
	{
		return dbDriverClass;
	}

	/**
	 * @param dbDriverClass
	 * The dbDriverClass to set.
	 */
	public void setDbDriverClass(String dbDriverClass)
	{
		this.dbDriverClass = dbDriverClass;
	}

	/**
	 * @return Returns the dbPwd.
	 */
	public String getDbPwd()
	{
		return dbPwd;
	}

	/**
	 * @param dbPwd
	 * The dbPwd to set.
	 */
	public void setDbPwd(String dbPwd)
	{
		this.dbPwd = dbPwd;
	}

	/**
	 * @return Returns the dbUrl.
	 */
	public String getDbUrl()
	{
		return dbUrl;
	}

	/**
	 * @param dbUrl
	 * The dbUrl to set.
	 */
	public void setDbUrl(String dbUrl)
	{
		this.dbUrl = dbUrl;
	}

	/**
	 * @return Returns the dbUser.
	 */
	public String getDbUser()
	{
		return dbUser;
	}

	/**
	 * @param dbUser
	 * The dbUser to set.
	 */
	public void setDbUser(String dbUser)
	{
		this.dbUser = dbUser;
	}

	/**
	 * @return Returns the maxBufferSize.
	 */
	public int getMaxBufferSize()
	{
		return maxBufferSize;
	}

	/**
	 * @param maxBufferSize
	 * The maxBufferSize to set.
	 */
	public void setMaxBufferSize(int maxBufferSize)
	{
		this.maxBufferSize = maxBufferSize;
	}

	/**
	 * @return Returns the recordCtr.
	 */
	public int getRecordCtr()
	{
		return recordCtr;
	}

	/**
	 * @param recordCtr
	 * The recordCtr to set.
	 */
	public void setRecordCtr(int recordCtr)
	{
		this.recordCtr = recordCtr;
	}

	/**
	 * @return Returns the useFilter.
	 */
	public String getUseFilter()
	{
		return useFilter;
	}

	/**
	 * @param useFilter
	 * The useFilter to set.
	 */
	public void setUseFilter(String useFilter)
	{
		this.useFilter = useFilter;
	}


}