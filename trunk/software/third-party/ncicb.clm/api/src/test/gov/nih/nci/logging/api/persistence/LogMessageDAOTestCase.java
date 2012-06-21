package test.gov.nih.nci.logging.api.persistence;

import gov.nih.nci.logging.api.applicationservice.SearchCriteria;
import gov.nih.nci.logging.api.applicationservice.exception.SearchCriteriaSpecificationException;
import gov.nih.nci.logging.api.domain.LogMessage;
import gov.nih.nci.logging.api.domain.ObjectAttribute;
import gov.nih.nci.logging.api.persistence.LogMessageDAO;
import gov.nih.nci.logging.api.util.ObjectFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.TestCase;

public class LogMessageDAOTestCase extends TestCase
{

	private LogMessageDAO logMessageDAO = (LogMessageDAO) ObjectFactory.getObject(ObjectFactory.LogMessageDAO);;

	/**
	 * @param name
	 */
	public LogMessageDAOTestCase(String name)
	{
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		logMessageDAO = (LogMessageDAO) ObjectFactory.getObject(ObjectFactory.LogMessageDAO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testSave()
	{
		assertNotNull(logMessageDAO);

		/*
		 * Object logmessage = createEventLogMessage();
		 * logMessageDAO.save(logmessage); assertTrue("Log Message saved",
		 * logmessage!=null);
		 */
		Object logmessage2 = createObjectStateLogMessage();
		logMessageDAO.save(logmessage2);
		assertTrue("Log Message saved", logmessage2 != null);

	}

	private LogMessage createEventLogMessage()
	{
		LogMessage logmessage = new LogMessage();
		logmessage.setApplication("App");
		logmessage.setMessage("Message");
		java.util.Date d = new java.util.Date();
		d.setTime(new Long(System.currentTimeMillis()).longValue());
		logmessage.setCreatedDate(d);
		return logmessage;
	}

	private LogMessage createObjectStateLogMessage()
	{

		LogMessage logmessage = new LogMessage();

		logmessage.setApplication("App");
		logmessage.setMessage("Message");
		java.util.Date d = new java.util.Date();
		d.setTime(new Long(System.currentTimeMillis()).longValue());
		logmessage.setCreatedDate(d);

		ObjectAttribute oa = new ObjectAttribute();
		oa.setAttributeName("Test1");
		oa.setPreviousValue("Previuos Test1");
		logmessage.addObjectAttribute(oa);
		ObjectAttribute oa2 = new ObjectAttribute();
		oa2.setAttributeName("Test2");
		oa2.setPreviousValue("Previuos Test2");
		logmessage.addObjectAttribute(oa2);

		return logmessage;
	}

	public void testRetrieveByID()
	{
		try{
		logMessageDAO = (LogMessageDAO) ObjectFactory.getObject(ObjectFactory.LogMessageDAO);
		assertNotNull(logMessageDAO);

		LogMessage logmessage = null;
		logmessage = (LogMessage) logMessageDAO.retrieveObjectById(LogMessage.class, new Long(1083));

		LogMessage oslm = (LogMessage) logmessage;
		// System.out.println("Object State Log.");
		System.out.println(oslm.getMessage());
		System.out.println(oslm.getObjectID());
		Iterator it = oslm.getObjectAttributeSet().iterator();
		while (it.hasNext())
		{
			ObjectAttribute oa = (ObjectAttribute) it.next();
			// System.out.println(" Attribute : "+oa.getAttribute()+", Previous
			// Value : "+oa.getPreviousValue()+", Next Value :
			// "+oa.getNextValue());
		}

		Object eventlogmessage = null;
		eventlogmessage = logMessageDAO.retrieveObjectById(LogMessage.class, new Long(3714));
		assertNotNull(eventlogmessage);

		if (logmessage.getClass().getName() == LogMessage.class.getName())
		{
			LogMessage elm = (LogMessage) logmessage;
			// System.out.println("Event Log.");
			System.out.println(elm.getMessage());
		}
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	public void testEstimateResultSize()
	{
		logMessageDAO = (LogMessageDAO) ObjectFactory.getObject(ObjectFactory.LogMessageDAO);
		assertNotNull(logMessageDAO);

		SearchCriteria searchCriteria = getSearchCriteriaThatGivesResults();
		Collection collection = null;
		int resultSize = logMessageDAO.estimateResultSize(searchCriteria);
		assertTrue("Esimated Result Size should be greater than zero.", resultSize != 0);

	}

	public void testRetrieveLogMessage()
	{
		logMessageDAO = (LogMessageDAO) ObjectFactory.getObject(ObjectFactory.LogMessageDAO);
		assertNotNull(logMessageDAO);

		SearchCriteria searchCriteria = getSearchCriteriaThatGivesResults();
		Collection collection = null;
		try
		{
			collection = logMessageDAO.retrieve(searchCriteria);
			assertTrue("No Log Messages Retrieved.", collection != null);
			assertTrue("No Log Messages Retrieved.", collection.size() != 0);
		}
		catch (SearchCriteriaSpecificationException e)
		{
			assertTrue(false);
		}
		if (collection != null)
		{
			Iterator iter = collection.iterator();
			while (iter.hasNext())
			{
				LogMessage logmessage = (LogMessage) iter.next();
				
				
					LogMessage lm = (LogMessage) logmessage;
					// System.out.println("Event Log Message:
					// "+elm.getMessage());
					if(lm.isObjectStateLog()){
						System.out.println(lm.getObjectID());
					}
			}
		}
	}

	public void testRetrieveLogMessagePaged()
	{
		logMessageDAO = (LogMessageDAO) ObjectFactory.getObject(ObjectFactory.LogMessageDAO);
		assertNotNull(logMessageDAO);

		SearchCriteria searchCriteria = getSearchCriteriaThatGivesResults();
		Collection collection = null;
		int resultSize = logMessageDAO.estimateResultSize(searchCriteria);
		assertTrue("Esimated Result Size should be greater than zero.", resultSize != 0);

		SortedSet ss = new TreeSet();
		try
		{
			for (int i = 0; i < 10; i++)
			{

				collection = logMessageDAO.retrieve(searchCriteria, i * 10, 10);
				assertTrue("No Log Messages Retrieved.", collection != null);
				Iterator iter = collection.iterator();
				while (iter.hasNext())
				{
					LogMessage lm = (LogMessage) iter.next();
					System.out.print(lm.getId() + ", ");
					ss.add(new Long(lm.getId()));
				}
				System.out.println("");
			}

			
			collection = logMessageDAO.retrieve(searchCriteria, 11, 10);
			assertTrue("No Log Messages Retrieved.", collection != null);

			assertTrue("No Log Messages Retrieved.", collection.size() != 0);
		}
		catch (SearchCriteriaSpecificationException e)
		{
			assertTrue(false);
		}

	}

	public void testRetrieveServer()
	{
		logMessageDAO = (LogMessageDAO) ObjectFactory.getObject(ObjectFactory.LogMessageDAO);
		assertNotNull(logMessageDAO);

		Collection collection = logMessageDAO.retrieveServer();
		assertTrue("Collection should not be null.", collection != null);

	}

	private SearchCriteria getSearchCriteriaThatGivesResults()
	{
		SearchCriteria searchCriteria = new SearchCriteria();
		searchCriteria.setApplication("csm");

		String ll = "1155756542578";

		java.util.Date d = new java.util.Date();
		long l = new Long(ll).longValue();
		d.setTime(l);
		d.toString();

		//searchCriteria.setStartDate("01/01/2005");
		//searchCriteria.setStartTime("00:00 AM");
	//	searchCriteria.setEndDate("09/15/2006");
	//	searchCriteria.setEndTime("07:00 PM");
		//searchCriteria.addAscendingSortOrderFor(SearchCriteria.SORT_BY_PARAMETER_APPLICATION);
		//searchCriteria.addAscendingSortOrderFor(SearchCriteria.SORT_BY_PARAMETER_USERNAME);
		return searchCriteria;
	}

	private SearchCriteria getSearchCriteriaThatGivesNoResults()
	{
		SearchCriteria searchCriteria = new SearchCriteria();
		searchCriteria.setApplication("ZZZZZZZZZZZZZZZZZZZ");
		return searchCriteria;
	}

}
