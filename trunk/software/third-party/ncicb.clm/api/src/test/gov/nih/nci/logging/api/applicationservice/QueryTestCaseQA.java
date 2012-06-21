package test.gov.nih.nci.logging.api.applicationservice;

import gov.nih.nci.logging.api.applicationservice.Query;
import gov.nih.nci.logging.api.applicationservice.QueryImpl;
import gov.nih.nci.logging.api.applicationservice.SearchCriteria;
import gov.nih.nci.logging.api.applicationservice.exception.QuerySpecificationException;
import gov.nih.nci.logging.api.applicationservice.exception.SearchCriteriaSpecificationException;

import java.util.Collection;

import junit.framework.TestCase;

public class QueryTestCaseQA extends TestCase {
	Query query;

	
	/**
	 * @param name
	 */
	public QueryTestCaseQA(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		query = new QueryImpl();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSetCriteriaNull(){
		assertNotNull(query);
		try {
			query.setCriteria(null);
			assertTrue(false);
		} catch (QuerySpecificationException e) {
			assertTrue(true);
		}
		catch (SearchCriteriaSpecificationException e)
		{
			assertTrue(true);
		}
	}
	public void testSetCriteriaNotNull(){
		assertNotNull(query);
		try {
			query.setCriteria(getSearchCriteriaThatGivesResults());
			assertTrue(true);
		} catch (QuerySpecificationException e) {
			assertTrue(false);
		}
		catch (SearchCriteriaSpecificationException e)
		{
			assertTrue(false);
		}
	}
	public void testEstimatedResultSize_NoResults(){
		assertNotNull(query);
		try {
			query.setCriteria(getSearchCriteriaThatGivesNoResults());
			int size = query.totalResultSize();
			assertTrue("Estimated Result Size should be zero", size== 0);
		} catch (QuerySpecificationException e) {
			assertTrue(false);
		}
		catch (SearchCriteriaSpecificationException e)
		{
			assertTrue(false);
		}
	}
	public void testEstimatedResultSize_WithResults(){
		assertNotNull(query);
		try {
			query.setCriteria(getSearchCriteriaThatGivesResults());
			int size = query.totalResultSize();
			assertTrue("Estimated Result Size should be greater than zero", size!= 0);
		} catch (QuerySpecificationException e) {
			assertTrue(false);
		}
		catch (SearchCriteriaSpecificationException e)
		{
			assertTrue(false);
		}
	}
	public void testQuery_NoResults(){
		assertNotNull(query);
		try {
			query.setCriteria(getSearchCriteriaThatGivesNoResults());
			Collection collection = query.query();
			assertTrue("Expected Result Collection should  be null.", collection==null);
		} catch (QuerySpecificationException e) {
			assertTrue(false);
		}
		catch (SearchCriteriaSpecificationException e)
		{
			assertTrue(false);
		}
	}	
	public void testQuery_WithResults(){
		//assertNotNull(query);
		try {
			Query query1 = new QueryImpl();
			query1.setCriteria(getSearchCriteriaThatGivesResults());
			Collection collection = query1.query();
			assertTrue("Expected Result Collection should  Not be null.", collection!=null);
			assertTrue("Expected Result Collection Size should be greater than Zero.", collection.size()!= 0);
		} catch (QuerySpecificationException e) {
			assertTrue(false);
		}
		catch (SearchCriteriaSpecificationException e)
		{
			assertTrue(false);
		}
	}
	public void testQuery_MaxSize_NoResults(){
		assertNotNull(query);
		try {
			query.setCriteria(getSearchCriteriaThatGivesNoResults());
			Collection collection = query.query(50);
			assertTrue("Expected Result Collection should  be null.", collection==null);
		} catch (QuerySpecificationException e) {
			assertTrue(false);
		}
		catch (SearchCriteriaSpecificationException e)
		{
			assertTrue(false);
		}
	}	
	public void testQuery_MaxSize_WithResults(){
		//assertNotNull(query);
		try {
			Query query1 = new QueryImpl();
			query1.setCriteria(getSearchCriteriaThatGivesResults());
			Collection collection = query1.query(25);
			assertTrue("Expected Result Collection should  Not be null.", collection!=null);
			assertTrue("Expected Result Collection Size should be greater than Zero.", collection.size()!= 0);
		} catch (QuerySpecificationException e) {
			assertTrue(false);
		}
		catch (SearchCriteriaSpecificationException e)
		{
			assertTrue(false);
		}
	}
	public void testQuery_OffSet_0_RecordCount_0(){
		assertNotNull(query);
		try {
			query.setCriteria(getSearchCriteriaThatGivesResults());
			Collection collection = query.query(0,0);
			assertTrue(false);
		} catch (QuerySpecificationException e) {
			assertTrue(true);
		}
		catch (SearchCriteriaSpecificationException e)
		{
			assertTrue(false);
		}
	}
	public void testQuery_OffSet_Not0_RecordCount0(){
		assertNotNull(query);
		try {
			query.setCriteria(getSearchCriteriaThatGivesResults());
			Collection collection = query.query(10,0);
			assertTrue(false);
		} catch (QuerySpecificationException e) {
			assertTrue(true);
		}
		catch (SearchCriteriaSpecificationException e)
		{
			assertTrue(false);
		}
	}
	public void testQuery_OffSet_0_RecordCount_Not0(){
		assertNotNull(query);
		try {
			query.setCriteria(getSearchCriteriaThatGivesResults());
			Collection collection = query.query(0,10);
			assertTrue(false);
		} catch (QuerySpecificationException e) {
			assertTrue(true);
		}
		catch (SearchCriteriaSpecificationException e)
		{
			assertTrue(false);
		}
	}
	public void testQuery_OffSet_Not0_RecordCount_Not0_NoResults(){
		assertNotNull(query);
		try {
			query.setCriteria(getSearchCriteriaThatGivesNoResults());
			Collection collection = query.query(1,10);
			assertTrue("Expected Result Collection should  be null.", collection==null);
		} catch (QuerySpecificationException e) {
			assertTrue(false);
		}
		catch (SearchCriteriaSpecificationException e)
		{
			assertTrue(false);
		}
	}
	public void testQuery_OffSet_Not0_RecordCount_Not0_WithResults(){
		assertNotNull(query);
		try {
			query.setCriteria(getSearchCriteriaThatGivesResults());
			Collection collection = query.query(1,10);
			assertTrue("Expected Result Collection should  Not be null.", collection!=null);
			assertTrue("Expected Result Collection Size should be greater than Zero.", collection.size()!= 0);
		} catch (QuerySpecificationException e) {
			assertTrue(false);
		}
		catch (SearchCriteriaSpecificationException e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}
    
	private SearchCriteria getSearchCriteriaThatGivesResults() {
		SearchCriteria searchCriteria = new SearchCriteria();
		searchCriteria.setApplication("test");
		searchCriteria.setLogLevel("WARN");
		searchCriteria.setObjectID("Bill Burke");
		String ll ="1155756542578";
		
		java.util.Date d = new java.util.Date();
		long l = new Long(ll).longValue();
		d.setTime(l);
		d.toString();
		
		
		searchCriteria.setStartDate("01/01/2005");
		
		searchCriteria.setStartTime("00:00 AM");
		searchCriteria.setEndDate("10/24/2006");
		searchCriteria.setEndTime("00:00 AM");
		searchCriteria.setObjectID("Bill Burke");
//		searchCriteria.setObjectName("test.application.domainobjects.Customer");
		//searchCriteria.setOrganization("OrganizationA");
		searchCriteria.addAscendingSortOrderFor(SearchCriteria.SORT_BY_PARAMETER_APPLICATION);
		searchCriteria.addAscendingSortOrderFor(SearchCriteria.SORT_BY_PARAMETER_USERNAME);
		return searchCriteria;
	}
	private SearchCriteria getSearchCriteriaThatGivesNoResults(){
		SearchCriteria searchCriteria = new SearchCriteria();
		searchCriteria.setApplication("ZZZZZZZZZZZZZZZZZ");
		searchCriteria.setLogLevel("DEBUG");
		searchCriteria.setStartDate("01/01/2006");
		searchCriteria.setStartTime("00:00 AM");
		searchCriteria.setEndDate("08/01/2006");
		searchCriteria.setEndTime("00:00 AM");
		searchCriteria.addAscendingSortOrderFor(SearchCriteria.SORT_BY_PARAMETER_APPLICATION);
		searchCriteria.addAscendingSortOrderFor(SearchCriteria.SORT_BY_PARAMETER_USERNAME);

		return searchCriteria;
	}
	
	  public void testRetrieveServer(){
		  assertNotNull(query);
			try {
				Collection collection = query.retrieveServer();
				assertTrue("Expected Result Collection Size should be greater than Zero.", collection.size()!= 0);
			} catch (QuerySpecificationException e) {
				assertTrue(false);
			}

	    }

	  public void testToXML(){
			//assertNotNull(query);
			try {
				Query query1 = new QueryImpl();
				query1.setCriteria(getSearchCriteriaThatGivesResults());
				Collection collection = query1.query(25);
				
				System.out.println(query1.toXML(collection));
				
				assertTrue("Expected Result Collection should  Not be null.", collection!=null);
				
				
				assertTrue("Expected Result Collection Size should be greater than Zero.", collection.size()!= 0);
				
			} catch (QuerySpecificationException e) {
				assertTrue(false);
			}
			catch (SearchCriteriaSpecificationException e)
			{
				assertTrue(false);
			}
		}
	  
	
}

