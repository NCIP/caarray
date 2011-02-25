package gov.nih.nci.logging.api.persistence;

import gov.nih.nci.logging.api.applicationservice.SearchCriteria;
import gov.nih.nci.logging.api.domain.LogMessage;
import gov.nih.nci.logging.api.logger.util.ApplicationConstants;
import gov.nih.nci.logging.api.util.HibernateUtil;
import gov.nih.nci.logging.api.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author Vijay Parmar (Ekagra Software Technologies Limited.)
 * 
 */

public class LogMessageDAOImpl extends HibernateDaoSupport implements
		LogMessageDAO {

	/* (non-Javadoc)
	 * @see gov.nih.nci.logging.api.persistence.LogMessageDAO#retrieve(gov.nih.nci.logging.api.applicationservice.SearchCriteria, int)
	 */
	public Collection retrieve(
			final SearchCriteria searchCriteria,
			final int maxSize) {
		
		Session session = HibernateUtil.currentSession();
		
		Criteria criteria = session.createCriteria(LogMessage.class);
		
		populateCriteria(searchCriteria, criteria);
		
		// set Maximum result size
		criteria.setMaxResults(maxSize);
		// get results.
		Collection logMessageCollection = criteria.list();
		
		if(logMessageCollection.size()>0) return logMessageCollection;
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.logging.api.persistence.LogMessageDAO#retrieve(gov.nih.nci.logging.api.applicationservice.SearchCriteria, int, int, java.util.Collection, int)
	 */
	public Collection retrieve(
			final SearchCriteria searchCriteria,
			final int currentStartOffSet,
			final int currentRecordCount) {

		Session session = HibernateUtil.currentSession();
		
		Criteria criteria = session.createCriteria(LogMessage.class);
		
		populateCriteria(searchCriteria, criteria);
		
		// set Start Off set.
		criteria.setFirstResult(currentStartOffSet-1);
		// Set Number of Records
		criteria.setMaxResults(currentRecordCount);
		
		// get Results.
		Collection logMessageCollection= criteria.list();
		if(logMessageCollection.size()>0) return logMessageCollection;
		return null;
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nih.nci.logging.api.persistence.LogMessageDAO#retrieve(gov.nih.nci.logging.api.domain.SearchCriteria)
	 */

	public Collection retrieve(SearchCriteria searchCriteria) {
		
		Session session = HibernateUtil.currentSession();
		
		Criteria criteria = session.createCriteria(LogMessage.class);
		populateCriteria(searchCriteria, criteria);
		List results = criteria.list();
		if(results.size()>0) return results;
		return null;

	}
	
	/* (non-Javadoc)
	 * @see gov.nih.nci.logging.api.persistence.LogMessageDAO#retrieveServer()
	 */
	public Collection retrieveServer(){
		String queryStr = "select distinct log.server from gov.nih.nci.logging.api.domain.LogMessage log where log.server is not null";
		
		Session session = HibernateUtil.currentSession();
		
		Query query = session.createQuery(queryStr);		
		List results = query.list();
		
		return (Collection) results;
	}
	
	/* (non-Javadoc)
	 * @see gov.nih.nci.logging.api.persistence.LogMessageDAO#estimateResultSize(gov.nih.nci.logging.api.applicationservice.SearchCriteria)
	 */
	public int estimateResultSize(SearchCriteria searchCriteria) {
		
		
		Session session = HibernateUtil.currentSession();
		
		Criteria criteria = session.createCriteria(LogMessage.class);
		
		populateCriteria(searchCriteria, criteria);
		
		
		criteria.setProjection( Projections.rowCount());
		
		List results = criteria.list();
		Integer integer = (Integer) results.iterator().next();
		if(integer!=null){
			return integer.intValue();	
		}else{
			return 0;
		}
	}

	
	/**
	 * Based on SearchCriteria object populate the Hibernates Criteria Object with Expression and sort order details.
	 * @param searchCriteria
	 * @param criteria
	 */
	private void populateCriteria(SearchCriteria searchCriteria, Criteria criteria) {
		
		criteria.add(createExpressionForDate(searchCriteria));
		
		if (!StringUtils.isBlank(searchCriteria.getApplication())) {
			criteria.add(Expression.eq(_APPLICATION, searchCriteria.getApplication().trim()));
		}
		if (!StringUtils.isBlank(searchCriteria.getLogLevel())) {
			criteria.add(Expression.eq(_LOG_LEVEL, searchCriteria.getLogLevel().trim()));
		}
		if (!StringUtils.isBlank(searchCriteria.getMessage())) {
			criteria.add(Expression.like(_MESSAGE, "%"+searchCriteria.getMessage()+"%"));
		}
		if (!StringUtils.isBlank(searchCriteria.getNdc())) {
			criteria.add(Expression.like(_NDC, "%"+searchCriteria.getNdc()+"%"));
		}
		if (!StringUtils.isBlank(searchCriteria.getObjectID())) {
			criteria.add(Expression.eq(_OBJECT_ID, searchCriteria.getObjectID().trim()));
		}
		if (!StringUtils.isBlank(searchCriteria.getObjectName())) {
			criteria.add(Expression.eq(_OBJECT_NAME, searchCriteria.getObjectName().trim()));
		}
		if (!StringUtils.isBlank(searchCriteria.getOperation())) {
			criteria.add(Expression.eq(_OPERATION, searchCriteria.getOperation().trim()));
		}
		if (!StringUtils.isBlank(searchCriteria.getOrganization())) {
			criteria.add(Expression.like(_ORGANIZATION, "%"+searchCriteria.getOrganization()+"%"));
		}
		if (!StringUtils.isBlank(searchCriteria.getServer())) {
			criteria.add(Expression.eq(_SERVER, searchCriteria.getServer().trim()));
		}
		if (!StringUtils.isBlank(searchCriteria.getSessionID())) {
			criteria.add(Expression.eq(_SESSION_ID, searchCriteria.getSessionID().trim()));
		}
		if (!StringUtils.isBlank(searchCriteria.getThrowable())) {
			criteria.add(Expression.eq(_THROWABLE, searchCriteria.getThrowable().trim()));
		}
		if (!StringUtils.isBlank(searchCriteria.getThreadName())) {
			criteria.add(Expression.like(_THREAD, "%"+searchCriteria.getThreadName()+"%"));
		}
		if (!StringUtils.isBlank(searchCriteria.getUserName())) {
			criteria.add(Expression.eq(_USERNAME, searchCriteria.getUserName().trim()));
		}

		// Sort By criteria.
		LinkedHashMap lhp = (LinkedHashMap) searchCriteria.getSortByOrderSequence();
		if(lhp!=null){
			Iterator iter = lhp.keySet().iterator();
			while(iter.hasNext()){
				 String key= (String) iter.next();
				 String value = (String) lhp.get(key);
				 if(SORT_ORDER_ASCENDING.equalsIgnoreCase(value)){
					 criteria.addOrder(Order.asc(key));
				 }
				 if(SORT_ORDER_DESCENDING.equalsIgnoreCase(value)){
					 criteria.addOrder(Order.desc(key));
				 }
			}
		}	
	}



	/**
	 * Based on start/end date and time returns a Criterion.
	 * @param searchCriteria
	 * @return Criterion
	 */
	private Criterion createExpressionForDate(SearchCriteria searchCriteria) {
		
		
		if(!StringUtils.isBlank(searchCriteria.getStartDate()) && !StringUtils.isBlank(searchCriteria.getEndDate()) ){
			//Expression.between
			Long start= getDateTime(searchCriteria.getStartDate(),searchCriteria.getStartTime(),true);
			Long end= getDateTime(searchCriteria.getEndDate(),searchCriteria.getEndTime(),false);
			return Expression.between(_DATE,start,end);	
		}
		if(!StringUtils.isBlank(searchCriteria.getStartDate()) && StringUtils.isBlank(searchCriteria.getEndDate()) ){
			//Expression.ge		
			Long start= getDateTime(searchCriteria.getStartDate(),searchCriteria.getStartTime(),true);
			return Expression.ge(_DATE,start);
		}
		
		Long end = getDateTime(searchCriteria.getEndDate(),searchCriteria.getEndTime(),false);
		return Expression.le(_DATE,end);
	}

	/**
	 * Returns the Data and Time in milliseconds (Long).
	 * 
	 * @param date
	 * @param time
	 * @param isStart
	 * @return Date and Time in milliseconds
	 */
	private Long getDateTime(String date, String time, boolean isStart) {
		
		final SimpleDateFormat sdf = new SimpleDateFormat(ApplicationConstants.DISPLAY_DATE_FORMAT);
		
		Long datetime=null;
		
		if(isStart){
			if(!StringUtils.isBlank(date)){
				if(StringUtils.isBlank(time)){
						time = "00:00 AM";
				}
				date += " , " + time;
				try {
					datetime= new Long(sdf.parse(date).getTime());
				} catch (ParseException e) {
					datetime= new Long(System.currentTimeMillis());
				}
			}else{
				datetime= new Long(System.currentTimeMillis());
			}
		}else{
			if(!StringUtils.isBlank(date)){
				if(StringUtils.isBlank(time)){
					time = "11:59 PM";
				}
				date += " , " + time;
				try {
					datetime= new Long(sdf.parse(date).getTime());
				} catch (ParseException e) {
					datetime= new Long(System.currentTimeMillis());
				}
			}else{
				datetime= new Long(System.currentTimeMillis());
			}
		}
		return datetime;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.logging.api.persistence.LogMessageDAO#save(java.lang.Object)
	 */
	public void save(Object logmessage) {
		getHibernateTemplate().save(logmessage);
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.logging.api.persistence.LogMessageDAO#retrieveObjectById(java.lang.Class, java.lang.Long)
	 */
	public Object retrieveObjectById(Class class1, Long id) {
		return getHibernateTemplate().get(class1, id);
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.logging.api.persistence.LogMessageDAO#deleteObject(java.lang.Object)
	 */
	public void deleteObject(Object logmessage) {
		getHibernateTemplate().delete(logmessage);
		
	}

	
	


	
}
