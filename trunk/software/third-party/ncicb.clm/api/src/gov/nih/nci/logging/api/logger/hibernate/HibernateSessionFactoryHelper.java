package gov.nih.nci.logging.api.logger.hibernate;
/**
 * <!-- LICENSE_TEXT_START -->
 * 
 * <!-- LICENSE_TEXT_END -->
 */

import gov.nih.nci.logging.api.util.ObjectFactory;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * 
 * This class provides the utility to obtain a Hibernate session for auditing
 * 
 * @author Vijay Parmar (Ekagra Software Technologies Limited.)
 * 
 */

public class HibernateSessionFactoryHelper
{

	
	private static SessionFactory sessionFactory;
    /**
     * Private Constructor to aviod intialization
     */
    private HibernateSessionFactoryHelper()
    {
    }

	/**
	 * @param sessionFactory 
	 * @return - Returns a Hibernate session with audit interceptor from the
	 * passed in session factory
	 * @throws HibernateException
	 */
	public static Session getAuditSession(SessionFactory sessionFactory) throws HibernateException
	{
		return sessionFactory.openSession(new ObjectStateInterceptor());
	}

	/**
	 * @return - Returns a Hibernate session with audit interceptor using
	 * the default session factory created from the <code>hibernate.cfg.xml</code> file
	 * @throws HibernateException
	 */
	public static Session getDefaultAuditSession() throws HibernateException
	{
	   Configuration cfg = new Configuration();
	   return cfg.configure().buildSessionFactory().openSession(new ObjectStateInterceptor());
	
	}
	
}
