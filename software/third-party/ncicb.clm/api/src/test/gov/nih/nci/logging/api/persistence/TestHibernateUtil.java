package test.gov.nih.nci.logging.api.persistence;

import gov.nih.nci.logging.api.util.HibernateUtil;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

public class TestHibernateUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try{
		String queryStr = "select distinct log.server from gov.nih.nci.logging.api.domain.LogMessage log where log.server is not null";
		
		Session session = HibernateUtil.currentSession();
		
		Query query = session.createQuery(queryStr);		
		List results = query.list();
		System.out.println(results.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
