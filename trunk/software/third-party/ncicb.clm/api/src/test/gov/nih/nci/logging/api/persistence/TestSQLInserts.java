package test.gov.nih.nci.logging.api.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gov.nih.nci.logging.api.appender.jdbc.SQLGeneratorMySQL;
import gov.nih.nci.logging.api.domain.LogMessage;
import gov.nih.nci.logging.api.domain.ObjectAttribute;

public class TestSQLInserts {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		List statementList = new ArrayList();
		//statementList.add("BEGIN;");
		/*statementList.add("insert into LOG_MESSAGE (LOG_ID,CREATED_ON) values (1,1);");
		statementList.add("SELECT @log_id:=LAST_INSERT_ID();");
		statementList.add("insert into OBJECT_ATTRIBUTE (OBJECT_ATTRIBUTE_ID,ATTRIBUTE, PREVIOUS_VALUE, CURRENT_VALUE) values (NULL,'a','b','c');");
		statementList.add("SELECT @objectattribute_id:= last_insert_id();");
		statementList.add("insert into OBJECTATTRIBUTES (LOG_ID, OBJECT_ATTRIBUTE_ID) values (11,12);");*/
		//statementList.add("COMMIT;");

		statementList.addAll(SQLGeneratorMySQL.getSQLStatements(getLogMessage_ObjectState()));
		statementList.addAll(SQLGeneratorMySQL.getSQLStatements(getLogMessage_NonObjectState()));
		
		Connection con = null;
		Statement stmt = null;
		try
		{
		
			Class.forName("org.gjt.mm.mysql.Driver");//com.mysql.jdbc.Driver // org.gjt.mm.mysql.Driver
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/clm", "root","admin");
			//con = DriverManager.getConnection("jdbc:mysql://cbiovdev5004.nci.nih.gov:3620/clm", "ncisecurity","ncisecurity");
			con.setAutoCommit(false);
			stmt = con.createStatement();
			

			Iterator iterator = statementList.iterator();
			while (iterator.hasNext())
			{
				String str  = (String) iterator.next();				
			    stmt.execute(str);
			    
			}
			con.commit();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally
		{
			try{
				stmt.close();
			}catch (Exception ex)
			{		}
			try
			{
				con.close();
			}
			catch (Exception ex)
			{
			}
		}
	}
	



	private static LogMessage getLogMessage_NonObjectState() {

		LogMessage lm = new LogMessage();
		
		lm.setApplication("JoeApp");
		lm.setLogLevel("WARN");
		
		
		lm.setCreatedOn(new Long(10055000));
		
		
		return lm;
	}




	

	private static LogMessage getLogMessage_ObjectState() {
		LogMessage lm = new LogMessage();
		
		lm.setApplication("objectstateApp");
		lm.setLogLevel("INFO");
		
		ObjectAttribute oa = new ObjectAttribute();
		oa.setAttributeName("OA1");
		oa.setCurrentValue("Current_OA1");
		oa.setPreviousValue("Prev_OA1");
		
		lm.addObjectAttribute(oa);
		lm.setCreatedOn(new Long(1000000));
		
		
		return lm;
	}

}
