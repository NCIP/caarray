/*
 * HibernateUtil.java
 * Created on Nov 23, 2004
 */
package gov.nih.nci.logging.api.util;

import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

/**
 * 
 * HibernateUtil class provides the SessionFactory for the CLM API.
 * 
 * Refer the clm.properties file in api/resources folder for reference.
 * 
 * @author Vijay Parmar (Ekagra Software Technologies Limited.)
 *
 */
public class HibernateUtil {

	private static Logger log = Logger.getLogger(HibernateUtil.class);

	private static Properties props;

	private static SessionFactory sessionFactory;

	public static SessionFactory getSessionFactory() {
		try {
			if (sessionFactory == null) {

				/*if (props == null) {
					sessionFactory = (SessionFactory) ObjectFactory
							.getObject(ObjectFactory.HibernateSessionFactory);
				} else {*/

					sessionFactory = createSessionFactory();
				//}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sessionFactory;
	}

	/**
	 * This method creates and returns the Hibernate SessionFactory used by CLM's Query API's.
	 * 
	 * @return SessionFactory
	 * @throws Exception
	 */
	private static SessionFactory createSessionFactory() throws Exception {
		LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
		String mappingResources[] = {
				"gov/nih/nci/logging/api/domain/LogMessage.hbm.xml",
				"gov/nih/nci/logging/api/domain/ObjectAttribute.hbm.xml" };
		localSessionFactoryBean.setMappingResources(mappingResources);

		/*
		 * BasicDataSource ds = new BasicDataSource();
		 * ds.setDriverClassName(props.getProperty("CLMDS.driverClassName"));
		 * ds.setUrl(props.getProperty("CLMDS.url"));
		 * ds.setUsername(props.getProperty("CLMDS.username"));
		 * ds.setPassword(props.getProperty("CLMDS.password"));
		 * localSessionFactoryBean.setDataSource(ds);
		 */

		if(props==null){
			java.io.InputStream inputStream = FileLoader.getInstance().getFileAsStream("clm.properties");
			Properties properties = new Properties();
			if (inputStream != null) {
				try {
					properties.load(inputStream);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(!properties.isEmpty()){
				validateProperties(properties);
				props = properties;
			}
		}
			
		if(isDataSourceProperties()){
			String jndiName = props.getProperty("CLMDJndiDS.jndiName");
			Properties properties = new Properties();
			properties.setProperty("hibernate.dialect","org.hibernate.dialect.MySQLDialect");
			localSessionFactoryBean.setHibernateProperties(props);
			// set javax.sql.DataSource 
			localSessionFactoryBean.setDataSource(getDataSource(jndiName));
		}else{
			//set JDBC Connection Properties for Hibernate
			localSessionFactoryBean.setHibernateProperties(props);

		}
		

		try {
			localSessionFactoryBean.afterPropertiesSet();
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SessionFactory sf = (SessionFactory) localSessionFactoryBean.getObject();
		return sf;
	}

	
	/**
	 * @return true if clm.properties configuration specifies a Data Source.
	 * @return false if clm.properties configuration does not specify a Data Source.
	 *  
	 */
	private static boolean isDataSourceProperties() {
		
		if(props==null) return false;
		
		if (props.containsKey("CLMDJndiDS.jndiName")) {
			/*String jndiName = props.getProperty("CLMDS.jndiName");

			props.clear();
			props.setProperty("CLMDJndiDS.jndiName", jndiName);*/
			return true;
		}
		return false;
	}
	
	private static DataSource getDataSource(String jndiName) throws Exception {
		Context ctx = new InitialContext();
		if (ctx == null) {
			throw new Exception("No Context available");
		}
		return (DataSource) ctx.lookup(jndiName);
	}

	public static final ThreadLocal session = new ThreadLocal();

	public static Session currentSession() throws HibernateException {
		Session s = (Session) session.get();

		// Open a new Session, if this Thread has none yet
		if (s == null) {
			s = getSessionFactory().openSession();
			session.set(s);
		}
		return s;
	}

	public static void closeSession() throws HibernateException {
		Session s = (Session) session.get();
		session.set(null);
		if (s != null)
			s.close();
	}

	public static Properties getProperties() {
		return props;
	}

	public static void setProperties(Properties properties) {
		try {

			if (props == null) {
				props = properties;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void validateProperties(Properties properties)
			throws Exception {

		if (properties.containsKey("CLMDS.jndiName")) {
			String jndiName = properties.getProperty("CLMDS.jndiName");
			String hibernateDialect = properties.getProperty("Hibernate.dialect");

			properties.clear();
			properties.setProperty("CLMDJndiDS.jndiName", jndiName);
			properties.setProperty("hibernate.dialect", hibernateDialect);
		}
		if (properties.containsKey("CLMDS.url")
				&& properties.containsKey("CLMDS.driverClassName")
				&& properties.containsKey("CLMDS.password")
				&& properties.containsKey("CLMDS.username")) {

			String driverClassName = properties
					.getProperty("CLMDS.driverClassName");
			String url = properties.getProperty("CLMDS.url");
			String username = properties.getProperty("CLMDS.username");
			String password = properties.getProperty("CLMDS.password");
			String hibernateDialect = properties.getProperty("Hibernate.dialect");
			properties.clear();
			/*properties.setProperty("CLMConnectionPoolDS.driverClassName",driverClassName);
			properties.setProperty("CLMConnectionPoolDS.url", url);
			properties.setProperty("CLMConnectionPoolDS.username", username);
			properties.setProperty("CLMConnectionPoolDS.password", password);*/
			properties.setProperty("hibernate.connection.driver_class",driverClassName);
			properties.setProperty("hibernate.connection.url", url);
			properties.setProperty("hibernate.connection.username", username);
			properties.setProperty("hibernate.connection.password", password);
			properties.setProperty("hibernate.dialect", hibernateDialect);
			
		}

	}

	

}
