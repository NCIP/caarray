package gov.nih.nci.logging.api.persistence;

import gov.nih.nci.logging.api.applicationservice.Constants;
import gov.nih.nci.logging.api.applicationservice.SearchCriteria;
import gov.nih.nci.logging.api.applicationservice.exception.SearchCriteriaSpecificationException;

import java.util.Collection;

import org.springframework.dao.DataAccessException;

/**
 * LogMessageDAO retrieves LogMessages from the Log Storage. <br>
 * LogMessages can be retrieved based on the provided Search Criteria. 
 * 
 * @author Vijay Parmar (Ekagra Software Technologies Limited.)
 * 
 */
public interface LogMessageDAO extends Constants {
	


	/**
	 * Retrieves a Collection of LogMessages based the provided SearchCriteria
	 * Object.
	 * 
	 * @param searchCriteria
	 * @return Collection of LogMessage objects
	 * @throws SearchCriteriaSpecificationException
	 */
	Collection retrieve(final SearchCriteria searchCriteria)
			throws SearchCriteriaSpecificationException;

	/**
	 * Retrieves a Collection of LogMessages based on the provided
	 * SearchCriteria Object with max result size of 'maxSize'
	 * 
	 * @param searchCriteria
	 * @param maxSize
	 * @return Collection
	 * @throws SearchCriteriaSpecificationException
	 */
	Collection retrieve(final SearchCriteria searchCriteria,final int maxSize)
			throws SearchCriteriaSpecificationException,DataAccessException;

	/**
	 * This method queries based on search criteria. It allows pagination
	 * features hence it queries for results starting at 'currentStartOffSet'
	 * and returns 'currentRecordCount' records in the logMessageCollection. It
	 * also gives the totalRecordSize of the query result.
	 * 
	 * 
	 * @param searchCriteria
	 * @param currentStartOffSet
	 * @param currentRecordCount
	 * @param logMessageCollection
	 * @param totalRecordSize
	 *            it can be used in the UI to represent total number of records
	 *            or pages.
	 * @throws SearchCriteriaSpecificationException
	 */
	Collection retrieve(final SearchCriteria searchCriteria,
			final int currentStartOffSet,
			final int currentRecordCount)
			throws SearchCriteriaSpecificationException,DataAccessException;
	
	
	/**
	 *  This method returns total number of records based on a Search Criteria.
	 * 
	 * @return Query Result size for the Search Criteria
	 */
	int estimateResultSize(final SearchCriteria searchCriteria);
	
	/**
	 * This method retrieves as a collection a list of Server's whose log entries are available in the CLM database.
	 * @return
	 * @throws DataAccessException
	 */
	Collection retrieveServer() throws DataAccessException;

	/**
	 * @param class1
	 * @param long1
	 * @return
	 */
	Object retrieveObjectById(Class class1, Long long1);
	
	/**
	 * Saves the LogMessage object.
	 * @param logmessage
	 */
	void save(Object logmessage);

	/**
	 * 
	 * For Debug and Junit purpose only. 
	 * There is no usecase for this operation/method.
	 * @param logmessage
	 */
	void deleteObject(Object logmessage);

	

}
