package test.gov.nih.nci.logging.api.appender.jdbc;

/**
 * <!-- LICENSE_TEXT_START -->
 * 
 * 
 * <!-- LICENSE_TEXT_END -->
 */

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * JUNIT test class for the JDBCAppender.
 * 
 * @author Ekagra Software Technologies Limited ('Ekagra')
 * 
 */
public class JDBCAppenderTest2 extends junit.framework.TestCase 
{
	public static Logger log = Logger.getLogger("sampleObjectStateLogger");
	protected static java.util.Random random = new java.util.Random();


	

	/*static Category cat = Category.getInstance(JDBCAppenderTest2.class.getName());*/

	public JDBCAppenderTest2(String testName)
	{
		super(testName);
	}

	public static void testAppend() throws Exception
	{
		DOMConfigurator.configure("resources/sample_log4j_config.xml");
		Logger log2 =Logger.getLogger("sampleObjectStateLogger");
		Level debug= Level.DEBUG;
		
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 20; i++)
		{
			
			 
			log.log(Level.DEBUG, "Debug test event log message");
			log.log(Level.INFO, "Info test event log message");
			log.log(Level.WARN, "Warn test event log message");
			


		}
		long endTime = System.currentTimeMillis();

		System.out.println("Total elapsed time was: " + (endTime - startTime));

	}

	public static void main(String[] args) throws Exception
	{
		JDBCAppenderTest2.testAppend();
	}

}