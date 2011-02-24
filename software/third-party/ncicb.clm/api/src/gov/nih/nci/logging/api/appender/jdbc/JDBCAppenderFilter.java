/*
 * Created on Aug 24, 2004
 */
package gov.nih.nci.logging.api.appender.jdbc;

/**
 * <!-- LICENSE_TEXT_START -->
 * 
 * 
 * <!-- LICENSE_TEXT_END -->
 */
import gov.nih.nci.logging.api.appender.util.AppenderUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.spi.LoggingEvent;

/**
 * Filter for the JDBCAppender. Class is responsible for detecting messages that
 * should be filtered out.
 * 
 * @author Vijay Parmar (Ekagra Software Technologies Limited.)
 * 
 */
public class JDBCAppenderFilter
{

	private static List filterList = new ArrayList();
	static
	{
		init();
	}

	/**
	 * Returns true if the LoggingEvent's message or throwable contains any of
	 * the filter strings from the FilterList file.
	 * 
	 * @param event
	 * @return
	 */
	public static boolean isMatch(LoggingEvent event)
	{

		if (getFilterList() != null && getFilterList().size() > 0)
		{
			String msg = "";
			if (event.getMessage() != null)
			{
				msg = event.getMessage().toString();
			}
			String throwable = "";
			if (event.getThrowableInformation() != null)
			{
				throwable = AppenderUtils.getThrowable(event);
			}

			String str = msg + throwable;

			Iterator i = getFilterList().iterator();
			while (i.hasNext())
			{
				String filter = (String) i.next();
				if (str.indexOf(filter) >= 0)
				{
					return true;
				}

			}
		}
		return false;
	}

	private static void init()
	{
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(JDBCAppenderFilter.class.getResourceAsStream("FilterList")));
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				getFilterList().add(line);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch (Exception e)
			{
			}
		}

	}

	/**
	 * @return Returns the filterList.
	 */
	public static List getFilterList()
	{
		return filterList;
	}

	/**
	 * @param filterList
	 *            The filterList to set.
	 */
	public static void setFilterList(List filterList)
	{
		JDBCAppenderFilter.filterList = filterList;
	}
}