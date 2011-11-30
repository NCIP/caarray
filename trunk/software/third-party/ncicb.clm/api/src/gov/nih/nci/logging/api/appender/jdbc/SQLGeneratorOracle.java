package gov.nih.nci.logging.api.appender.jdbc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import gov.nih.nci.logging.api.domain.LogMessage;
import gov.nih.nci.logging.api.domain.ObjectAttribute;
import gov.nih.nci.logging.api.util.StringUtils;
/**
 * 
 * SQLGenerator is a utility class that generates SQL Insert statements for persisting LogMessage objects.
 *   
 * @author Vijay Parmar (Ekagra Software Technologies Limited.)
 */
public class SQLGeneratorOracle {

	public static final String APPLICATION = "APPLICATION";

	public static final String SERVER = "SERVER";

	public static final String CATEGORY = "CATEGORY";

	public static final String THREAD = "THREAD";

	public static final String USERNAME = "USERNAME";

	public static final String SESSIONID = "SESSION_ID";

	public static final String MSG = "MSG";

	public static final String THROWABLE = "THROWABLE";

	public static final String NDC = "NDC";

	public static final String CREATED_ON = "CREATED_ON";

	public static final String ORGANIZATION = "ORGANIZATION";
	public static final String OBJECT_ID = "OBJECT_ID";
	public static final String OBJECT_NAME = "OBJECT_NAME";
	public static final String OPERATION = "OPERATION";
	
	public static final String COMMA = ",";
	
	
	

	/**
	 * Returns an ArrayList consisting of SQL Insert Statements for LogMessage.
	 * 
	 * @param logMessage
	 * @return Arraylist consisting of SQL Insert Statements.
	 */
	public static List getSQLStatements(LogMessage logMessage) {
		if(logMessage==null) return new ArrayList();
		
		if (logMessage.isObjectStateLog()) {
			return getObjectStateSQLStatements(logMessage);
		} else {
			List list = new ArrayList();
			list.add(getLogMessageSQLStatement(logMessage));
			return list;
		}
	}

	/**
	 * Returns an ArrayList of SQL Insert statements to persist a LogMessage Object. The LogMessage can be an Event or Object Log Message.
	 * 
	 * @param logMessage
	 * @return
	 */
	private static List getObjectStateSQLStatements(LogMessage logMessage) {
			
		List<String> l = new ArrayList();
	
		
		 
			l.add(getLogMessageSQLStatement(logMessage));
			//System.out.println(getLogMessageSQLStatement(logMessage));
			//sb1.append(getLogMessageSQLStatement(logMessage));

			// Get the primary id for the last insert statement.
			//l.add("SELECT @log_id:=	last_number	FROM user_sequences where sequence_name = 'LOG_MESSAGE_ID_SEQ'");
			
			
			
			
			Set objectAttributeSet = logMessage.getObjectAttributeSet();
			int iCount = 0;
			Iterator iterator = objectAttributeSet.iterator();
			while(iterator.hasNext()){
				iCount+= 1;
				ObjectAttribute oa = (ObjectAttribute) iterator.next();
				l.add(getObjectAttributeSQLStatement(oa));
				//System.out.println(getObjectAttributeSQLStatement(oa));
				//l.add("SELECT @objectattribute_id"+iCount+":= last_insert_id()");
				l.add("insert into OBJECTATTRIBUTES (LOG_ID, OBJECT_ATTRIBUTE_ID) values (LOG_MESSAGE_ID_SEQ.CURRVAL,OBJECT_ATTRIBUTE_ID_SEQ.CURRVAL)");
				//System.out.println("insert into OBJECTATTRIBUTES (LOG_ID, OBJECT_ATTRIBUTE_ID) values (LOG_MESSAGE_ID_SEQ.CURRVAL,OBJECT_ATTRIBUTE_ID_SEQ.CURRVAL)");

			}
			l.add("COMMIT");
		


			
		return l;
	}

	
	/**
	*  Returns a SQL Insert statement based on an LogMessage Object instance. 
	 * 
	 * @param logMessage
	 * @return a string consisting an SQL insert statement for LogMessage object
	 */
	private static String getLogMessageSQLStatement(LogMessage logMessage) {

		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO log_message (");
		sql.append(		APPLICATION);
		sql.append(COMMA+SERVER);
		sql.append(COMMA+CATEGORY);
		sql.append(COMMA+THREAD);
		sql.append(COMMA+USERNAME);
		sql.append(COMMA+SESSIONID);
		sql.append(COMMA+MSG);
		sql.append(COMMA+THROWABLE);
		sql.append(COMMA+NDC);
		sql.append(COMMA+CREATED_ON);
		sql.append(COMMA+ORGANIZATION);
		sql.append(COMMA+OBJECT_ID);
		sql.append(COMMA+OBJECT_NAME);
		sql.append(COMMA+OPERATION);
		sql.append(") VALUES ('");
		sql.append(StringUtils.initString(logMessage.getApplication()));
		sql.append("','");
		sql.append(StringUtils.initString(logMessage.getServer()));
		sql.append("','");
		sql.append(StringUtils.initString(logMessage.getLogLevel()));
		sql.append("','");
		sql.append(StringUtils.initString(logMessage.getThread()));
		sql.append("','");
		sql.append(StringUtils.initString(logMessage.getUserName()));
		sql.append("','");
		sql.append(StringUtils.initString(logMessage.getSessionID()));
		sql.append("','");
		sql.append(StringUtils.initString(logMessage.getMessage()));
		sql.append("','");
		sql.append(StringUtils.initString(logMessage.getThrowable()));
		sql.append("','");
		sql.append(StringUtils.initString(logMessage.getNdc()));
		sql.append("','");
		sql.append(logMessage.getCreatedOn());
		sql.append("','");
		sql.append(StringUtils.initString(logMessage.getOrganization()));
		sql.append("','");
		sql.append(StringUtils.initString(logMessage.getObjectID()));
		sql.append("','");
		sql.append(StringUtils.initString(logMessage.getObjectName()));
		sql.append("','");
		sql.append(StringUtils.initString(logMessage.getOperation()));
		sql.append("')");
		return sql.toString();
	}

	/**
	 *  Returns a SQL Insert statement based on an ObjectAttribute Object instance. 
	 * 
	 * @param objectAttribute
	 * @return a string consisting an SQL insert statement for ObjectAttribute object.
	 */
	private static String getObjectAttributeSQLStatement(ObjectAttribute objectAttribute) {
		
		return "insert into OBJECT_ATTRIBUTE (OBJECT_ATTRIBUTE_ID,ATTRIBUTE, PREVIOUS_VALUE, CURRENT_VALUE) values (NULL"
			+",'"+StringUtils.initString(objectAttribute.getAttributeName())
			+"','"+StringUtils.initString(objectAttribute.getPreviousValue())
			+"','"+StringUtils.initString(objectAttribute.getCurrentValue())
			+"')";
	}
}


