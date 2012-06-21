package test.application.clients;
/**
 * <!-- LICENSE_TEXT_START -->
 * 
 * 
 * <!-- LICENSE_TEXT_END -->
 */

import java.util.Set;

import gov.nih.nci.logging.api.logger.hibernate.HibernateSessionFactoryHelper;
import gov.nih.nci.logging.api.user.UserInfoHelper;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import test.application.domainobjects.Customer;
import test.application.domainobjects.Item;

/**
 *  Test class for the ObjectStateLogger.
 * 
 * @author Ekagra Software Technologies Limited ('Ekagra')
 * 
 */

public class ClientThread extends Thread
{
	public static Logger log = Logger.getLogger("ObjectStateLogger");
	private String x = null;
	private int customerid;
	private int itemid;
	private SessionFactory sessionFactory = null;
	

	/**
	 * @param name
	 * @param id
	 */
	public ClientThread(String name, int id, SessionFactory sessionFactory)
	{
		this.x = name;
		this.customerid = id;
		this.itemid = id;
		this.sessionFactory = sessionFactory;

	}


	public void run()
	{

		try
		{
			sleep((long) (Math.random() * 13000));
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}

		// get the Auditing session
		Session session = HibernateSessionFactoryHelper.getDefaultAuditSession();
		// set the userName and sessionID
		UserInfoHelper.setUserInfo(new String("NAME" + x), new String("sessionId" + x), new String("OrganizationA"));
		UserInfoHelper.setObjectStateChangeComment("Save Comment");
		String obectID = new String("NAME" + x);
		UserInfoHelper.setObjectID(obectID);
		// create a new customer object
		Customer customer = new Customer();
		customer.setId(customerid);
		customer.setFirst("Bill");
		customer.setLast("Burke");
		customer.setState("MA");
		customer.setCity("Newland");
		customer.setStreet("1 Boston Road");
		customer.setZip("02115");

		Item item = new Item();
		item.setId(itemid);
		item.setName("pen");
		item.setmanufacturer("Burke");
		item.setPrice(12.5f);
		item.setCustomerid(customerid);

		customer.getItems().add(item);

		org.hibernate.Transaction tx = null;
		try
		{

			tx = session.beginTransaction();
			session.save(customer);
			tx.commit();
			//UserInfoHelper.setUserInfo( "" , new String("NAMEAGAIN" + x));
			
		}
		catch (Exception e)
		{
			tx.rollback();
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		finally
		{
			session.close();
		}

		
		try{
			Set items = customer.getItems();
			Item itema = (Item) items.iterator().next();
			itema.setmanufacturer("Griffin Solutions");
			Item itemb = new Item();
			itemb.setName("New Item");
			itemb.setmanufacturer("Art Solutions");
			itemb.setCustomerid(customer.getId());
			itemb.setId(itemid + 101);
			itemb.setPrice(12.5f);

			items.add(itemb);
			session = HibernateSessionFactoryHelper.getAuditSession(sessionFactory);
			tx = session.beginTransaction();
			session.saveOrUpdate(customer);
			customer.setCity("Springfield");
			itemb.setmanufacturer("Kunal Solutions");
			tx.commit();

		}
		catch (Exception e)
		{
			tx.rollback();
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		finally
		{
			session.close();
		}

	}

}
