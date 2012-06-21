package gov.nih.nci.logging.api.applicationservice;

import gov.nih.nci.logging.api.applicationservice.exception.QuerySpecificationException;
import gov.nih.nci.logging.api.applicationservice.exception.SearchCriteriaSpecificationException;

import java.util.Collection;

/**
 * 
 * Query API 
 * 
 * 
 * Query interface to define the operations available to query the Log Messages.
 * 
 * @author parmarv
 *
 */
public interface Query extends Constants {

	/**
	 * Specifies the SearchCriteria for the Query.
	 * 
	 * @param searchCriteria
	 * @throws QuerySpecificationException 
	 * @throws SearchCriteriaSpecificationException 
	 */
	void setCriteria(final SearchCriteria searchCriteria) throws QuerySpecificationException, SearchCriteriaSpecificationException;
	
	/**
	 * Query based on search criteria. Returns all results.
	 * 
	 * @param searchCriteria
	 * @param totalRecordSize
	 * @return Collection of all results.
	 */
	Collection query() throws QuerySpecificationException;

	/**
	 * Query based on search criteria for maximum of 'maxSize'.
	 * 
	 * 
	 * @param maxSize
	 *            Maximum desired result size
	 * @return Collection of size not greater than 'maxSize'.@return
	 * @throws QuerySpecificationException;
	 */
	Collection query(final int maxSize) throws QuerySpecificationException;

	/**
	 * Query based on search criteria. This method is getting the result set to
	 * assist pagination.
	 * 
	 * @param currentStartOffSet
	 *            specifies the Start offset of the result.
	 * @param currentRecordCount
	 *            specifies the Record Count to be populated as a result of the
	 *            query starting from currentStartOffset
	 * 
	 * @return logMessageCollection
	 *            Populated collection with the result.
	 * @throws QuerySpecificationException
	 */
	Collection query(final int currentStartOffSet, final int currentRecordCount)
			throws QuerySpecificationException;
	
	
	
	/**
	 * This method returns total number of records based on a Search Criteria.
	 * This method is getting the max result count to assist pagination.
	 * 
	 * @return Query Result size for the Search Criteria
	 * @throws QuerySpecificationException 
	 */
	int totalResultSize() throws QuerySpecificationException;

	/**
	 * This method returns a collection of server names available in the log message database..
	 * 
	 * @return Collection of names of server who have log message available in the database. 
	 * @throws QuerySpecificationException 
	 */
	Collection retrieveServer() throws QuerySpecificationException;

	/**
	 * Returns a XML representation of Ojbects extending LogMessage.
	 * <br><br>Example:
	 *<br>&lt;LogMessages&gt;
	 * <br>&nbsp;&nbsp;		&lt;LogMessage&gt;
	 * <br>&nbsp;&nbsp;		&lt;/LogMessage&gt;
	 * <br>&nbsp;&nbsp;		&lt;LogMessage&gt;
	 * <br>&nbsp;&nbsp;		&lt;/LogMessage&gt;
	 * <br>&nbsp;&nbsp;		.....
	 * <br>&lt;/LogMessages&gt;
	 * 
	 * 
	 * @param searchCriteria
	 * @return XML representation of LogMessageCollection
	 */
	String toXML(Collection logMessageCollection);
}
